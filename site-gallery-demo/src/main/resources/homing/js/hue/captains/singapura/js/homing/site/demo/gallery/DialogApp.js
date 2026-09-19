// =============================================================================
// DialogApp — the dialog as a page. Three openers, one status line. The
// dialogs are built on this page's branch; each open mints a child and each
// close dissolves it, so the page is the same after as before.
// =============================================================================

const _owner = Object.freeze({ toString: () => "dialogPage" });

function construct(branch, params) {
    branch.activate(_owner);
    var el = branch.createElement("root", "div");

    var kicker = branch.createElement("kicker", "div");
    css.addClass(kicker, ga_kicker);
    kicker.textContent = "homing-ui-dialog";
    el.appendChild(kicker);

    var title = branch.createElement("title", "h1");
    css.addClass(title, ga_title);
    title.textContent = "Dialog";
    el.appendChild(title);

    var lede = branch.createElement("lede", "p");
    css.addClass(lede, ga_lede);
    lede.textContent = "A frame that owns the screen until dismissed: the page behind goes inert, keys are captured, "
        + "Escape closes, Enter fires the primary action, and the focus comes back to where it was. "
        + "Non-modal, none of that; a control inside may take its own keys first.";
    el.appendChild(lede);

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
    row.appendChild(Button(branch, "openModal", { label: "Open a modal", onClick: function () {
        openDialog({
            branch: branch,
            title: "A modal dialog",
            content: function (b, body) {
                var p = b.createElement("text", "p");
                css.addClass(p, ga_lede);
                p.textContent = "Everything behind this frame is inert. Escape or the cross cancels; Enter, or OK, confirms.";
                body.appendChild(p);
                return {};
            },
            actions: [
                { id: "cancel", label: "Cancel", onClick: function (h) { say("cancelled"); h.close(); } },
                { id: "ok",     label: "OK", primary: true, onClick: function (h) { say("confirmed"); h.close(); } }
            ],
            onClose: function () { if (!/confirmed|cancelled/.test(status.textContent)) say("dismissed"); }
        });
    } }));

    // 2. Non-modal: the page stays live, keys reach the dialog only while focus is inside.
    row.appendChild(Button(branch, "openLoose", { label: "Open a non-modal", kind: "plain", onClick: function () {
        openDialog({
            branch: branch,
            title: "A non-modal dialog",
            modal: false,
            size: { w: 380, h: 200 },
            content: function (b, body) {
                var p = b.createElement("text", "p");
                css.addClass(p, ga_lede);
                p.textContent = "The page behind is still live: click the other buttons. Escape closes this only while it has the focus.";
                body.appendChild(p);
                return {};
            },
            onClose: function () { say("non-modal closed"); }
        });
    } }));

    // 3. A control inside that takes its own keys: a counter on the arrows,
    //    and Escape resets it before the dialog is asked.
    row.appendChild(Button(branch, "openKeys", { label: "Open one with keys inside", kind: "plain", onClick: function () {
        var n = 0, dlg;
        dlg = openDialog({
            branch: branch,
            title: "The content is asked first",
            size: { w: 460, h: 240 },
            content: function (b, body) {
                var p = b.createElement("text", "p");
                css.addClass(p, ga_lede);
                body.appendChild(p);
                var draw = function () { p.textContent = "Count: " + n + " — arrows change it, Escape resets it to 0; a second Escape, with nothing to reset, closes the dialog. Apply is off until the count moves."; };
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
    } }));
    return { root: el, dispose: function () {} };
}

function appMain(el, params) {
    el.appendChild(construct(domOpsParty.createBranch("dialogPage"), params).root);
}
