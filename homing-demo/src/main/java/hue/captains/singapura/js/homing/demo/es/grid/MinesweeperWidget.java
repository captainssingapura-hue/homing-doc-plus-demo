package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.demo.css.GridDemoStyles;
import hue.captains.singapura.js.homing.grid.RelationGridModule;
import hue.captains.singapura.js.homing.studio.base.widget.DocWidget;

import java.util.List;

/**
 * RFC 0050 Appendix A — Minesweeper, keyboard-only, on the RelationGrid's
 * ACTION variant: {@code editable:false} turns Enter/Space/F/C/R into
 * {@code onAction(key, pk, column)} dispatches that {@link MinesweeperGame}
 * turns into moves; arrows stay with the grid's own shallow keyboard. The
 * cells are a custom mine-tile factory (the GridCellContract's minimal
 * surface — no StockCells needed), and every reveal flows back through the
 * adapter feed into the grid's direct update path: a flood fill lands as
 * ONE batched frame.
 */
public final class MinesweeperWidget extends DocWidget<MinesweeperWidget.Params,
                                                       MinesweeperWidget> {

    public static final MinesweeperWidget INSTANCE = new MinesweeperWidget();
    private MinesweeperWidget() {}

    /** No URL params — 9x9, 10 mines. */
    public record Params() implements Widget._Param {}

    private record mountInto() implements Widget._MountInto<Params, MinesweeperWidget> {}

    @Override public String simpleName() { return "minesweeper-widget"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title()      { return "Minesweeper"; }

    @Override
    protected Widget._MountInto<Params, MinesweeperWidget> mountInto() {
        return new mountInto();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(
                        List.of(new RelationGridModule.RelationGrid()),
                        RelationGridModule.INSTANCE),
                new ModuleImports<>(
                        List.of(new MinesweeperGame.createMinesweeperGame()),
                        MinesweeperGame.INSTANCE),
                new ModuleImports<>(
                        List.of(new GridDemoStyles.ms_wrap(), new GridDemoStyles.ms_host(),
                                new GridDemoStyles.ms_status(), new GridDemoStyles.ms_progress(),
                                new GridDemoStyles.ms_tile(), new GridDemoStyles.gd_hint()),
                        GridDemoStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> bodyJs() {
        return List.of(
                "    var owner = Object.freeze({ toString: function(){ return 'minesweeperDemo'; } });",
                "    var b = branch.createBranch('mines');",
                "    b.activate(owner);",
                "",
                "    var wrap = b.createElement('wrap', 'div');",
                "    css.addClass(wrap, ms_wrap);",
                "    parent.appendChild(wrap);",
                "",
                "    var hint = b.createElement('hint', 'div');",
                "    css.addClass(hint, gd_hint);",
                "    hint.textContent = 'Keyboard only: arrows move \\u00b7 Enter/Space reveal \\u00b7 '",
                "                     + 'F flag \\u00b7 C chord \\u00b7 R restart';",
                "    wrap.appendChild(hint);",
                "",
                "    // ms_host hides the header band via a nested rule — a game board",
                "    // has no headers, and the hide is a style concern, not a DOM poke.",
                "    var host = b.createElement('host', 'div');",
                "    css.addClass(host, ms_host);",
                "    wrap.appendChild(host);",
                "",
                "    var status = b.createElement('status', 'div');",
                "    css.addClass(status, ms_status);",
                "    wrap.appendChild(status);",
                "    var progress = b.createElement('progress', 'div');",
                "    css.addClass(progress, ms_progress);",
                "    wrap.appendChild(progress);",
                "",
                "    var game = createMinesweeperGame({",
                "        size: 9, mines: 10,",
                "        onStatus:   function (t) { status.textContent = t; },",
                "        onProgress: function (t) { progress.textContent = t; }",
                "    });",
                "",
                "    // A custom mine-tile cell — the GridCellContract's minimal surface:",
                "    // fixed square tiles, no editing (the grid runs editable:false).",
                "    function mineCellFactory() {",
                "        var el = null, val = null;",
                "        return {",
                "            render: function (host2, v) {",
                "                el = host2; val = v;",
                "                css.addClass(el, ms_tile);",
                "                el.textContent = v;",
                "            },",
                "            update: function (v) { val = v; if (el) el.textContent = v; },",
                "            onSelect: function () {},",
                "            getValue: function () { return val; },",
                "            getValueToCopy: function () { return val == null ? '' : String(val); },",
                "            dispose: function () { el = null; }",
                "        };",
                "    }",
                "",
                "    var cellsB = b.createBranch('cells');",
                "    cellsB.activate(owner);",
                "",
                "    var grid = new RelationGrid({",
                "        container: host,",
                "        branch: cellsB,",
                "        adapter: game.adapter,",
                "        editable: false,                       // the ACTION variant",
                "        cellFactory: mineCellFactory,",
                "        onAction: game.act",
                "    });",
                "",
                "    grid.focus();"
        );
    }
}
