// =============================================================================
// TaxonomyWidget — what every widget of the taxonomy workbench is to its host,
// said once; a widget extends it and builds what it shows into `body`.
//
// A self-contained widget: made with the container its host lends it and its
// params, and nothing else; its DomOps and focus parties its own, offered as
// roots for its host to graft. It joins the node-selection party after it is
// made, and is told what is picked through selected(id) - "" for nothing -
// whoever picked it; it picks through pick(id). Not joined, a pick is its own.
// It reads the taxonomy through `taxonomy`, the one index over TAXONOMY.
//
// THE KEYS. A logical member of the keyboard's party; what takes keys natively
// inside it - the tree, a button - is the native world's. A press anywhere in
// it claims the keys for it. Given the keys by a call, they go on to `keysTo`
// when it names something that takes them; Escape that nothing native took
// gives them back.
//
//   widget.root  widget.roots { dom, focus }  widget.focus  widget.branch  widget.body
//   widget.pick(id)  widget.picked()  widget.selected(id)  widget.taxonomy
//   widget.el(name, tag, cls, into?, text?)              an element on its own branch
//   widget.mint(branch, name, tag, cls, into?, text?)    an element on a branch of its
//   widget.fresh()                                       a view branch, the last one dissolved
//   widget.link(branch, name, id, into, label?)          a node named so it can be picked
//   widget.join(given)  widget.leave()  widget.activate()  widget.granted(by)  widget.keyDown(ev)  widget.dispose()
// =============================================================================

var _taxonomyWidgets = 0;

class TaxonomyWidget {
    constructor(container, name, label) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[" + name + "] a container is required: the one its host lends it");
        var n = name + "-" + (++_taxonomyWidgets);
        this._name = name;
        this._dom = domOpsParties.mobile(n);
        this._dom.activate(Object.freeze({ toString: function () { return name; } }));
        var root = this._dom.createElement("root", "div");
        css.addClass(root, wg_fill);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", label);
        var body = this._dom.createElement("body", "div");
        css.addClass(body, tx_root);
        root.appendChild(body);
        this.root = root;
        this.body = body;
        this.keysTo = null;
        this._focusParty = focusParties.mobile(n);
        this.focus = this._focusParty.root.join(name, this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: this._dom, focus: this._focusParty });
        this._member = null;
        this._picked = "";
        this.taxonomy = TaxonomyIndex.the();
        this._view = null;
        this._views = 0;
        this.alive = true;
        container.appendChild(root);
    }

    get branch() { return this._dom; }

    /** What is picked now: a node's token, or "". */
    picked() { return this._picked; }

    /** Told what is picked; a widget shows it. */
    selected(id) {}

    /** Called as it is disposed, before its branch goes. */
    disposed() {}

    /** A node picked here: told to the party, which tells everyone - this widget too. */
    pick(id) {
        if (this._member) { this._member.tell({ kind: "Select", id: id }); return; }
        this._picked = id;
        this.selected(id);
    }

    join(given) {
        if (this._member) throw new Error("[" + this._name + "] joined already: leave first");
        var self = this, party = (given || {})[NODE_SELECTION.name];
        if (!party) return;
        this._member = party.join(this._name, { Selected: function (m) { self._picked = m.id; self.selected(m.id); } });
        this._member.tell({ kind: "CurrentRequested" });
    }

    leave() {
        if (this._member) { this._member.leave(); this._member = null; }
    }

    el(name, tag, cls, into, text) { return this.mint(this._dom, name, tag, cls, into, text); }

    mint(branch, name, tag, cls, into, text) {
        var e = branch.createElement(name, tag);
        if (cls) css.addClass(e, cls);
        if (text != null) e.textContent = text;
        if (into) into.appendChild(e);
        return e;
    }

    /** A branch for one showing of what is picked: the last one dissolved, its elements with it. */
    fresh() {
        if (this._view) this._view.dissolve();
        this._view = this._dom.createBranch("view" + (++this._views));
        this._view.activate(this);
        return this._view;
    }

    /** A node, named so it can be picked: a button, drawn as a link; its token its title. */
    link(branch, name, id, into, label) {
        var self = this, b = this.mint(branch, name, "button", tx_link, into, label != null ? label : this.taxonomy.label(id));
        b.type = "button";
        b.title = id;
        b.addEventListener("click", function () { self.pick(id); });
        return b;
    }

    activate() { Keys.claim(this.focus); }

    granted(by) { if (by !== "native" && this.keysTo && typeof this.keysTo.focus === "function") this.keysTo.focus(); }

    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return false;
    }

    dispose() {
        this.alive = false;
        this.leave();
        this.disposed();
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
