// =============================================================================
// DialogApp — the dialog as a page. Three openers, one status line. The
// dialogs are built on this page's branch; each open mints a child and each
// close dissolves it, so the page is the same after as before.
// =============================================================================

const _owner = Object.freeze({ toString: () => "dialogPage" });

class DialogWidget {
    constructor(branch, params) {
        var self = this;
        this._acts = new Map();   // what each of its buttons does: the page is the handler of every press it makes
        branch.activate(_owner);
        var dialogs = 0;   // each open is a branch of its own
        var el = branch.createElement("root", "div");
        // the keys, through the party: the page's steward, made by the chrome and handed in the params
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");

        var kicker = new Kicker(KickerFactory.create(branch, "kicker"), { text: "homing-ui-dialog" });
        css.addClass(kicker.el, ga_kicker);
        el.appendChild(kicker.el);

        var title = new Heading1(Heading1Factory.create(branch, "title"), { text: "Dialog" });
        css.addClass(title.el, ga_title);
        el.appendChild(title.el);

        var lede = new Lede(LedeFactory.create(branch, "lede"), { text: "A frame that owns the screen until dismissed: the page behind goes inert, keys are captured, "
            + "Escape closes, Enter fires the primary action, and the focus comes back to where it was. "
            + "Non-modal, none of that; a control inside may take its own keys first." });
        css.addClass(lede.el, ga_lede);
        el.appendChild(lede.el);

        var status = branch.createElement("status", "div");
        css.addClass(status, ga_status);
        el.appendChild(status);
        var answers = 0;
        function say(what) { answers++; status.textContent = answers + ": " + what; }
        say("nothing opened yet");

        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        el.appendChild(row);

        // 1. Modal, with actions. OK is primary; Enter anywhere but a form control fires it.
        row.appendChild(self._press(new PrimaryButton(PrimaryButtonFactory.create(branch, "openModal"), { label: "Open a modal" }), function () {
            new Dialog(branch.createBranch("dialog" + (++dialogs)), {
                title: "A modal dialog", keyboard: kb, keyboardId: "dialog/modal",
                content: function (b, body) {
                    var p = new Lede(LedeFactory.create(b, "text"), { text: "Everything behind this frame is inert. Escape or the cross cancels; Enter, or OK, confirms." });
                    css.addClass(p.el, ga_lede);
                    body.appendChild(p.el);
                    return {};
                },
                actions: [
                    { id: "cancel", label: "Cancel", onClick: function (h) { say("cancelled"); h.close(); } },
                    { id: "ok",     label: "OK", primary: true, onClick: function (h) { say("confirmed"); h.close(); } }
                ],
                onClose: function () { if (!/confirmed|cancelled/.test(status.textContent)) say("dismissed"); }
            });
        }).el);

        // 2. Non-modal: the page stays live, keys reach the dialog only while focus is inside.
        row.appendChild(self._press(new PlainButton(PlainButtonFactory.create(branch, "openLoose"), { label: "Open a non-modal" }), function () {
            new Dialog(branch.createBranch("dialog" + (++dialogs)), {
                title: "A non-modal dialog", keyboard: kb, keyboardId: "dialog/loose-" + dialogs,
                modal: false,
                size: { w: 380, h: 200 },
                content: function (b, body) {
                    var p = new Lede(LedeFactory.create(b, "text"), { text: "The page behind is still live: click the other buttons. Escape closes this only while it has the focus." });
                    css.addClass(p.el, ga_lede);
                    body.appendChild(p.el);
                    return {};
                },
                onClose: function () { say("non-modal closed"); }
            });
        }).el);

        // 3. A control inside that takes its own keys: a counter on the arrows,
        //    and Escape resets it before the dialog is asked.
        row.appendChild(self._press(new PlainButton(PlainButtonFactory.create(branch, "openKeys"), { label: "Open one with keys inside" }), function () {
            var n = 0, dlg;
            dlg = new Dialog(branch.createBranch("dialog" + (++dialogs)), {
                title: "The content is asked first", keyboard: kb, keyboardId: "dialog/keys",
                size: { w: 460, h: 240 },
                content: function (b, body) {
                    var p = new Lede(LedeFactory.create(b, "text"), { text: "" });
                    css.addClass(p.el, ga_lede);
                    body.appendChild(p.el);
                    var draw = function () { p.text = "Count: " + n + " — arrows change it, Escape resets it to 0; a second Escape, with nothing to reset, closes the dialog. Apply is off until the count moves."; };
                    draw();
                    return {
                        onKeydown: function (ev) {
                            if (ev.key === "ArrowUp")   { n++; draw(); dlg.setAction("apply", { enabled: true }); return true; }
                            if (ev.key === "ArrowDown") { n--; draw(); dlg.setAction("apply", { enabled: true }); return true; }
                            if (ev.key === "Escape" && n !== 0) { n = 0; draw(); dlg.setAction("apply", { enabled: false }); return true; }
                            return false;
                        }
                    };
                },
                actions: [{ id: "apply", label: "Apply", primary: true, onClick: function (h) { say("applied " + n); h.close(); } }],
                onClose: function () { if (!/applied/.test(status.textContent)) say("keys dialog dismissed at " + n); }
            });
            dlg.setAction("apply", { enabled: false });
        }).el);
        this.root = el;
    }

    /** One of its buttons, its press kept with it: the page is the handler of every press it makes. */
    _press(button, act) { this._acts.set(button, act); button.setOnClick(this); return button; }

    /** A page handles its buttons' presses. */
    canHandle(type) { return type === EVENT.CLICK; }

    /** A press: what the page keeps for the button pressed. */
    handle(event) { this._acts.get(event.source)(event); }

    dispose() {}
}

function appMain(el, params) {
    el.appendChild(new DialogWidget(domOpsParty.createBranch("dialogPage"), params).root);
}
