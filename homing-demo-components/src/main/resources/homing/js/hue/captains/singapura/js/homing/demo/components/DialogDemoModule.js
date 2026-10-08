// =============================================================================
// DialogDemo — the house's dialog in action: one matter put to the user above
// the page. Its options: open-modal - the page behind held back, a scrim over
// it and nothing there answering, until it is settled; open - the page still
// answers; close - closed by call. It takes the keys while it is open: Escape
// closes it, Enter does the primary action, and they go back to whoever held
// them. Every opening, action and close is said.
//
//   new DialogDemo(container, { leaf })   leaf: "dialog"
//   (the rest is a ComponentDemo's)
// =============================================================================

class DialogDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "dialog-demo", params);
        this._dialog = null;
        this._opened = 0;
        this.el("about", "p", dm_text, this.stage, "A draft with changes nobody has saved. Open the dialog from the controls: it asks whether to discard them.");
        this.intro = "open the dialog, modal or not: settle it by an action, by Enter for the primary one, or by Escape";
    }

    openModal() { this._open(true); }

    open() { this._open(false); }

    close() {
        if (!this._dialog) { this.say("nothing is open"); return; }
        this._how = "by call";
        this._dialog.close();
    }

    _open(modal) {
        if (this._dialog) { this.say("one is open already: settle it first"); return; }
        var self = this, n = ++this._opened;
        this._how = "";
        this._dialog = new Dialog(this.branch.createBranch("dialog-" + n), {
            title: "Discard the draft?",
            modal: modal,
            content: function (b, body) {
                var text = b.createElement("draft-note", "p");
                css.addClass(text, dm_text);
                text.textContent = "The draft has changes nobody has saved. Discarding it cannot be undone.";
                body.appendChild(text);
                return {};
            },
            actions: [
                { id: "keep", label: "Keep it", onClick: function (d) { self._how = "Keep it: the draft stays"; d.close(); } },
                { id: "discard", label: "Discard", primary: true, onClick: function (d) { self._how = "Discard, the primary action"; d.close(); } }
            ],
            keyboard: KeyboardStewardInstance,
            keyboardId: "demo-dialog-" + n,
            onClose: function () {
                self._dialog = null;
                self.say("closed" + (self._how ? " - " + self._how : " - by Escape or its cross") + "; the keys go back to who held them");
            }
        });
        this.say(modal ? "open, modal: the page behind is held back until it is settled" : "open, not modal: the page behind still answers");
    }

    disposed() {
        if (this._dialog) { try { this._dialog.close(); } catch (e) {} this._dialog = null; }
    }
}
