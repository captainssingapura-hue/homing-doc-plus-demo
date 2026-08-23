package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * RFC 0050 Appendix A — the Minesweeper game domain, host-agnostic:
 * {@code createMinesweeperGame(config)} returns {@code {adapter, act, reset}}.
 * The board is a Relation of display symbols; the RelationGrid renders it
 * read-only ({@code editable:false}) and routes every action key to
 * {@code act(key, pk, column)} via the grid's {@code onAction} contract.
 * Reveals / flags / chords / restarts all flow back through the adapter
 * feed — the grid's direct update path — so a flood fill batches into one
 * animation frame. Keymap per the appendix: Enter/Space reveal, F flag,
 * C chord, R restart; arrows stay with the grid's own shallow keyboard.
 */
public record MinesweeperGame() implements DomModule<MinesweeperGame> {

    public record createMinesweeperGame() implements Exportable._Constant<MinesweeperGame> {}

    public static final MinesweeperGame INSTANCE = new MinesweeperGame();

    @Override
    public ImportsFor<MinesweeperGame> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<MinesweeperGame> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createMinesweeperGame()));
    }
}
