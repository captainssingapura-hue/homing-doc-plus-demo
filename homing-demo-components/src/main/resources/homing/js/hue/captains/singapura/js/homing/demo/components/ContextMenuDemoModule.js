// =============================================================================
// ContextMenuDemo — the house's context menu in action, driven as its steward
// drives it: the choices that apply to a document, shown where the user asks -
// a right-press in the box. Its options: open - shown from the keyboard at the
// box's corner, its first row lit; close - hidden by call. The keys the demo
// holds walk it: the arrows move along the rows, Right and Enter open a row's
// rows beside it, Left steps back, Enter or a press picks; Escape hides it.
// Below, the same menu shown still, every level open. Every show, pick and
// hide is said.
//
// The page's one steward - one per page, which keeps the workspace's own menus
// - is not the demo's to take, so the demo holds the menu itself.
//
//   new ContextMenuDemo(container, { leaf })   leaf: "context-menu"
//   (the rest is a ComponentDemo's)
// =============================================================================

var _DOCUMENT_MENU = Object.freeze({ kind: "document", nodes: [
    { id: "open", label: "Open" },
    { id: "pin", label: "Pin it", icon: "pin" },
    { id: "rename", label: "Rename", hint: "F2" },
    { id: "share", label: "Share", section: 1, nodes: [{ id: "link", label: "Copy the link" }, { id: "mail", label: "Send it by mail" }] },
    { id: "delete", label: "Delete", icon: "remove", section: 2 }
] });

class ContextMenuDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "context-menu-demo", params);
        var self = this;
        this._shown = false;
        this._layer = this.el("layer", "div", dm_layer);
        this._menu = new ContextMenu(this.branch.createBranch("menu"), _DOCUMENT_MENU, { pick: function (id) { self.say("picked: " + id); self._hide(null); } });
        this._target = this.el("target", "div", dm_host, this.stage);
        this.el("target-text", "p", dm_text, this._target, "A document. Right-press anywhere in this box for what applies to it.");
        this._target.addEventListener("contextmenu", function (e) { e.preventDefault(); self._show(e.clientX, e.clientY, false); });
        var still = this.el("still", "div", dm_frames, this.stage);
        this._still = new ContextMenu(this.branch.createBranch("still-menu"), _DOCUMENT_MENU, {}, { specimen: true });
        this._still.bind(null);
        this._still.mount(still);
        this.intro = "right-press the box, or open it from the controls: then the arrows, Right, Left, Enter, Escape";
    }

    /** open: shown from the keyboard, at the box's corner, its first row lit. */
    open() {
        var r = this._target.getBoundingClientRect();
        this._show(r.left + 24, r.top + 24, true);
    }

    /** close: hidden by call. */
    close() { this._hide("by call"); }

    _show(x, y, fromKeyboard) {
        if (this._shown) this._hide("replaced");
        this._menu.bind(this);
        document.body.appendChild(this._layer);
        var at = this._menu.show(this._layer, { x: x, y: y }, { w: window.innerWidth, h: window.innerHeight }, fromKeyboard);
        // as its steward's claim does: the frame does not keep the browser's focus, so the keys come through their holder
        if (document.activeElement && this._menu.el.contains(document.activeElement)) document.activeElement.blur();
        this._shown = true;
        this.activate();
        this.say("shown at " + Math.round(at.x) + ", " + Math.round(at.y) + (fromKeyboard ? ", its first row lit" : ""));
    }

    _hide(reason) {
        if (!this._shown) { if (reason) this.say("nothing is shown"); return; }
        this._menu.hide();
        this._menu.unbind();
        if (this._layer.parentNode) this._layer.parentNode.removeChild(this._layer);
        this._shown = false;
        if (reason) this.say("hidden: " + reason);
    }

    /** The keys, while it is shown: to the menu; Escape hides it. */
    key(ev) {
        if (!this._shown) return false;
        if (ev.key === "Escape") { this._hide("escape"); return true; }
        return this._menu.key(ev);
    }

    disposed() {
        this._hide(null);
        try { this._menu.dispose(); } catch (e) {}
        try { this._still.dispose(); } catch (e) {}
    }
}
