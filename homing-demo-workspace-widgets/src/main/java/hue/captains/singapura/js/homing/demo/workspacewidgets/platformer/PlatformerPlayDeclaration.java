package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalChoice;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;
import hue.captains.singapura.js.homing.workspace.widgets.NoParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;

import java.util.List;

/**
 * The platformer, declared: {@code platformer}, titled Animal platformer, with no
 * params - a run starts fresh - SINGLE, for it is the authority of the run a
 * workspace watches, and two would tangle their streams; joining the platformer
 * party, which it tells the run, and the animal choice party, which says its animal.
 */
public record PlatformerPlayDeclaration() implements WidgetDeclaration<NoParams> {

    public static final PlatformerPlayDeclaration INSTANCE = new PlatformerPlayDeclaration();

    @Override public String kind() { return "platformer"; }
    @Override public String title() { return "Animal platformer"; }
    @Override public Class<NoParams> paramsType() { return NoParams.class; }
    @Override public WidgetQuery<NoParams> query() { return new NoParams.Query(); }
    @Override public List<PartyType<?>> parties() { return List.of(Platformer.TYPE, AnimalChoice.TYPE); }
    @Override public boolean single() { return true; }

    @Override
    public ModuleImports<?> constructs() {
        return new ModuleImports<>(List.of(new PlatformerPlayModule.PlatformerPlay()), PlatformerPlayModule.INSTANCE);
    }
}
