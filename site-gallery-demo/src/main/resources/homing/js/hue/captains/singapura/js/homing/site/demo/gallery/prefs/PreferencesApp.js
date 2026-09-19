// =============================================================================
// PreferencesApp — the preferences as a page. The view is mounted in the
// slot with the gallery's stamped registry; the widgets arrive as nodes are
// chosen. Nothing else on this page.
// =============================================================================

const _owner = Object.freeze({ toString: () => "preferencesPage" });

function construct(branch, params) {
    branch.activate(_owner);
    var el = branch.createElement("root", "div");

    var kicker = branch.createElement("kicker", "div");
    css.addClass(kicker, ga_kicker);
    kicker.textContent = "homing-preferences";
    el.appendChild(kicker);

    var title = branch.createElement("title", "h1");
    css.addClass(title, ga_title);
    title.textContent = "Preferences";
    el.appendChild(title);

    var lede = branch.createElement("lede", "p");
    css.addClass(lede, ga_lede);
    lede.textContent = "A rigid tree on the left, from the relation tree; the chosen node's widget on the right, "
        + "loaded when the node is first chosen and kept after. Every widget writes through the steward "
        + "and follows it, so a pick here reaches the chrome, another tab, and the overview alike.";
    el.appendChild(lede);

    var host = branch.createElement("host", "div");
    el.appendChild(host);
    var view = mountPreferencesView(branch, host, PREFERENCES);
    return { root: el, dispose: function () { view.dispose(); } };
}

function appMain(el, params) {
    el.appendChild(construct(domOpsParty.createBranch("preferencesPage"), params).root);
}
