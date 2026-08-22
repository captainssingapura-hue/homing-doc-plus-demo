package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.demo.css.GridDemoStyles;
import hue.captains.singapura.js.homing.grid.RelationGridModule;
import hue.captains.singapura.js.homing.grid.StockCellsModule;
import hue.captains.singapura.js.homing.studio.base.widget.DocWidget;

import java.util.List;

/**
 * RFC 0050 — the Dish List demo widget: the plain "Excel baseline" case live
 * on the RelationGrid. One grid over {@link DishListRelation}, a toolbar
 * driving every Phase-3 view command, editing per Phase 5 (TextCell inputs,
 * NumberCell raw-form editing, EnumCell select over the six styles), and a
 * live popularity ticker exercising the direct {@code (PK,col)} update path.
 *
 * <p>Keyboard: arrows / Shift+arrows / Ctrl+A select (Phase 4); Enter or F2
 * edits, Enter commits to the Relation, Escape cancels (Phase 5); sorting
 * while an edit is open demonstrates D7 — the remap defers until commit.</p>
 */
public final class DishListDemoWidget extends DocWidget<DishListDemoWidget.Params,
                                                        DishListDemoWidget> {

    public static final DishListDemoWidget INSTANCE = new DishListDemoWidget();
    private DishListDemoWidget() {}

    /** No URL params — the Dish List is the fixture. */
    public record Params() implements Widget._Param {}

    private record mountInto() implements Widget._MountInto<Params, DishListDemoWidget> {}

    @Override public String simpleName() { return "dish-list-demo-widget"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title()      { return "Dish List"; }

    @Override
    protected Widget._MountInto<Params, DishListDemoWidget> mountInto() {
        return new mountInto();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(
                        List.of(new RelationGridModule.RelationGrid()),
                        RelationGridModule.INSTANCE),
                new ModuleImports<>(
                        List.of(new StockCellsModule.TextCell(),
                                new StockCellsModule.NumberCell(),
                                new StockCellsModule.EnumCell()),
                        StockCellsModule.INSTANCE),
                new ModuleImports<>(
                        List.of(new DishListRelation.createDishListRelation()),
                        DishListRelation.INSTANCE),
                new ModuleImports<>(
                        List.of(new GridDemoStyles.gd_wrap(), new GridDemoStyles.gd_hint(),
                                new GridDemoStyles.gd_bar(), new GridDemoStyles.gd_btn(),
                                new GridDemoStyles.gd_host(), new GridDemoStyles.gd_status()),
                        GridDemoStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> bodyJs() {
        return List.of(
                "    var owner = Object.freeze({ toString: function(){ return 'dishListDemo'; } });",
                "    var b = branch.createBranch('dish');",
                "    b.activate(owner);",
                "",
                "    var wrap = b.createElement('wrap', 'div');",
                "    css.addClass(wrap, gd_wrap);",
                "    parent.appendChild(wrap);",
                "",
                "    var hint = b.createElement('hint', 'div');",
                "    css.addClass(hint, gd_hint);",
                "    hint.textContent = 'Click / arrows select · Shift extends · Ctrl adds a range · '",
                "                     + 'Ctrl+A all · Enter or F2 edits, Enter commits, Escape cancels · '",
                "                     + 'Ctrl+C copies the active range as raw TSV · Del clears the selected '",
                "                     + 'cells · edit a homogeneous selection to fill it in one commit · '",
                "                     + 'sort while editing to watch D7 defer the remap';",
                "    wrap.appendChild(hint);",
                "",
                "    var bar = b.createElement('bar', 'div');",
                "    css.addClass(bar, gd_bar);",
                "    wrap.appendChild(bar);",
                "",
                "    var host = b.createElement('host', 'div');",
                "    css.addClass(host, gd_host);",
                "    wrap.appendChild(host);",
                "",
                "    var status = b.createElement('status', 'div');",
                "    css.addClass(status, gd_status);",
                "    wrap.appendChild(status);",
                "    function log(m) { status.textContent = m + '\\n' + status.textContent; }",
                "",
                "    var relation = createDishListRelation();",
                "    var STYLES = ['Chinese', 'French', 'English', 'German', 'USA', 'Italian'];",
                "",
                "    // The CELLS branch — handed to the grid; the layout stays raw-DOM",
                "    // inside the primitive (the RFC 0050 two-branch model).",
                "    var cellsB = b.createBranch('cells');",
                "    cellsB.activate(owner);",
                "",
                "    var grid = new RelationGrid({",
                "        container: host,",
                "        branch: cellsB,",
                "        adapter: relation,",
                "        cellFactory: function (column, value) {",
                "            if (column === 'style') return new EnumCell({ options: STYLES });",
                "            if (column === 'price') return new NumberCell({ format: function (v) {",
                "                return '$' + Number(v).toFixed(2); } });",
                "            return (typeof value === 'number') ? new NumberCell() : new TextCell();",
                "        },",
                "        onEditCommitted: function (pk, col, v) { log('committed ' + pk + '.' + col + ' = ' + v); },",
                "        onViewChanged: function (kind) { log('view changed: ' + kind); },",
                "        // Ctrl+C -> raw-value TSV of the active range (Phase 6); the demo",
                "        // hands it to the system clipboard and mirrors it in the log.",
                "        onCopy: function (tsv) {",
                "            if (navigator.clipboard) navigator.clipboard.writeText(tsv).catch(function () {});",
                "            log('copied (raw TSV):\\n' + tsv);",
                "        }",
                "    });",
                "",
                "    var btnSeq = 0;",
                "    function btn(label, fn) {",
                "        var x = b.createElement('btn' + (++btnSeq), 'button');",
                "        css.addClass(x, gd_btn);",
                "        x.textContent = label;",
                "        x.addEventListener('click', function () { fn(); grid.focus(); });",
                "        bar.appendChild(x);",
                "    }",
                "    btn('sort price \\u25B2', function () { grid.sortBy('price', 'asc'); });",
                "    btn('sort price \\u25BC', function () { grid.sortBy('price', 'desc'); });",
                "    btn('clear sort',        function () { grid.sortBy(null); });",
                "    btn('filter < 700 cal',  function () { grid.filterRows(function (pk, get) {",
                "        return get('calories') < 700; }); });",
                "    btn('clear filter',      function () { grid.clearFilter(); });",
                "    btn('hide calories',     function () { grid.hideColumn('calories'); });",
                "    btn('show calories',     function () { grid.showColumn('calories'); });",
                "    btn('price first',       function () { grid.reorderColumn('price', 0); });",
                "    btn('copy selection',    function () {",
                "        var tsv = grid.copySelection();",
                "        if (navigator.clipboard) navigator.clipboard.writeText(tsv).catch(function () {});",
                "        log('copied (raw TSV):\\n' + tsv);",
                "    });",
                "    btn('delete selected rows', function () {",
                "        var pks = grid.deleteSelectedRows();",
                "        log(pks.length ? 'deleted rows: ' + pks.join(', ') : 'nothing selected');",
                "    });",
                "",
                "    // The live column: domain pushes through the direct (PK,col) path,",
                "    // rAF-batched — sort by popularity while it ticks.",
                "    var ticking = null;",
                "    btn('toggle live popularity', function () {",
                "        if (ticking) { clearInterval(ticking); ticking = null; log('tick off'); return; }",
                "        log('tick on');",
                "        ticking = setInterval(function () {",
                "            relation.pks().forEach(function (pk) {",
                "                var v = relation.get(pk, 'popularity');",
                "                relation.push(pk, 'popularity',",
                "                    Math.max(0, v + Math.round(Math.random() * 10 - 5)));",
                "            });",
                "        }, 400);",
                "    });",
                "",
                "    grid.focus();"
        );
    }
}
