package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;
import hue.captains.singapura.js.homing.workspace.widgets.NoParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;

import java.util.List;

/** The animal selector, declared: {@code animal-selector}, titled Animal, with no params, joining the animal choice party. */
public record AnimalSelectorDeclaration() implements WidgetDeclaration<NoParams> {

    public static final AnimalSelectorDeclaration INSTANCE = new AnimalSelectorDeclaration();

    @Override public String kind() { return "animal-selector"; }
    @Override public String title() { return "Animal"; }
    @Override public Class<NoParams> paramsType() { return NoParams.class; }
    @Override public WidgetQuery<NoParams> query() { return new NoParams.Query(); }
    @Override public List<PartyType<?>> parties() { return List.of(AnimalChoice.TYPE); }

    @Override
    public ModuleImports<?> constructs() {
        return new ModuleImports<>(List.of(new AnimalSelectorModule.AnimalSelector()), AnimalSelectorModule.INSTANCE);
    }
}
