// =============================================================================
// KeysPicker — swap the workspace's key scheme while you are standing in it.
// A pane's keys are a list it is given, and pane.keys(...) changes it live, so
// a page that means to show that off needs no more than a list of choices and
// a loop.
//
//   new KeysPicker(branch, { host, panes, label?, onPick? })
//     panes:  () → [pane], asked afresh: the workspace is split and merged
//
//   picker.dispose()
//
// The three rows are the two schemes that ship and both of them together,
// which is the point of a list: arrows and control chords do not overlap, so a
// pane may wear both and answer either.
//
// AND ONE OF THEM CANNOT BE PRESSED HERE. A browser keeps Ctrl+Tab and the
// page keys for its own tabs and hands them to nobody, so inside a browser tab
// that row is silent on its faithful chords — it answers Ctrl+Shift+← and
// Ctrl+Shift+→ as well for exactly this reason, and those arrive. The scheme
// is whole in a desktop shell; here it is whole minus the keys the shell above
// it took.
// =============================================================================

const _keysPickerOwner = Object.freeze({ toString: () => "keysPicker" });

class KeysPicker {
    constructor(branch, opts) {
        if (!branch) throw new Error("[KeysPicker] a branch of its own is required");
        var o = opts || {}, self = this;
        if (typeof o.panes !== "function") throw new Error("[KeysPicker] panes() is asked afresh: hand in the function, not the list");
        branch.activate(_keysPickerOwner);
        this.branch = branch;
        this._panes = o.panes;
        this._onPick = typeof o.onPick === "function" ? o.onPick : null;

        var row = branch.createElement("keys", "div");
        css.addClass(row, ga_control);
        var label = branch.createElement("label", "span");
        css.addClass(label, ga_control_label);
        label.textContent = o.label == null ? "the keys" : String(o.label);
        row.appendChild(label);

        var pick = branch.createElement("scheme", "select");
        css.addClass(pick, mtp_new_pick);
        pick.setAttribute("aria-label", "which keys the docks answer");
        this._ways = [
            { id: "arrows",  label: "the arrows",        schemes: [PaneKeys] },
            { id: "browser", label: "a browser's",       schemes: [BrowserKeys] },
            { id: "both",    label: "both",              schemes: [PaneKeys, BrowserKeys] }
        ];
        this._ways.forEach(function (w) {
            var opt = branch.createElement("way-" + w.id, "option");
            opt.value = w.id;
            opt.textContent = w.label;
            pick.appendChild(opt);
        });
        pick.addEventListener("change", function () { self.pick(pick.value); });
        row.appendChild(pick);
        this._pick = pick;

        var says = branch.createElement("says", "span");
        css.addClass(says, ga_control_readout);
        row.appendChild(says);
        this._says = says;

        this.root = row;
        if (o.host) o.host.appendChild(row);
        this.pick("arrows");
    }

    /** That way, on every dock there is: the schemes are a list a pane is given, and giving it another is the whole swap. */
    pick(id) {
        var way = null;
        for (var i = 0; i < this._ways.length; i++) if (this._ways[i].id === id) way = this._ways[i];
        if (!way) return this;
        this._pick.value = way.id;
        this._panes().forEach(function (p) { p.keys(way.schemes); });
        this._says.textContent = way.id === "arrows" ? "← →  Home  End   — the bar holds the keys"
                                                     : "Ctrl+Shift+← →   (Ctrl+Tab where a browser will give it up)   — the tab holds the keys";
        if (this._onPick) this._onPick(way.id);
        return this;
    }

    /** A dock minted after a choice was made answers what the others answer. */
    refresh() { return this.pick(this._pick.value); }

    dispose() {
        if (this.root.parentNode) this.root.parentNode.removeChild(this.root);
        try { this.branch.dissolve(); } catch (e) {}
    }
}
