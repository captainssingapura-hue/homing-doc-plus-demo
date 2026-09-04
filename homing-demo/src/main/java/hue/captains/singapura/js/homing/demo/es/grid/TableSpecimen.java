package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.css.GridDemoStyles;
import hue.captains.singapura.js.homing.grid.RelationGridModule;
import hue.captains.singapura.js.homing.grid.StockCellsModule;

import java.util.ArrayList;
import java.util.List;

/**
 * The body shared by every Table Workbench specimen — one
 * {@link RelationGridModule} over {@link TableSpecimenRelation}, differing
 * only in shape and header band.
 *
 * <p>Each specimen is nonetheless its OWN widget class rather than one class
 * registered five times with different defaults. The workspace keys tabs,
 * pins, codecs and persistence by widget simple name
 * ({@code WidgetEntriesJson}, {@code PinnedTabSpawnerModule}), so five entries
 * sharing a class would be five tabs the workspace cannot tell apart on
 * restore. The shape is a Java constant, the body is written once, here.</p>
 *
 * <p>Every specimen carries a live readout — cursor position, the scrollport's
 * offsets, the band of rows actually on screen, and a plain VISIBLE / OFF
 * SCREEN verdict. That is the point of the bed: "the cursor moved" and "the
 * viewport followed" are separate claims, and only the second one was ever
 * broken.</p>
 */
final class TableSpecimen {

    private TableSpecimen() {}

    /** Header band postures a specimen can take. */
    static final String STICKY = "sticky";      // frozen: the follow insets under it
    static final String PLAIN  = "plain";       // shown, scrolls away: no inset
    static final String NONE   = "none";        // no band at all (Minesweeper-shaped)

    /** Imports every specimen body needs. */
    static List<ModuleImports<? extends Importable>> imports() {
        return List.of(
                new ModuleImports<>(
                        List.of(new RelationGridModule.RelationGrid()),
                        RelationGridModule.INSTANCE),
                new ModuleImports<>(
                        List.of(new StockCellsModule.TextCell(),
                                new StockCellsModule.NumberCell()),
                        StockCellsModule.INSTANCE),
                new ModuleImports<>(
                        List.of(new TableSpecimenRelation.createSpecimenRelation()),
                        TableSpecimenRelation.INSTANCE),
                new ModuleImports<>(
                        List.of(new GridDemoStyles.tw_root(), new GridDemoStyles.tw_host(),
                                new GridDemoStyles.tw_readout(), new GridDemoStyles.gd_hint(),
                                new GridDemoStyles.gd_bar(), new GridDemoStyles.gd_btn()),
                        GridDemoStyles.INSTANCE)
        );
    }

