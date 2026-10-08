// =============================================================================
// SvgPanZoomDemo — the house's pan-zoom view in action: a drawing in a box its
// host sizes, fitted to it whole. The wheel with Ctrl or ⌘ held - or a
// trackpad's pinch - zooms about the pointer; a drag pans once zoomed in; a
// double press zooms in there, or back to fit; while the demo holds the keys,
// + and − zoom, 0 fits, the arrows pan. Its options: zoom-in, zoom-out, fit.
// Every change of the zoom is said, as a share of fit.
//
//   new SvgPanZoomDemo(container, { leaf })   leaf: "svg-pan-zoom"
//   (the rest is a ComponentDemo's)
// =============================================================================

class SvgPanZoomDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "svg-pan-zoom-demo", params);
        var self = this, last = null;
        var host = this.el("host", "div", dm_host, this.stage);
        var svg = new DOMParser().parseFromString(DEMO_DRAWINGS.flow, "image/svg+xml").documentElement;   // the drawing is data
        this._view = new SvgPanZoom(this.branch.createBranch("view"), { svg: svg, label: "Orders, stock and shipping" });
        css.addClass(this._view.root, dm_fill);
        host.appendChild(this._view.root);
        this._off = this._view.onChange(function (view) {
            var share = view && typeof view.zoom === "number" ? Math.round(100 * view.zoom) + "% of fit" + (view.fitted ? ", fitted" : "") : "changed";
            if (share !== last) { last = share; self.say("zoom: " + share); }
        });
        this.intro = "Ctrl or ⌘ and the wheel, a pinch, a double press, or the controls; drag to pan once zoomed in";
    }

    zoomIn() { this._view.zoomIn(); }

    zoomOut() { this._view.zoomOut(); }

    fit() { this._view.fit(); }

    key(ev) { return this._view.key(ev); }

    disposed() {
        if (this._off) { try { this._off(); } catch (e) {} this._off = null; }
        try { this._view.dispose(); } catch (e) {}
    }
}
