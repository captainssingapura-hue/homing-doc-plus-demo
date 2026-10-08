// =============================================================================
// FloatingPaneDemo — the house's floating pane in action: content in a window
// of its own over the box, which the user moves by its head and sizes by its
// grip, its place and measure clamped to the box it floats in. Moved and
// resized are said once, when the hand lets go. Its cross asks its holder to
// close it - here the holder does. Its options: move - asked past the far
// corner, clamped to the box; resize - bigger; ring - the active one, or not;
// open - opened again once closed, where it began.
//
//   new FloatingPaneDemo(container, { leaf })   leaf: "floating-pane"
//   (the rest is a ComponentDemo's)
// =============================================================================

class FloatingPaneDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "floating-pane-demo", params);
        this._pane = null;
        this._opened = 0;
        this._ringed = false;
        this._host = this.el("host", "div", dm_host, this.stage);
        this._open();
        this.intro = "move it by its head, size it by the grip at its corner, close it by its cross - or move, size, ring and open it from the controls";
    }

    move() {
        if (!this._pane) { this.say("closed: open it again first"); return; }
        this._pane.moveTo(10000, 10000);
        this.say("asked to go past the corner: clamped to the box");
    }

    resize() {
        if (!this._pane) { this.say("closed: open it again first"); return; }
        var r = this._pane.bounds();
        this._pane.resizeTo(r.w + 60, r.h + 40);
    }

    ring() {
        if (!this._pane) { this.say("closed: open it again first"); return; }
        this._ringed = !this._ringed;
        this._pane.setActive(this._ringed);
        this.say(this._ringed ? "ringed: the active one" : "no ring: not the active one");
    }

    open() {
        if (this._pane) { this.say("open already"); return; }
        this._open();
        this.say("open again, where it began");
    }

    _open() {
        var self = this, n = ++this._opened;
        this._ringed = false;
        this._pane = new FloatingPane(this.branch.createBranch("pane-" + n), {
            id: "notes", title: "Notes", x: 16, y: 12, w: 240, h: 120, z: 1, closable: true,
            onEvent: function (ev) {
                if (ev.kind === "Moved") self.say("moved to " + ev.x + ", " + ev.y);
                else if (ev.kind === "Resized") self.say("resized to " + ev.w + " by " + ev.h);
            },
            onClose: function (pane) {
                self.say("its cross asked its holder to close it - and the holder did");
                pane.dispose();
                self._pane = null;
            }
        });
        var text = this.branch.createElement("note-" + n, "p");
        css.addClass(text, dm_text);
        text.textContent = "Notes for the meeting: the budget, the hiring plan, the spring launch.";
        this._pane.body.appendChild(text);
        this._host.appendChild(this._pane.root);
    }

    disposed() {
        if (this._pane) { try { this._pane.dispose(); } catch (e) {} this._pane = null; }
    }
}
