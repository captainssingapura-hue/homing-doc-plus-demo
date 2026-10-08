// =============================================================================
// PanZoomBarDemo — the house's pan-zoom bar in action: the controls of a view
// it is given - zoom out, the zoom read out as a share of fit, zoom in, and
// back to fit - driving it and following it: out goes off at the least, fit at
// fit, in at the most. Here it drives a drawing; zoom the drawing by the wheel
// with Ctrl or ⌘ held and the bar follows. What the bar reads is said as it
// changes. It is controlled by nothing more than itself.
//
//   new PanZoomBarDemo(container, { leaf })   leaf: "pan-zoom-bar"
//   (the rest is a ComponentDemo's)
// =============================================================================

class PanZoomBarDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "pan-zoom-bar-demo", params);
        var self = this, last = null;
        var row = this.el("row", "div", dm_row, this.stage);
        var host = this.el("host", "div", dm_host, this.stage);
        var svg = new DOMParser().parseFromString(DEMO_DRAWINGS.stages, "image/svg+xml").documentElement;   // the drawing is data
        this._view = new SvgPanZoom(this.branch.createBranch("view"), { svg: svg, label: "From draft to published" });
        css.addClass(this._view.root, dm_fill);
        host.appendChild(this._view.root);
        this._bar = new PanZoomBar(this.branch.createBranch("bar"), this._view);
        row.appendChild(this._bar.root);
        // the readout is the bar's one live region among its own children; read it, not its buttons
        var readout = Array.prototype.find.call(this._bar.root.children, function (c) { return c.getAttribute("aria-live"); }) || this._bar.root;
        this._off = this._view.onChange(function () {
            var reads = readout.textContent.replace(/\s+/g, " ").trim();
            if (reads !== last) { last = reads; self.say("the bar reads " + reads); }
        });
        this.intro = "zoom with the bar's buttons, or the drawing with Ctrl or ⌘ and the wheel: the bar follows";
    }

    disposed() {
        if (this._off) { try { this._off(); } catch (e) {} this._off = null; }
        try { this._bar.dispose(); } catch (e) {}
        try { this._view.dispose(); } catch (e) {}
    }
}
