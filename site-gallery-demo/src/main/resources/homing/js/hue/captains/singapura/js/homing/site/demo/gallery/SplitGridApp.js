// =============================================================================
// SplitGridApp — the split grid, exercised. One cell to start; what the page
// puts in each cell is its own — a small card of buttons: split it left,
// right, above or below, or remove it — and the grid arranges. Drag the
// dividers. Under the grid, its mirror: the same arrangement drawn from the
// geometry at a scale the slider sets, with a cursor the arrows move from cell
// to cell by the workspace's rule once the mirror has focus — the page marks
// the same cell's card current in the grid. Every change of arrangement and
// every move of the cursor is a line on the log, and the layout as the grid
// reports it is printed under it.
// =============================================================================

const _owner = Object.freeze({ toString: () => "splitGridPage" });

/** What the page puts in a cell: its name and the five ways it can change the grid. A widget by the base's contract. */
class CellWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var root = branch.createElement("cell", "div");
        css.addClass(root, ga_grid_cell);
        var name = branch.createElement("name", "div");
        css.addClass(name, ga_kicker);
        name.textContent = params.id;
        root.appendChild(name);
        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        var self = this;
        [["←", "left"], ["↑", "top"], ["→", "right"], ["↓", "bottom"]].forEach(function (s) {
            var b = new ButtonBuilder().label(s[0]).plain().size(-1).onClick(function () { params.onSplit(params.id, s[1]); });
            row.appendChild(b.build(branch.createElement("split-" + s[1], b.tag)).el);
        });
        var x = new ButtonBuilder().label("×").colour("danger", 0.6).size(-1).onClick(function () { params.onRemove(params.id); });
        row.appendChild(x.build(branch.createElement("remove", x.tag)).el);
        root.appendChild(row);
        this.root = root;
    }
    current(on) { css.toggleClass(this.root, ga_grid_cell_current, !!on); }
}

class SplitGridWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-splitgrid";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Split grid";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "A tree of rows and columns of cells, sharing their space by ratio. The grid arranges; what is in a cell is the "
            + "page's — here, a card that asks the grid to split beside it or remove it. Drag the dividers. The last cell cannot go.";
        el.appendChild(lede);

        var box = branch.createElement("box", "div");
        css.addClass(box, ga_pane_host);
        el.appendChild(box);

        // ── the mirror, and its scale ─────────────────────────────────────
        var mirrorRow = branch.createElement("mirrorRow", "div");
        css.addClass(mirrorRow, ga_buttons);
        var mirrorHost = branch.createElement("mirrorHost", "div");
        mirrorRow.appendChild(mirrorHost);
        el.appendChild(mirrorRow);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        el.appendChild(log);
        var shown = branch.createElement("layout", "pre");
        css.addClass(shown, ga_log);
        el.appendChild(shown);
        var lines = 0;
        function say(line) {
            lines++;
            log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line;
            log.scrollTop = log.scrollHeight;
            shown.textContent = JSON.stringify(self._grid.layout(), null, 1).replace(/\n\s*/g, " ");
        }
        function reflect() { self._mirror.reflect(self._grid.layout(), self._grid.box()); }

        this._grid = new SplitGrid(branch.createBranch("grid"), {
            host: box, minCellPx: 90, layout: { kind: "cell", id: "a" },
            onEvent: function (ev) {
                switch (ev.kind) {
                    case "TracksChanged": say("Tracks     " + (ev.path || "root") + "  " + ev.ratios.map(function (r) { return r.toFixed(2); }).join(" : ")); break;
                    case "Subdivided":    say("Subdivided " + ev.cellId + "  " + ev.side + " → " + ev.newCellId); break;
                    case "Removed":       say("Removed    " + ev.cellId); break;
                    default:              say(ev.kind);
                }
                reflect();
            }
        });
        var grid = this._grid;
        this._widgets = new Map();
        var n = 0;

        this._mirror = new SplitGridMirror(branch.createBranch("mirror"), { host: mirrorHost, scale: 0.25, onEvent: function (ev) {
            if (ev.kind !== "CursorMoved") return;
            say("Cursor     " + ev.cellId + "  by " + ev.by);
            self._widgets.forEach(function (w, id) { w.current(id === ev.cellId); });
        } });
        var scale = this._slider(branch, "scale", "the mirror's scale", 0.1, 0.5, 0.05, 0.25, function (v) { self._mirror.scale(v); return v.toFixed(2); });
        mirrorRow.appendChild(scale);
        var hint = branch.createElement("hint", "span");
        css.addClass(hint, ga_control_readout);
        hint.textContent = "click the mirror, then the arrows move the cursor";
        mirrorRow.appendChild(hint);

        function fill(id) {
            var w = new CellWidget(branch.createBranch("cell-" + id), { id: id,
                onSplit: function (of, side) { fill(grid.subdivide(of, side, String.fromCharCode(98 + (n++ % 24)) + (n > 24 ? n : ""))); },
                onRemove: function (of) { if (grid.cells().length < 2) { say("Refused    the last cell stays"); return; } grid.remove(of); self._widgets.delete(of); } });
            self._widgets.set(id, w);
            grid.cell(id).appendChild(w.root);
        }
        fill("a");
        say("one cell, a");
        this._onResize = function () { reflect(); };
        window.addEventListener("resize", this._onResize);
        requestAnimationFrame(reflect);
        this.root = el;
    }

    /** A labelled range with a readout; onValue draws the readout and does the work. */
    _slider(branch, name, label, min, max, step, value, onValue) {
        var wrap = branch.createElement(name + "-wrap", "div");
        css.addClass(wrap, ga_control);
        var lab = branch.createElement(name + "-label", "span");
        css.addClass(lab, ga_control_label);
        lab.textContent = label;
        var range = branch.createElement(name + "-range", "input");
        range.type = "range";
        css.addClass(range, pv_range);
        range.min = min; range.max = max; range.step = step; range.value = value;
        range.setAttribute("aria-label", label);
        var out = branch.createElement(name + "-out", "span");
        css.addClass(out, ga_control_readout);
        function draw() { out.textContent = onValue(Number(range.value)); }
        range.addEventListener("input", draw);
        wrap.appendChild(lab);
        wrap.appendChild(range);
        wrap.appendChild(out);
        draw();
        return wrap;
    }

    dispose() { window.removeEventListener("resize", this._onResize); this._mirror.dispose(); this._grid.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new SplitGridWidget(domOpsParty.createBranch("splitGridPage"), params).root);
}
