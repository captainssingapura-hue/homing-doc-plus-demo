package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panes.PaneStyles;
import hue.captains.singapura.js.homing.ui.panes.TabChipModule;
import hue.captains.singapura.js.homing.ui.panes.TabDragModule;

import java.util.List;

/**
 * The tear-off lab's stage and chart. The stage: a strip of real chips over a
 * sunken box, the band's edges dashed around it, and a drag that asks the
 * page's {@code TabTear} what phase it is in at every move and every frame -
 * on the rail it slides along the row by {@code TabDrag}, torn it is lifted
 * off and left waiting at the breach, settled it jumps to the hand and
 * follows. The hand, its path in the phase's colour, and the breach and the
 * settle are drawn. The chart: the hand's speed a bar a frame, with the
 * breach's speed and the change either side of it as levels.
 */
public record TearLabSceneModule() implements DomModule<TearLabSceneModule> {

    /** The stage: {@code new TearStage(branch, { host, names, tear, onStep, onDone })}. */
    public record TearStage() implements BranchComponent<TearLabSceneModule> {
        @Override public String summary() { return "A strip of chips on a sunken stage: drag one off and see where TabTear tears, flies and settles it."; }
    }
    /** The chart: {@code new TearChart(branch, { host })}; {@code start}, {@code push}, {@code levels}. */
    public record TearChart() implements BranchComponent<TearLabSceneModule> {
        @Override public String summary() { return "The hand's speed over a gesture, a bar a frame, with the breach's speed and the change either side of it."; }
    }

    public static final TearLabSceneModule INSTANCE = new TearLabSceneModule();

    @Override
    public ImportsFor<TearLabSceneModule> imports() {
        return ImportsFor.<TearLabSceneModule>builder()
                .add(new ModuleImports<>(List.of(new TabChipModule.TabChip()), TabChipModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabDragModule.TabDrag()), TabDragModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new PaneStyles.mtp_strip(),
                        new PaneStyles.mtp_chip_seated(),
                        new PaneStyles.mtp_chip_dragging(),
                        new PaneStyles.mtp_chip_shifted()
                ), PaneStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new TearLabStyles.tl_stage(), new TearLabStyles.tl_strip(), new TearLabStyles.tl_band(), new TearLabStyles.tl_band_inner(),
                        new TearLabStyles.tl_centre(), new TearLabStyles.tl_free(),
                        new TearLabStyles.tl_waiting(), new TearLabStyles.tl_hand(), new TearLabStyles.tl_dot(), new TearLabStyles.tl_mark(),
                        new TearLabStyles.tl_rail(), new TearLabStyles.tl_flight(), new TearLabStyles.tl_follow(),
                        new TearLabStyles.tl_chart(), new TearLabStyles.tl_bar(), new TearLabStyles.tl_level()
                ), TearLabStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TearLabSceneModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new TearStage(), new TearChart()));
    }
}