    /**
     * The {@code construct(branch, params)} body for one specimen.
     *
     * @param label what this specimen is called, in its own hint line
     * @param rows  row count
     * @param cols  column count — the first four are named, the rest monthly
     * @param band  {@link #STICKY} | {@link #PLAIN} | {@link #NONE}
     * @param note  what this specimen is FOR
     */
    static List<String> bodyJs(String label, int rows, int cols, String band, String note) {
        var js = new ArrayList<String>(List.of(
                "    var owner = Object.freeze({ toString: function(){ return 'tableSpecimen'; } });",
                "    var ROWS = " + rows + ", COLS = " + cols + ", BAND = '" + band + "';",
                "    var LABEL = " + quote(label) + ", NOTE = " + quote(note) + ";",
                "",
                "    var root = branch.createElement('root', 'div');",
                "    css.addClass(root, tw_root);",
                "",
                "    var hint = branch.createElement('hint', 'div');",
                "    css.addClass(hint, gd_hint);",
                "    hint.textContent = LABEL + ' \\u2014 ' + ROWS + ' rows \\u00D7 ' + COLS",
                "                     + ' cols, header ' + BAND + ' \\u00B7 ' + NOTE;",
                "    root.appendChild(hint);",
                "",
                "    var bar = branch.createElement('bar', 'div');",
                "    css.addClass(bar, gd_bar);",
                "    root.appendChild(bar);",
                "",
                "    var host = branch.createElement('host', 'div');",
                "    css.addClass(host, tw_host);",
                "    root.appendChild(host);",
                "",
                "    var readout = branch.createElement('readout', 'div');",
                "    css.addClass(readout, tw_readout);",
                "    root.appendChild(readout);",
                "",
                "    // The CELLS branch — the grid's other half of the two-branch model;",
                "    // the layout stays raw-DOM inside the primitive.",
                "    var cellsB = branch.createBranch('cells');",
                "    cellsB.activate(owner);",
                "",
                "    var relation = createSpecimenRelation({ rows: ROWS, cols: COLS });",
                "    var grid = new RelationGrid({",
                "        container: host,",
                "        branch: cellsB,",
                "        adapter: relation,",
                "        editable: false,",
                "        label: LABEL + ': ' + ROWS + ' rows by ' + COLS + ' columns',",
                "        header: { show: BAND !== 'none', sticky: BAND === 'sticky' },",
                "        cellFactory: function (column, value) {",
                "            return (typeof value === 'number') ? new NumberCell() : new TextCell();",
                "        },",
                "        onCursorMoved:      function () { report(); },",
                "        onSelectionChanged: function () { report(); }",
                "    });",
                "",
                "    // The instrument. Cursor and viewport are reported separately and the",
                "    // verdict is spelled out, because the defect this bed exists for was",
                "    // exactly the two of them disagreeing.",
                "    function report() {",
                "        if (!grid) return;      // boot fires onCursorMoved before the assignment",
                "        var c = JSON.parse(grid.cursor()), maps = grid.viewMaps();",
                "        var at = c ? maps.locate(c.pk, c.column) : null;",
                "        var first = -1, last = -1;",
                "        var table = host.getElementsByTagName('table')[0];",
                "        var body = table ? table.tBodies[0] : null;",
                "        if (body && body.rows.length) {",
                "            var top = host.getBoundingClientRect().top, bottom = top + host.clientHeight;",
                "            for (var r = 0; r < body.rows.length; r++) {",
                "                var rr = body.rows[r].getBoundingClientRect();",
                "                if (rr.bottom > top && rr.top < bottom) {",
                "                    if (first < 0) first = r;",
                "                    last = r;",
                "                }",
                "            }",
                "        }",
                "        var seen = at && first >= 0 && at.i >= first && at.i <= last;",
                "        readout.textContent =",
                "            'cursor row ' + (at ? at.i : '\\u2014') + ', col ' + (at ? at.j : '\\u2014')",
                "          + '   |   scrollTop ' + Math.round(host.scrollTop)",
                "          + ', scrollLeft ' + Math.round(host.scrollLeft)",
                "          + '   |   rows on screen ' + first + '\\u2013' + last",
                "          + '   |   ' + (seen ? 'cursor VISIBLE' : 'cursor OFF SCREEN');",
                "    }",
                "",
                "    var btnSeq = 0;",
                "    function btn(text, fn) {",
                "        var x = branch.createElement('btn' + (++btnSeq), 'button');",
                "        css.addClass(x, gd_btn);",
                "        x.textContent = text;",
                "        x.addEventListener('click', function () { fn(); grid.focus(); report(); });",
                "        bar.appendChild(x);",
                "    }",
                "    // selectCell follows unconditionally: a programmatic destination is still",
                "    // a destination, even though the keyboard is on the button, not the grid.",
                "    btn('first row', function () {",
                "        grid.selectCell(relation.pks()[0], relation.columns()[0]);",
                "    });",
                "    btn('middle row', function () {",
                "        var pks = relation.pks();",
                "        grid.selectCell(pks[Math.floor(pks.length / 2)], relation.columns()[0]);",
                "    });",
                "    btn('last row, last column', function () {",
                "        var pks = relation.pks(), cs = relation.columns();",
                "        grid.selectCell(pks[pks.length - 1], cs[cs.length - 1]);",
                "    });",
                "    // Desync on purpose, then prove the follow can be demanded back.",
                "    btn('scroll away (keep cursor)', function () { host.scrollTop = 0; host.scrollLeft = 0; });",
                "    btn('re-reveal cursor', function () { grid.revealCursor(); });",
                "    btn('sort by region', function () { grid.sortBy('region', 'asc'); });",
                "    btn('clear sort',     function () { grid.sortBy(null); });",
                "",
                "    host.addEventListener('scroll', function () { report(); });",
                "    // NOT report() straight away: the chrome attaches root AFTER construct",
                "    // returns, so until the next frame every rect is zero and the readout",
                "    // would open by claiming a plainly visible cursor is off screen.",
                "    if (typeof requestAnimationFrame === 'function') requestAnimationFrame(report);",
                "    else report();",
                "",
                "    // Focusing from setActive is a WORKAROUND, and knowingly against the",
                "    // documented contract — FocusManager calls setActive pure lifecycle that",
                "    // 'never focuses', and the sanctioned channel is tab.defaultActivation.",
                "    // But nothing in the workspace ever populates defaultActivation (the",
                "    // mounter forwards setActive and partyDeregister and not that), so this",
                "    // is the only way a keyboard-driven widget gets the keys. It works on the",
                "    // shallow->deep EDGE only: re-entering an already-deep pane fires no",
                "    // setActive, so focus that has left cannot be won back from the keyboard.",
                "    // See outbox/defect-widget-default-activation.md.",
                "    return { root: root, setActive: function (active) {",
                "        if (active) { grid.focus(); report(); }   // a resized pane moves the band",
                "    } };"
        ));
        return List.copyOf(js);
    }

    /** A JS string literal — the labels are ours, but quoting them is free. */
    private static String quote(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }
}
