package hue.captains.singapura.js.homing.demo.workspacewidgets;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.demo.workspacewidgets.conformance.GameLoopModuleType;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalChoiceModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalChoiceSecretaryModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalSelectorDeclaration;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalSelectorModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalStyles;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalsModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.CuteAnimals;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.JumpPhysicsModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformEngineModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerGameModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerPlayDeclaration;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerPlayModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerReplayDeclaration;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerReplayModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerSecretaryModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerSoundModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerStageModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerStyles;
import hue.captains.singapura.js.homing.demo.workspacewidgets.video.EmbeddedVideoDeclaration;
import hue.captains.singapura.js.homing.demo.workspacewidgets.video.EmbeddedVideoModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.video.EmbeddedVideoStyles;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.libs.LibsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.elements.UiElementsCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceWidgetsCrate;

import java.util.List;
import java.util.stream.Stream;

/**
 * The demo's widgets on the workspace's core, and nothing else: each
 * self-contained, each worn in the design's words. Any workspace may declare
 * them among its kinds; nothing here knows a shell, a page or the studio's old
 * workspace. Two sets: the media - the video playlist - and the games - the
 * animal platformer, played and watched, and the animal selector its animal is
 * chosen by, with the two parties they meet in.
 */
public final class DemoWorkspaceWidgetsCrate implements Crate {

    public static final DemoWorkspaceWidgetsCrate INSTANCE = new DemoWorkspaceWidgetsCrate();

    /** The media: the video playlist. */
    public static final List<WidgetDeclaration<?>> MEDIA = List.of(EmbeddedVideoDeclaration.INSTANCE);

    /** The games: the platformer, played - single - and watched - many - and the animal selector. */
    public static final List<WidgetDeclaration<?>> GAMES = List.of(PlatformerPlayDeclaration.INSTANCE, PlatformerReplayDeclaration.INSTANCE, AnimalSelectorDeclaration.INSTANCE);

    /** Every kind, for a workspace that offers them all. */
    public static final List<WidgetDeclaration<?>> KINDS = Stream.concat(MEDIA.stream(), GAMES.stream()).toList();

    private DemoWorkspaceWidgetsCrate() {}

    @Override public String name() { return "homing-demo-workspace-widgets"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the DomOpsParty a widget mints its root from
                CoreJsCrate.INSTANCE,
                // the focus party, the keys' convention, the css manager
                ServerCrate.INSTANCE,
                // the design words the sheets wear
                DesignCrate.INSTANCE,
                // the design's slider and button: the platformer's gravity, its music, its Play again
                UiElementsCrate.INSTANCE,
                // Tone.js, served from the classpath: the platformer's sound
                LibsCrate.INSTANCE,
                // what a widget is: the sheet it fills its container by
                WorkspaceWidgetsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                // the video playlist: a player over a strip of takes
                CrateEntry.of(EmbeddedVideoStyles.INSTANCE),
                CrateEntry.of(EmbeddedVideoModule.INSTANCE, StandardJsModuleType.CONSUMER),
                // the animals: their pictures, and each named
                CrateEntry.of(CuteAnimals.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(AnimalsModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                // the animal choice party: its type, generated from Java, and its secretary; the selector
                CrateEntry.of(AnimalChoiceModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(AnimalChoiceSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                CrateEntry.of(AnimalStyles.INSTANCE),
                CrateEntry.of(AnimalSelectorModule.INSTANCE, StandardJsModuleType.CONSUMER),
                // the platformer party: the run, told; its type and its secretary
                CrateEntry.of(PlatformerModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlatformerSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                // the game apart from any page: the terrain, the jump, the world and its rules; the sound
                CrateEntry.of(PlatformEngineModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(JumpPhysicsModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlatformerGameModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlatformerSoundModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                // the game drawn, and played
                CrateEntry.of(PlatformerStyles.INSTANCE),
                CrateEntry.of(PlatformerStageModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(PlatformerPlayModule.INSTANCE, GameLoopModuleType.GAME_LOOP),
                // and watched: the run re-simulated, a snapshot of records inside records to join by
                CrateEntry.of(PlatformerReplayModule.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
