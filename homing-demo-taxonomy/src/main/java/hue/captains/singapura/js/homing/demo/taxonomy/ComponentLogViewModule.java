package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.Elements;

import java.util.List;

/**
 * {@code ComponentLogView}: what a component-log party says, newest first, with a Clear of its own -
 * the view both logs are; each log a widget of its own module, {@link DemoLogViewModule} and
 * {@link ControlLogViewModule}, handing it its party's type.
 */
public record ComponentLogViewModule() implements DomModule<ComponentLogViewModule> {

    public static final ComponentLogViewModule INSTANCE = new ComponentLogViewModule();

    public record ComponentLogView() implements Exportable._Class<ComponentLogViewModule> {}

    @Override
    public ImportsFor<ComponentLogViewModule> imports() {
        return ImportsFor.<ComponentLogViewModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_title(), new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_line(),
                        new TaxonomyStyles.tx_code(), new TaxonomyStyles.tx_scroll()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ComponentLogViewModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentLogView())); }
}
