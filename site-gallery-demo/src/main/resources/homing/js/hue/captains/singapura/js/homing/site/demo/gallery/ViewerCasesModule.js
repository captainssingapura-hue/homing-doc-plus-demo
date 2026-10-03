// =============================================================================
// ViewerCase, CaseBoard — the viewers' case board. A case is a cell: its number
// and title, what to do, the scene it holds, and its checks - each a line that
// says what is done, what is expected, and a lamp for what happened.
//
// The board judges the checks by where the keys go. It hears the route of
// every Escape from the steward's trace, finds the member the Escape came
// from - the holder it was routed to, or the member around the element it was
// pressed in - and reads who holds once the steward is done with it, which is
// before the trace is told. A member watched answers, for the route its Escape
// took, which check that was and the holder expected; the root's default is
// read at the time - the home while the page names one, else no one.
//
//   var c = new ViewerCase(branch, host, { n, title, how })   c.root  c.body
//   var k = c.check(what)   k.expect(text)   k.verdict(ok, text)   k.verdict() → "waiting" | "ok" | "off"
//   var board = new CaseBoard(steward)
//   board.name(m, text)        a member's name for the eye
//   board.watch(m, judge)      judge(route) → { check, id: the holder expected, or null for no one, says }, or null
//   board.rootDefault() → { id, says }   the home, or no one
//   board.nameOf(id)   board.dispose()
// =============================================================================

const _viewerCaseOwner = Object.freeze({ toString: () => "viewerCase" });

/** One check of a case: what is done, what is expected, and a lamp. */
class CaseCheck {
    constructor(branch, host, what, n) {
        var row = branch.createElement("check" + n, "div");
        css.addClass(row, ga_case_check);
        var label = branch.createElement("what" + n, "span");
        label.textContent = what;
        var expect = branch.createElement("expect" + n, "span");
        css.addClass(expect, ga_cell_caption);
        var lamp = branch.createElement("lamp" + n, "span");
        css.addClass(lamp, ga_lamp);
        lamp.setAttribute("aria-live", "polite");
        row.appendChild(label);
        row.appendChild(expect);
        row.appendChild(lamp);
        host.appendChild(row);
        this._expect = expect;
        this._lamp = lamp;
        this._verdict = "waiting";
        lamp.textContent = "waiting";
        lamp.setAttribute("data-verdict", "waiting");
    }
    expect(text) { this._expect.textContent = "expected: " + text; }
    verdict(ok, text) {
        if (ok === undefined) return this._verdict;
        this._verdict = ok ? "ok" : "off";
        css.toggleClass(this._lamp, ga_lamp_ok, ok);
        css.toggleClass(this._lamp, ga_lamp_off, !ok);
        this._lamp.textContent = (ok ? "as expected - " : "not as expected - ") + text;
        this._lamp.setAttribute("data-verdict", this._verdict);
    }
}

class ViewerCase {
    constructor(branch, host, opts) {
        branch.activate(_viewerCaseOwner);
        var o = opts || {};
        this.branch = branch;
        var root = branch.createElement("case", "section");
        css.addClass(root, ga_case);
        root.setAttribute("aria-label", "case " + o.n + ": " + o.title);
        var title = branch.createElement("title", "div");
        css.addClass(title, ga_case_title);
        title.textContent = "Case " + o.n + ": " + o.title;
        var how = branch.createElement("how", "p");
        css.addClass(how, ga_cell_caption);
        how.textContent = o.how || "";
        var body = branch.createElement("body", "div");
        css.addClass(body, ga_case_body);
        root.appendChild(title);
        root.appendChild(how);
        root.appendChild(body);
        host.appendChild(root);
        this.root = root;
        this.body = body;
        this._checks = 0;
    }
    /** A check under the scene: what is done; its expectation and lamp said as they come. */
    check(what) { return new CaseCheck(this.branch, this.root, what, ++this._checks); }
    dispose() { this.branch.dissolve(); }
}

class CaseBoard {
    constructor(steward) {
        var self = this;
        this._steward = steward;
        this._names = {};
        this._watched = {};
        this._off = steward.trace(function (t) { self._heard(t); });
    }
    name(m, text) { this._names[m.id] = text; }
    nameOf(id) { return id == null ? "no one" : (this._names[id] || String(id)); }
    watch(m, judge) { this._watched[m.id] = judge; }
    /** The root's default, read now: the home while the page names one, else no one. */
    rootDefault() {
        var h = this._steward.homed();
        return { id: h, says: h ? this.nameOf(h) + ", the root's default" : "no one: the page names no home" };
    }
    /** An Escape routed: the member it came from, the check its route answers, and who holds now. */
    _heard(t) {
        if (t.kind !== "KeyDown" || t.key !== "Escape") return;
        var from = t.route === "holder" ? t.to : (t.to && t.to.nodeType === 1 ? this._steward.memberAt(t.to) : null);
        var judge = from ? this._watched[from] : null;
        var j = judge ? judge(t.route) : null;
        if (!j) return;
        var after = this._steward.holder(), at = this._steward.marker();
        var got = this.nameOf(after) + (at && after === from ? ", " + at.state : "");
        j.check.expect(j.says);
        j.check.verdict((j.id || null) === (after || null), "the keys at: " + got);
    }
    dispose() { this._off(); }
}
