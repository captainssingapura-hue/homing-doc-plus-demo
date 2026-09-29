package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalChoice;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;
import hue.captains.singapura.js.homing.workspace.widgets.NoParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;

import java.util.List;

/**
 * The platformer, watched, declared: {@code platformer-replay}, titled Platformer
 * replay, with no params - MANY, each the same run - joining the platformer party,
 * whose run it re-simulates, and the animal choice party, which says its animal.
 */
public record PlatformerReplayDeclaration() implements WidgetDeclaration<NoParams> {

    public static final PlatformerReplayDeclaration INSTANCE = new PlatformerReplayDeclaration();

    @Override public String kind() { return "platformer-replay"; }
    @Override public String title() { return "Platformer replay"; }
    @Override public Class<NoParams> paramsType() { return NoParams.class; }
    @Override public WidgetQuery<NoParams> query() { return new NoParams.Query(); }
    @Override public List<PartyType<?>> parties() { return List.of(Platformer.TYPE, AnimalChoice.TYPE); }

    @Override
    public ModuleImports<?> constructs() {
        return new ModuleImports<>(List.of(new PlatformerReplayModule.PlatformerReplay()), PlatformerReplayModule.INSTANCE);
    }
}
