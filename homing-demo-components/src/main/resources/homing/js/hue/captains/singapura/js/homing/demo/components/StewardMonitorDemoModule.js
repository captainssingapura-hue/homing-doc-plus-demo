// =============================================================================
// StewardMonitorDemo — the house's steward monitor in action: this page's keys
// as one lamp - who holds them, lent or away - changing as the page is
// pressed. Its option: ask - what the lamp says, and whether an invariant of
// the steward's is broken.
//
//   new StewardMonitorDemo(container, { leaf })   leaf: "steward-monitor"
//   (the rest is a ComponentDemo's)
// =============================================================================

class StewardMonitorDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "steward-monitor-demo", params);
        var host = this.el("host", "div", dm_row, this.stage);
        this._monitor = new StewardMonitor(this.branch.createBranch("monitor"), { host: host });
        this.intro = "this page's keys, as one lamp: press around the page and it changes; ask what it says from the controls";
    }

    /** ask: what the lamp says, and the first invariant broken, if one is. */
    ask() {
        var broken = this._monitor.broken();
        return "the lamp: " + this._monitor.lamp() + (broken.length ? " - broken: " + broken[0] : " - every invariant holds");
    }

    disposed() { try { this._monitor.dispose(); } catch (e) {} }
}
