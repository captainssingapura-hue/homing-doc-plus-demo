package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.ui.panes.PaneStyles;
import hue.captains.singapura.js.homing.ui.panes.TabTearModule;

import java.util.List;

/**
 * The tear-off lab: a strip of chips to drag off, {@code TabTear}'s options as
 * sliders and a choice of measure, the gesture read out in numbers and its
 * speed charted a frame at a time. No window is made: a torn chip stands for
 * one, so the model can be played with before a desk is asked to do it.
 */
public record TearLabApp() implements AppModule<AppModule._None, TearLabApp> {

    public static final TearLabApp INSTANCE = new TearLabApp();

    record appMain() implements AppModule._AppMain<AppModule._None, TearLabApp> {}
    /** The app as a widget by the base's contract: {@code new TearLabWidget(branch, params)}; appMain delegates to it. */
    public record TearLabWidget() implements BranchComponent<TearLabApp> {
        @Override public String summary() { return "Tearing a tab off its strip, as a lab: TabTear's options to play with, and every gesture read out and charted."; }
    }

    @Override public String title()      { return "Tearing a tab off"; }
    @Override public String simpleName() { return "tear"; }

    @Override
    public ImportsFor<TearLabApp> imports() {
        return ImportsFor.<TearLabApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabTearModule.TabTear()), TabTearModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new TearLabSceneModule.TearStage(), new TearLabSceneModule.TearChart()), TearLabSceneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PaneStyles.mtp_new_pick()), PaneStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new TearLabStyles.tl_readout()), TearLabStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_control(),
                        new GalleryStyles.ga_control_label()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TearLabApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new TearLabWidget()));
    }
}
