package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.demo.es.grid.DishListDemoApp;
import hue.captains.singapura.js.homing.demo.es.grid.MinesweeperDemoApp;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;

import java.util.List;

/**
 * L1 sub-catalogue under {@link DemoStudio} for the RFC 0050 Relation Grid's
 * two companion demos — the Dish List (the plain "Excel baseline" case) and
 * Minesweeper (the keyboard-only boundary case on the grid's action variant).
 * Both consume the upstream {@code homing-relation-grid} primitive; both are
 * built in parallel with the core phases, each at whatever functionality the
 * core supports so far.
 *
 * <p>Both are also SMALL on purpose, which is their one blind spot: neither has
 * a row below the fold, so neither can express a scrolling defect. The Table
 * Workbench covers that — a workspace of grids in shapes these two never take,
 * at {@code ?app=genericWorkspace&ws_kind=tableWorkbench}. It is reached by URL
 * rather than by a tile here because {@code GenericWorkspace} already holds its
 * one catalogue position under the path axiom (see {@link DemoStudio}).</p>
 */
public record GridDemosCatalogue() implements L1_Catalogue<DemoStudio, GridDemosCatalogue> {

    public static final GridDemosCatalogue INSTANCE = new GridDemosCatalogue();

    @Override public DemoStudio parent()  { return DemoStudio.INSTANCE; }
    @Override public String     name()    { return "The Relation Grid"; }
    @Override public String     summary() { return "RFC 0050's two triangulating companion demos on the generic table primitive: the Dish List exercises the full spreadsheet surface (view ops, selection, editing, the live direct-update column); Minesweeper proves the same grid drives a keyboard-only game through the onAction variant. Both fit on screen by design — for grids that do not, the Table Workbench benches five oversized shapes at ?app=genericWorkspace&ws_kind=tableWorkbench."; }
    @Override public String     badge()   { return "GRID"; }
    @Override public String     icon()    { return "🧮"; }

    @Override public List<Entry<GridDemosCatalogue>> leaves() {
        return List.of(
                Entry.of(this, new Navigable<>(
                        DishListDemoApp.INSTANCE,
                        new DishListDemoApp.Params(),
                        "Dish List",
                        "The plain Excel-like case: sort / filter / hide / reorder as pure remaps, "
                      + "click + keyboard selection with Excel range semantics, in-cell editing "
                      + "(text, raw-number, enum select) committing to the Relation, and a live "
                      + "popularity column ticking through the rAF-batched direct update path.")),
                Entry.of(this, new Navigable<>(
                        MinesweeperDemoApp.INSTANCE,
                        new MinesweeperDemoApp.Params(),
                        "Minesweeper",
                        "The boundary case: the same grid, editing disabled, every action key "
                      + "dispatched via onAction. Arrows move the grid's own cursor; Enter/Space "
                      + "reveal, F flags, C chords, R restarts — a flood fill lands as one "
                      + "batched frame. Fully playable without a pointer."))
        );
    }
}
