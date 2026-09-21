// =============================================================================
// GalleryShellApp — the gallery as a shell. A split grid of three cells,
// arranged once and never re-arranged, fills the slot: the navigator on the
// left, the chosen demo top right, its explanation under it — the dividers
// drag, nothing subdivides or goes. The navigator is the relation tree over DEMOS; the
// demo is the demo app's widget class, imported through the serving context
// when first chosen and kept in a slot after; the explanation is what DEMOS
// says, with a link to the page the demo also is. The address follows. The
// shell is one document, so it makes the one keyboard steward and hands it
// to every demo in its params: a demo standing alone as a page makes its own.
// =============================================================================

const _owner = Object.freeze({ toString: () => "galleryShell" });
var _loaded = new Map();   // module url → Promise<class>

function _load(entry) {
    var url = withServingContext(entry.module);
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

    var keyboard = new KeyboardSteward(branch.createBranch("keyboard"), {});
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

    function select(path) {
        var d = DEMOS.demos[path];
        if (!d || wanted === path) return;
        wanted = path;
        describe(path, d);
        var slug = path.slice(path.lastIndexOf("/") + 1);
        try { history.replaceState(null, "", "?demo=" + encodeURIComponent(slug)); } catch (e) {}
        if (demoSlot.has(path)) { demoSlot.show(path); return; }
        _load(d.widget).then(function (Widget) {
            if (wanted === path) demoSlot.show(path, Widget, Object.assign({}, d.widget.params || {}, { keyboard: keyboard }));
        }).catch(function (e) { console.error("[galleryShell] demo '" + path + "' failed", e); });
    }

    var first = Object.keys(DEMOS.demos)[0];
    var asked = params && params.demo ? "gallery/" + params.demo : null;
    var initial = asked && DEMOS.demos[asked] ? asked : first;

    _load(DEMOS.navigator).then(function (Navigator) {
        var nav = navSlot.show("navigator", Navigator, { tree: DEMOS.tree, labels: DEMOS.labels, label: DEMOS.label });
        nav.onSelect(select);
        nav.select(initial);
        select(initial);
    }).catch(function (e) { console.error("[galleryShell] the navigator failed", e); });
}
