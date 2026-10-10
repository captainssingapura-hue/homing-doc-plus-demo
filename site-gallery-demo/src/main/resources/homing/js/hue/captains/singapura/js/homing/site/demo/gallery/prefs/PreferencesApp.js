// =============================================================================
// PreferencesApp — the preferences as a page. The view is a widget, shown in
// a slot over the page's host with the gallery's stamped registry for params; the widgets arrive as nodes are
// chosen. Nothing else on this page.
// =============================================================================

const _owner = Object.freeze({ toString: () => "preferencesPage" });

class PreferencesWidget {
    constructor(branch, params) {
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
        var shown = branch.createBranch("view");
        shown.activate(_owner);
        this._slot = new WidgetSlot({ branch: shown, host: host });
        var view = this._slot.show("preferences", PreferencesView, PREFERENCES);
        // the keys, through the party: the page holds them for the view — a press or the focus arriving in it claims — and the view hands them on
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");
        this._kb = kb;
        this._kbId = kb.join("preferences/view", { keyDown: function (ev) { return view.key(ev); } });
        this._offKeys = Keys.claimOn(host, kb, this._kbId);
        this.root = el;
    }

    dispose() { this._offKeys(); this._kb.leave(this._kbId); this._slot.disposeAll(); }
}

function appMain(el, params) {
    el.appendChild(new PreferencesWidget(domOpsParty.createBranch("preferencesPage"), params).root);
}
