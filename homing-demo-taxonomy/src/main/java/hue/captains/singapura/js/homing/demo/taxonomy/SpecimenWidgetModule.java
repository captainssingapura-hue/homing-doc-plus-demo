package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.SliderGroupModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.ui.specimens.HouseSpecimensModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code SpecimenWidget}: the picked component in action - built live by its specimen, what it does said, its axes set by number. */
public record SpecimenWidgetModule() implements DomModule<SpecimenWidgetModule> {

    public static final SpecimenWidgetModule INSTANCE = new SpecimenWidgetModule();

    public record SpecimenWidget() implements SelfContainedWidget<SpecimenWidgetModule>, NeedKeyboard {
        @Override public String summary() { return "The picked component in action: built live by its specimen, its behaviour exercised and every thing it does said; a slider for each axis its leaf varies along, set on the live component. A leaf only; one nothing realizes yet says so, with what it means."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>(SliderModule.Slider.KEYS);
            keys.addAll(KeyBinding.each("handed on to the specimen: a card's action", Key.ENTER, Key.SPACE));
            keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back, when neither the specimen nor its axes took it"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<SpecimenWidgetModule> imports() {
        return ImportsFor.<SpecimenWidgetModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HouseSpecimensModule.HOUSE_SPECIMENS(), new HouseSpecimensModule.HOUSE_UNREALIZED(),
                        new HouseSpecimensModule.HOUSE_AROUND()), HouseSpecimensModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderGroupModule.SliderGroupBuilder()), SliderGroupModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_scroll(), new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_title(),
                        new TaxonomyStyles.tx_prose(), new TaxonomyStyles.tx_panel(), new TaxonomyStyles.tx_code()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SpecimenWidgetModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SpecimenWidget())); }
}
