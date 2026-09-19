// =============================================================================
// GalleryShellApp — the gallery as a shell. A fixed one–two split fills the
// slot: the navigator on the left, the chosen demo top right, its
// explanation under it. The navigator is the relation tree over DEMOS; the
// demo is the demo app's construct, imported through the serving context
// when first chosen and kept in a slot after; the explanation is what DEMOS
// says, with a link to the page the demo also is. The address follows.
// =============================================================================

const _owner = Object.freeze({ toString: () => "galleryShell" });
var href = HrefManagerInstance;

var _loaded = new Map();   // module url → Promise<construct>

function _load(entry) {
    var url = withServingContext(entry.module);
    var p = _loaded.get(url);
    if (!p) {
        p = import(url).then(function (m) {
            var fn = m[entry.export || "construct"];
            if (typeof fn !== "function") throw new Error("[galleryShell] " + entry.module + " exports no " + (entry.export || "construct"));
            return fn;
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

    var split = mountSplitPane({
        branch: branch, host: shell, minPanePx: 160,
        layout: { kind: "split", orientation: "horizontal", children: [
            { pane: { kind: "leaf", slotId: "nav" }, ratio: 1 },
            { pane: { kind: "split", orientation: "vertical", children: [
                { pane: { kind: "leaf", slotId: "demo" }, ratio: 5 },
                { pane: { kind: "leaf", slotId: "explain" }, ratio: 3 } ] }, ratio: 4 } ] }
    });

    var navHost = branch.createElement("navHost", "div");
    css.addClass(navHost, ga_shell_nav);
    split.slot("nav").appendChild(navHost);

    var demoHost = branch.createElement("demoHost", "div");
    css.addClass(demoHost, ga_shell_demo);
    split.slot("demo").appendChild(demoHost);

    var explain = branch.createElement("explain", "div");
    css.addClass(explain, ga_shell_explain);
    split.slot("explain").appendChild(explain);
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

    var navSlot  = createWidgetSlot({ branch: branch, host: navHost });
    var demoSlot = createWidgetSlot({ branch: branch, host: demoHost });
    var wanted = null;

    function describe(path, d) {
        kicker.textContent = path;
        title.textContent = d.label;
        summary.textContent = d.summary;
        text.textContent = d.explanation;
        link.textContent = "Open as a page →";
        href.set(link, d.page);
    }

    function select(path) {
        var d = DEMOS.demos[path];
        if (!d || wanted === path) return;
        wanted = path;
        describe(path, d);
        var slug = path.slice(path.lastIndexOf("/") + 1);
        try { history.replaceState(null, "", "?demo=" + encodeURIComponent(slug)); } catch (e) {}
        if (demoSlot.has(path)) { demoSlot.show(path); return; }
        _load(d.widget).then(function (construct) {
            if (wanted === path) demoSlot.show(path, construct, d.widget.params);
        }).catch(function (e) { console.error("[galleryShell] demo '" + path + "' failed", e); });
    }

    var first = Object.keys(DEMOS.demos)[0];
    var asked = params && params.demo ? "gallery/" + params.demo : null;
    var initial = asked && DEMOS.demos[asked] ? asked : first;

    _load(DEMOS.navigator).then(function (construct) {
        var nav = navSlot.show("navigator", construct, { tree: DEMOS.tree, labels: DEMOS.labels, label: DEMOS.label });
        nav.onSelect(select);
        nav.select(initial);
        select(initial);
    }).catch(function (e) { console.error("[galleryShell] the navigator failed", e); });
}
