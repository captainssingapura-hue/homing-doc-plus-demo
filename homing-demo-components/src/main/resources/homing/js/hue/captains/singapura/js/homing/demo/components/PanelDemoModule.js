// =============================================================================
// PanelDemo — the house's panel in action: one concern, named, filling the
// box it is put in. Its options: current - the one in use, said in colour
// alone, nothing else moving; raised - lifted off its own plane, as the design
// has it, or set flat. Each change is said.
//
//   new PanelDemo(container, { leaf })   leaf: "panel"
//   (the rest is a ComponentDemo's)
// =============================================================================

class PanelDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "panel-demo", params);
        var host = this.el("host", "div", dm_host, this.stage);
        this._panel = new PanelBuilder().title("Orders to pack").host(host).build(this.branch.createBranch("panel"));
        this.el("text", "p", dm_text, this._panel.body, "Twelve orders wait to be packed; three of them leave today.");
        this._current = false;
        this._raised = false;
        this.intro = "a panel fills what holds it: make it current, or raise it, in the controls";
    }

    /** current: the one in use, said in colour alone. */
    highlight(on) {
        if (on === this._current) return;
        this._current = on;
        this._panel.highlight(on);
        this.say(on ? "the current one: said in colour, nothing else moves" : "no longer the current one");
    }

    /** raised: off its own plane, or flat on it. */
    raised(on) {
        if (on === this._raised) return;
        this._raised = on;
        this._panel.elevation(on ? "elevated" : null);
        this.say(on ? "raised: off its plane, as the design has it" : "flat: on its own plane");
    }

    disposed() { this._panel.dispose(); }
}
