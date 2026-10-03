// =============================================================================
// SvgViewer — the experiment's one widget: an SVG drawing that zooms and pans,
// and nothing else, as a widget by the workspace's pattern - made from its
// container and its params alone, its DomOps party and its focus party its
// own, offered as roots for its host to graft. It never learns where it sits.
//
// The keys, by the design as it stands (RFC 0066 E3, keyboard §15.1, §17.5)
// and nothing added: the widget is a member of its own focus party, never
// focused itself; the viewport inside it is the native world, a control with
// its own keys. A press in the widget claims the keys for it, and the widget
// puts the browser's focus in its viewport - lent. An Escape the viewport has
// no use for is the steward's: the viewport lets go, and the widget holds with
// nothing focused - its keys from the steward then, handed on to the view. Its
// own Escape yields: the keys go up the focus tree to the first ancestor that
// would hold them, else to no one.
//
//   var v = new SvgViewer(container, { svg, label? })
//   v.root   v.roots { dom, focus }   v.focus  its membership   v.view  the SvgPanZoom
//   v.activate()   v.dispose()
// =============================================================================

const _svgViewerOwner = Object.freeze({ toString: () => "svgViewer" });
var _svgViewers = 0;

class SvgViewer {
    constructor(container, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[SvgViewer] a container is required: the one its host lends it");
        var p = params || {}, label = p.label || "A drawing";
        var name = "svgViewer-" + (++_svgViewers);
        this._dom = domOpsParties.mobile(name);
        this._dom.activate(_svgViewerOwner);
        var root = this._dom.createElement("root", "div");
        css.addClass(root, ga_panel);
        root.setAttribute("role", "group");
        root.setAttribute("aria-label", label);
        var head = this._dom.createElement("head", "div");
        css.addClass(head, ga_panel_header);
        head.textContent = label;
        root.appendChild(head);
        this.view = new SvgPanZoom(this._dom.createBranch("zoom"), { svg: p.svg, label: label });
        this._bar = new PanZoomBar(this._dom.createBranch("bar"), this.view);
        root.appendChild(this._bar.root);
        root.appendChild(this.view.root);
        container.appendChild(root);
        this.root = root;
        // a press on the head claims, and what granted put in the viewport stays there: the press's own default, the focus to the body, stopped
        head.addEventListener("mousedown", function (ev) { ev.preventDefault(); });
        this._focusParty = focusParties.mobile(name);
        this.focus = this._focusParty.root.join("viewer", this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: this._dom, focus: this._focusParty });
    }

    /** Asked for the keys: a claim, what a press on it does. */
    activate() { Keys.claim(this.focus); }

    /** Given the keys by a press or a call: into the viewport, so its keys are its own at once. */
    granted(by) { if (by !== "native") this.view.root.focus({ preventScroll: true }); }

    /** Held with nothing focused: Escape yields; the view's keys are handed on to it. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return this.view.key(ev);
    }

    dispose() {
        this._off();
        this._bar.dispose();
        this.view.dispose();
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
