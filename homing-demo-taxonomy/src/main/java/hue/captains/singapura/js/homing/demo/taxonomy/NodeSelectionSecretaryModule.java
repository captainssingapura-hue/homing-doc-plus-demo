package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/** {@code NodeSelectionSecretary}: the node-selection party's secretary - one pick, kept and told. */
public record NodeSelectionSecretaryModule() implements EsModule<NodeSelectionSecretaryModule> {

    public static final NodeSelectionSecretaryModule INSTANCE = new NodeSelectionSecretaryModule();

    public record NodeSelectionSecretary() implements Exportable._Constant<NodeSelectionSecretaryModule> {}

    @Override public ImportsFor<NodeSelectionSecretaryModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<NodeSelectionSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new NodeSelectionSecretary())); }
}
