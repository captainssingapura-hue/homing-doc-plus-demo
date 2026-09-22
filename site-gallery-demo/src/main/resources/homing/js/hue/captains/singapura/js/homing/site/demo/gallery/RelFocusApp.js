// =============================================================================
// RelFocusApp — the relation grid and the relation tree under the focus
// model, wrapped and unwrapped. Both are the native world: a host element
// with tabindex, their own keys on their own element, heavily worked for the
// keyboard — the cursor, the selection, deep control of a cell, the fold and
// unfold. Nothing in them joins the party, and nothing in them should have
// to. UNWRAPPED: a tree and a grid straight on the page, outside every member
// root — a press in them claims nothing, the steward goes dormant on the host
// and the keys are theirs; whoever held resumes when the host lets go.
// WRAPPED: a tree and a grid each inside a panel of the logical world, a
// member holding a branch — a press in the grid claims the panel, the
// innermost member, and the panel is dormant while the host has the focus;
// the panel wires the seam itself: an Escape the grid did not want blurs the
// host, and the panel has the keys; on granted the panel puts the focus back
// in its host. A leaf beside them holds the keys when nothing is focused, so
// the arrows show where they go. The monitors beside: the tree, the lamp,
// the log. The conflicts to look for are the ones that would show here.
// =============================================================================

const _owner = Object.freeze({ toString: () => "relFocusPage" });

/** A panel of the logical world wrapping a native keyboard host: a grid or a tree, built by `mount(host, branch)`. */
class RelPanel extends Panel {
    constructor(branch, host, focusBranch, name, mount) {
        super(branch, host, focusBranch, name, true);
        var self = this;
        var box = branch.createElement("box", "div");
        css.addClass(box, ga_rel_host);
        this.root.appendChild(box);
        this._box = box;
        this._widget = mount(box, branch.createBranch("widget-" + branch.name));
        // the seam, wired by the panel: an Escape the host let through blurs it — the panel, the holder since the
        // press, has the keys again; a second Escape is the panel's own and yields
        this.root.addEventListener("keydown", function (ev) {
            if (ev.key !== "Escape" || !box.contains(ev.target)) return;
            ev.target.blur();
            ev.preventDefault();
            ev.stopPropagation();
        });
    }
    /** Granted, however: the host takes the native focus, so the grid's or the tree's keys are theirs at once. */
    granted(by) { super.granted(by); this._widget.focus(); }
    dispose() { this._widget.destroy(); super.dispose(); }
}

class RelFocusWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-rel-grid · homing-ui-focus";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Relations in focus";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "The relation tree and the relation grid, both the native world — a focusable host, their own keys, worked hard "
            + "for the keyboard — under the focus model, wrapped and unwrapped. Unwrapped, straight on the page: a press claims nothing, "
            + "the steward goes dormant on the host, the keys are theirs, and whoever held resumes when the host lets go. Wrapped in a "
            + "panel of the logical world: a press claims the panel, the innermost member; the panel is dormant while the host has the "
            + "focus; an Escape the grid did not want blurs the host and the panel has the keys; the next Escape yields. The leaf holds "
            + "the keys when nothing is focused. Arrows, Enter on a cell, Escape in the editor, a header drag, Ctrl+C: every one of them "
            + "should be the grid's, and none of them should move the holder.";
        el.appendChild(lede);

        var row = branch.createElement("row", "div");
        css.addClass(row, ga_focus);
        var scene = branch.createElement("scene", "div");
        css.addClass(scene, ga_focus_scene);
        var monitorBox = branch.createElement("monitorBox", "div");
        css.addClass(monitorBox, ga_focus_monitor);
        row.appendChild(scene);
        row.appendChild(monitorBox);
        el.appendChild(row);

        var domain = branch.createBranch("domain");
        domain.activate(_owner);
        var store = new BooksStore();
        this._relations = [];
        function tree(box, b) {
            var relation = new ShelfTreeRelation(store, { branch: domain.createBranch("cells-" + b.name) });
            self._relations.push(relation);
            return new RelTree({ container: box, branch: b, relation: relation, label: "Shelves", folder: true,
                                 ask: function (question, mask) { return relation.answer(question, mask); } });
        }
        function grid(box, b) {
            var relation = new BooksRelation(store, { branch: domain.createBranch("cells-" + b.name) });
            self._relations.push(relation);
            var g = new RelGrid({ container: box, branch: b, relation: relation, header: { show: true, sticky: true }, label: "Books" });
            g.setColumnWidths({ title: 180, author: 120, year: 56, rating: 56 });
            return g;
        }

        // unwrapped: straight on the page, outside every member root
        var loose = branch.createElement("loose", "div");
        css.addClass(loose, ga_rel_group);
        var looseHead = branch.createElement("looseHead", "div");
        css.addClass(looseHead, ga_panel_header);
        looseHead.textContent = "unwrapped — outside every container";
        loose.appendChild(looseHead);
        var looseTree = branch.createElement("looseTree", "div");
        css.addClass(looseTree, ga_rel_host);
        var looseGrid = branch.createElement("looseGrid", "div");
        css.addClass(looseGrid, ga_rel_host);
        loose.appendChild(looseTree);
        loose.appendChild(looseGrid);
        scene.appendChild(loose);
        this._widgets = [tree(looseTree, branch.createBranch("tree-loose")), grid(looseGrid, branch.createBranch("grid-loose"))];

        // wrapped: each in a panel of the logical world, a member holding a branch; and a leaf beside them
        var top = focusParty.root;
        var panelT = new RelPanel(branch.createBranch("panel-t"), scene, top, "panel T — a tree", tree);
        var panelG = new RelPanel(branch.createBranch("panel-g"), scene, top, "panel G — a grid", grid);
        var leaf = new Leaf(branch.createBranch("x"), scene, top, "x");
        this._parts = [panelT, panelG, leaf];

        this._monitor = new FocusMonitor(branch.createBranch("monitor"), { host: monitorBox });
        this._steward = new StewardMonitor(branch.createBranch("steward"), { host: monitorBox });

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        el.appendChild(log);
        var lines = 0;
        function say(line) {
            lines++;
            log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line;
            log.scrollTop = log.scrollHeight;
        }
        function nameOf(id) { var m = focusParty.find(id); return m ? m.path : id; }
        this._offKb = KeyboardStewardInstance.on(function (ev) {
            if (ev.kind === "Granted") say("Granted   " + nameOf(ev.id) + (ev.by === "claim" ? "" : "  by " + ev.by));
            else if (ev.kind === "Taken") say("Taken     " + nameOf(ev.id) + "  by " + nameOf(ev.by));
            else say("Released  " + nameOf(ev.id));
        });
        say("two trees, two grids, one leaf; press into one and use its keys");
        this.root = el;
    }

    dispose() {
        this._offKb(); this._steward.dispose(); this._monitor.dispose();
        this._parts.forEach(function (p) { p.dispose(); });
        this._widgets.forEach(function (w) { w.destroy(); });
        this._relations.forEach(function (r) { r.dispose(); });
    }
}

function appMain(el, params) {
    el.appendChild(new RelFocusWidget(domOpsParty.createBranch("relFocusPage"), params).root);
}
