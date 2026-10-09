package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.BadgeModule;
import hue.captains.singapura.js.homing.ui.elements.CaptionModule;
import hue.captains.singapura.js.homing.ui.elements.HeadingModule;
import hue.captains.singapura.js.homing.ui.elements.KickerModule;
import hue.captains.singapura.js.homing.ui.elements.LabelModule;
import hue.captains.singapura.js.homing.ui.elements.LedeModule;
import hue.captains.singapura.js.homing.ui.elements.PillModule;
import hue.captains.singapura.js.homing.ui.elements.ReadoutModule;
import hue.captains.singapura.js.homing.ui.elements.WordmarkModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code TextDemo}: one of the house's nine texts, as its leaf says, in the words such a text holds; sized by its options. */
public record TextDemoModule() implements DomModule<TextDemoModule> {

    public static final TextDemoModule INSTANCE = new TextDemoModule();

    public record TextDemo() implements SelfContainedWidget<TextDemoModule> {
        @Override public String summary() { return "One of the house's nine texts, as its leaf says - a heading, a kicker, a caption, a lede, a label, a readout, a badge, a pill or a wordmark - in the words such a text holds, in the look the style gives it; sized by its options."; }
    }

    @Override
    public ImportsFor<TextDemoModule> imports() {
        return ImportsFor.<TextDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HeadingModule.Heading(), new HeadingModule.HeadingFactory()), HeadingModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CaptionModule.Caption(), new CaptionModule.CaptionFactory()), CaptionModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LabelModule.Label(), new LabelModule.LabelFactory()), LabelModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReadoutModule.Readout(), new ReadoutModule.ReadoutFactory()), ReadoutModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new BadgeModule.Badge(), new BadgeModule.BadgeFactory()), BadgeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PillModule.Pill(), new PillModule.PillFactory()), PillModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WordmarkModule.Wordmark(), new WordmarkModule.WordmarkFactory()), WordmarkModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<TextDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new TextDemo())); }
}
