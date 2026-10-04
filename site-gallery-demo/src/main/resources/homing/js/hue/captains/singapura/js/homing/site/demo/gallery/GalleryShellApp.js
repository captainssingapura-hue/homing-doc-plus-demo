// =============================================================================
// GalleryShellApp — the gallery as a shell. A split grid of three cells,
// arranged once and never re-arranged, fills the slot: the navigator on the
// left, the chosen demo top right, its explanation under it — the dividers
// drag, nothing subdivides or goes. The navigator is the relation tree over DEMOS —
// the demos in their groups, the sub-catalogues, folders open from the start; the
// demo is the demo app's widget class, imported by its module's URL — the page's
// own instance — when first chosen and kept in a slot after; the explanation is what DEMOS
// says, with a link to the page the demo also is; a group chosen is described
// and shows its first demo. The address follows. The
// shell is one document under the chrome, which made the one keyboard steward
// and handed it in the params; the shell hands it on to every demo in theirs.
// =============================================================================

const _owner = Object.freeze({ toString: () => "galleryShell" });
var _loaded = new Map();   // module url → Promise<class>

function _load(entry) {
    var url = entry.module;
    var p = _loaded.get(url);
    if (!p) {
        p = import(url).then(function (m) {
            var Widget = m[entry.export];
            if (typeof Widget !== "function") throw new Error("[galleryShell] " + entry.module + " exports no class " + entry.export);
            return Widget;
        });
        _loaded.set(url, p);
    }
    return p;
}

function appMain(el, params) {
    var branch = domOpsParty.createBranch("galleryShell");
    branch.activate(_owner);
    css.addClass(el, mpa_main_full);

    var shell = branch.createElement("shell", "div");
    css.addClass(shell, ga_shell);
    el.appendChild(shell);

    var grid = new SplitGrid(branch.createBranch("grid"), {
        host: shell, minCellPx: 160,
        layout: { kind: "split", orientation: "horizontal", children: [
            { node: { kind: "cell", id: "nav" }, ratio: 1 },
            { node: { kind: "split", orientation: "vertical", children: [
                { node: { kind: "cell", id: "demo" }, ratio: 5 },
                { node: { kind: "cell", id: "explain" }, ratio: 3 } ] }, ratio: 4 } ] }
    });

    var navHost = branch.createElement("navHost", "div");
    css.addClass(navHost, ga_shell_nav);
    grid.cell("nav").appendChild(navHost);

    var demoHost = branch.createElement("demoHost", "div");
    css.addClass(demoHost, ga_shell_demo);
    grid.cell("demo").appendChild(demoHost);

    var explain = branch.createElement("explain", "div");
    css.addClass(explain, ga_shell_explain);
    grid.cell("explain").appendChild(explain);
    var kicker = branch.createElement("kicker", "div");
    css.addClass(kicker, ga_kicker);
    var title = branch.createElement("title", "h2");
    css.addClass(title, ga_title);
    var summary = branch.createElement("summary", "p");
    css.addClass(summary, ga_lede);
    var text = branch.createElement("text", "p");
    css.addClass(text, ga_explain_text);
    var link = branch.createElement("link", "a");
    css.addClass(link, ga_explain_link);
    explain.appendChild(kicker);
    explain.appendChild(title);
    explain.appendChild(summary);
    explain.appendChild(text);
    explain.appendChild(link);

    var keyboard = params && params.keyboard;
    if (!keyboard) throw new Error("[galleryShell] the page's keyboard steward is required: params.keyboard");
    // one document, one menu steward: the kinds are the site's, derived from every page's needs; the demos take it in their params
    var menus = new ContextMenuSteward(branch.createBranch("menus"), { types: MENUS, keyboard: keyboard, keyboardId: "shell/menus" });
    var navSlot  = new WidgetSlot({ branch: branch, host: navHost });
    var demoSlot = new WidgetSlot({ branch: branch, host: demoHost });
    var wanted = null;

    function describe(path, d) {
        kicker.textContent = path;
        title.textContent = d.label;
        summary.textContent = d.summary;
        text.textContent = d.explanation;
        link.textContent = "Open as a page →";
        HrefManagerInstance.set(link, d.page);
    }

    function describeGroup(path, g) {
        kicker.textContent = path;
        title.textContent = g.label;
        summary.textContent = g.summary;
        text.textContent = g.demos.map(function (p) { return DEMOS.demos[p].label + " — " + DEMOS.demos[p].summary; }).join("\n");
        link.textContent = "Open " + DEMOS.demos[g.demos[0]].label + " as a page →";
        HrefManagerInstance.set(link, DEMOS.demos[g.demos[0]].page);
    }

    function show(path, d) {
        if (wanted === path) return;
        wanted = path;
        var slug = path.slice(path.lastIndexOf("/") + 1);
        try { history.replaceState(null, "", "?demo=" + encodeURIComponent(slug)); } catch (e) {}
        if (demoSlot.has(path)) { demoSlot.show(path); return; }
        _load(d.widget).then(function (Widget) {
            if (wanted === path) demoSlot.show(path, Widget, Object.assign({}, d.widget.params || {}, { keyboard: keyboard, menus: menus }));
        }).catch(function (e) { console.error("[galleryShell] demo '" + path + "' failed", e); });
    }

    // a demo chosen: described and shown; a group chosen: described, its first demo shown
    function select(path) {
        var d = DEMOS.demos[path];
        if (d) { describe(path, d); show(path, d); return; }
        var g = DEMOS.groups[path];
        if (!g || !g.demos.length) return;
        describeGroup(path, g);
        show(g.demos[0], DEMOS.demos[g.demos[0]]);
    }

    var paths = Object.keys(DEMOS.demos), first = paths[0];
    var asked = params && params.demo ? paths.filter(function (p) { return p.slice(p.lastIndexOf("/") + 1) === params.demo; })[0] : null;
    var initial = asked || first;

    _load(DEMOS.navigator).then(function (Navigator) {
        var nav = navSlot.show("navigator", Navigator, { tree: DEMOS.tree, labels: DEMOS.labels, label: DEMOS.label });
        nav.onSelect(select);
        nav.select(initial);
        select(initial);
    }).catch(function (e) { console.error("[galleryShell] the navigator failed", e); });
}
