package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code NodeDetails}: the picked node - what it is, how it falls back, its parts or where it is a part, and its semantic classes. */
public record NodeDetailsModule() implements DomModule<NodeDetailsModule> {

    public static final NodeDetailsModule INSTANCE = new NodeDetailsModule();

    public record NodeDetails() implements SelfContainedWidget<NodeDetailsModule>, NeedKeyboard {
        @Override public String summary() { return "The picked node of the taxonomy: what it means, and the meanings it narrows; what it is and where it sits; the axes it varies along by degree, each with the node that declares it; the chain it falls back along - a part's through its base, never its owner; a component's parts and the roles it plays in others; a kind's components; and its semantic classes, one for every target leaf."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back, when nothing in it took it")); }
    }

    @Override
    public ImportsFor<NodeDetailsModule> imports() {
        return ImportsFor.<NodeDetailsModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_scroll(), new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_title(),
                        new TaxonomyStyles.tx_panel(), new TaxonomyStyles.tx_facts(), new TaxonomyStyles.tx_key(), new TaxonomyStyles.tx_code(),
                        new TaxonomyStyles.tx_line(), new TaxonomyStyles.tx_tag(), new TaxonomyStyles.tx_prose()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<NodeDetailsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new NodeDetails())); }
}
