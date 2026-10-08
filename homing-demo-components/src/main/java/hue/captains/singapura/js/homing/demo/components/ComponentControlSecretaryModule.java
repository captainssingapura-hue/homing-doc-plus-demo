package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.controlpanel.ControlCatalogueModule;

import java.util.List;

/** {@code ComponentControlSecretary}: the component-control party's secretary - what is set, by option, kept and told; what is done, told. */
public record ComponentControlSecretaryModule() implements EsModule<ComponentControlSecretaryModule> {

    public static final ComponentControlSecretaryModule INSTANCE = new ComponentControlSecretaryModule();

    public record ComponentControlSecretary() implements Exportable._Constant<ComponentControlSecretaryModule> {}

    @Override
    public ImportsFor<ComponentControlSecretaryModule> imports() {
        return ImportsFor.<ComponentControlSecretaryModule>builder()
                .add(new ModuleImports<>(List.of(new ControlCatalogueModule.CONTROL_OPTIONS()), ControlCatalogueModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ComponentControlSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentControlSecretary())); }
}
