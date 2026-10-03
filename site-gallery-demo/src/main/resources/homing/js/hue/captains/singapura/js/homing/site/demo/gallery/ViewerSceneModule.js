// =============================================================================
// SvgViewer — the experiment's one widget: an SVG drawing that zooms and pans,
// and nothing else, as a widget by the workspace's pattern - made from its
// container and its params alone, its DomOps party and its focus party its
// own, offered as roots for its host to graft. It never learns where it sits.
//
// The keys (RFC 0066 E3, keyboard §15.1, §17.5): the widget is a member of its
// own focus party, never focused itself; the viewport inside it is the native
// world, a control with its own keys. A press in the widget claims the keys
// for it, and the widget puts the browser's focus in its viewport - lent. An
// Escape the viewport has no use for lets it go: a yield from the viewport,
// and the widget is asked first whether it would hold the keys. As a rule it
// has nothing designed for holding them once its viewport lets go, so it says
// no and is passed by: the keys go on up the tree to the first ancestor that
// would hold them, else to the root's default, the home. One made to keep
// them (keeps) says yes and holds, with nothing focused: its keys go to the
// view through the party - the arrows still pan - and its own Escape yields.
//
// EscapeLayer - a layer that keeps an Escape while it is open, as a stage or a
// dialog closing does: the Escape closes it, and no one under it, nor the
// steward, hears of it; closed, it lets every key by. A press anywhere in it
// opens it. It is the native world's, no member.
//
//   var v = new SvgViewer(container, { svg, label?, keeps? })
//   v.root   v.roots { dom, focus }   v.focus  its membership   v.view  the SvgPanZoom
//   v.activate()   v.dispose()
//   var l = new EscapeLayer(branch, host, { title })   l.root  l.body   l.open()  l.isOpen()
//   l.closedNow() → true once, after the Escape that closed it     l.dispose()
// =============================================================================

const _svgViewerOwner = Object.freeze({ toString: () => "svgViewer" });
var _svgViewers = 0;

class SvgViewer {
    constructor(container, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[SvgViewer] a container is required: the one its host lends it");
        var p = params || {}, label = p.label || "A drawing";
        this._keeps = p.keeps === true;
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
        css.addClass(this.view.root, ga_viewer_view);
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

    /** Its viewport let go of the keys: kept, held with nothing focused, only when it was made to keep them; else passed by. */
    wouldHold() { return this._keeps; }

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

const _escapeLayerOwner = Object.freeze({ toString: () => "escapeLayer" });

class EscapeLayer {
    constructor(branch, host, opts) {
        branch.activate(_escapeLayerOwner);
        var self = this;
        this.branch = branch;
        this._title = (opts && opts.title) || "a layer";
        this._closed = false;
        var root = branch.createElement("layer", "div");
        css.addClass(root, ga_layer);
        var head = branch.createElement("head", "div");
        css.addClass(head, ga_layer_head);
        root.appendChild(head);
        var body = branch.createElement("body", "div");
        root.appendChild(body);
        host.appendChild(root);
        this.root = root; this.body = body; this._head = head;
        root.addEventListener("pointerdown", function () { self.open(); });
        // bubbled from what is under it, before the steward on the document: kept, so no one else hears of it
        root.addEventListener("keydown", function (ev) {
            if (ev.key !== "Escape" || ev.defaultPrevented || !self._open) return;
            ev.preventDefault();
            self._closed = true;
            self._set(false);
        });
        this._set(true);
    }
    open() { if (!this._open) this._set(true); }
    isOpen() { return this._open; }
    /** Whether the last Escape closed it: true once, then false until it closes again. */
    closedNow() { var c = this._closed; this._closed = false; return c; }
    _set(open) {
        this._open = open;
        css.toggleClass(this._head, ga_layer_open, open);
        this._head.textContent = this._title + (open ? " - open: an Escape closes it, and is kept" : " - closed: an Escape goes by (a press opens it)");
    }
    dispose() { this.branch.dissolve(); }
}
