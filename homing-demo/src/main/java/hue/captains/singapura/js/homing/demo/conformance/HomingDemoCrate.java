package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.ext.GameLoopModuleType;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.libs.LibsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.studio.base.StudioBaseCrate;
import hue.captains.singapura.js.homing.studio.workspace.StudioWorkspaceCrate;
import hue.captains.singapura.js.homing.workspace.WorkspaceCrate;
import hue.captains.singapura.js.homing.workspace.codecs.WorkspaceCodecsCrate;
import hue.captains.singapura.js.homing.workspace.persistence.WorkspacePersistenceCrate;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceShellCrate;

import hue.captains.singapura.js.homing.demo.css.AliceStyles;
import hue.captains.singapura.js.homing.demo.css.BaseStyles;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;
import hue.captains.singapura.js.homing.demo.css.SpinningStyles;
import hue.captains.singapura.js.homing.demo.css.SubwayStyles;
import hue.captains.singapura.js.homing.demo.es.animation.AnimalCell;
import hue.captains.singapura.js.homing.demo.es.animation.CuteAnimal;
import hue.captains.singapura.js.homing.demo.es.animation.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.animation.DancingAnimalsWidget;
import hue.captains.singapura.js.homing.demo.es.svg.DecomposedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.svg.ExtrudedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.svg.ExtrudedTurtleDemo;
import hue.captains.singapura.js.homing.demo.es.game.platformer.JumpPhysics;
import hue.captains.singapura.js.homing.demo.es.game.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.game.MovingAnimalGame;
import hue.captains.singapura.js.homing.demo.es.game.MovingAnimalReplayWidget;
import hue.captains.singapura.js.homing.demo.es.game.MovingAnimalWidget;
import hue.captains.singapura.js.homing.demo.es.game.platformer.PlatformEngine;
import hue.captains.singapura.js.homing.demo.es.game.platformer.PlatformerBgm;
import hue.captains.singapura.js.homing.demo.es.animation.SpinningAnimals;
import hue.captains.singapura.js.homing.demo.es.animation.SpinningAnimalsWidget;
import hue.captains.singapura.js.homing.demo.es.svg.SvgDecomposer;
import hue.captains.singapura.js.homing.demo.es.svg.SvgExtruder;
import hue.captains.singapura.js.homing.demo.playground.AnimalsPlaygroundStyles;
import hue.captains.singapura.js.homing.demo.playground.AnimalsSecretaryModule;
import hue.captains.singapura.js.homing.demo.playground.DocViewWidget;
import hue.captains.singapura.js.homing.demo.studio.DemoStandardMPA;

import java.util.List;

/**
 * RFC 0044 — the demo's own {@link Crate}: every served JS module {@code
 * homing-demo} ships, in one crate (one crate per Maven module — the {@code
 * OrphanCheck} scans this module's whole build output and would flag any served
 * module this list omits). This is the demo's "register for serving + register
 * for conformance" leg, browsed by the demo's conformance studio.
 *
 * <p>{@link MovingAnimalGame} is declared as the downstream {@link
 * GameLoopModuleType#GAME_LOOP} extension type (the declaration wins over
 * structural inference), so the studio shows the animal-game loop held to the
 * downstream {@code game-loop} rule set — the RFC 0044 policy extension, made
 * visible.</p>
 *
 * <p>{@link #requires()} names the framework crates the demo modules import, so
 * their cross-crate imports are legal under {@code CrateDependencyRule}; those
 * crates render as external dependencies in the studio's crate graph.</p>
 */
public final class HomingDemoCrate implements Crate {

    public static final HomingDemoCrate INSTANCE = new HomingDemoCrate();

    private HomingDemoCrate() {}

    @Override public String name() { return "homing-demo"; }

    @Override
    public List<Crate> requires() {
        return List.of(
                CoreJsCrate.INSTANCE,
                ServerCrate.INSTANCE,
                StudioBaseCrate.INSTANCE,
                WorkspaceCrate.INSTANCE,
                WorkspaceCodecsCrate.INSTANCE,
                WorkspacePersistenceCrate.INSTANCE,
                WorkspaceShellCrate.INSTANCE,
                StudioWorkspaceCrate.INSTANCE,
                LibsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(AliceStyles.INSTANCE),
                CrateEntry.of(BaseStyles.INSTANCE),
                CrateEntry.of(PlaygroundStyles.INSTANCE),
                CrateEntry.of(SpinningStyles.INSTANCE),
                CrateEntry.of(SubwayStyles.INSTANCE),
                CrateEntry.of(AnimalCell.INSTANCE),
                CrateEntry.of(CuteAnimal.INSTANCE),
                CrateEntry.of(DancingAnimals.INSTANCE),
                CrateEntry.of(DancingAnimalsWidget.INSTANCE),
                CrateEntry.of(DecomposedSvgDemo.INSTANCE),
                CrateEntry.of(ExtrudedSvgDemo.INSTANCE),
                CrateEntry.of(ExtrudedTurtleDemo.INSTANCE),
                CrateEntry.of(JumpPhysics.INSTANCE),
                CrateEntry.of(MovingAnimal.INSTANCE),
                // The downstream extension, made visible in the studio:
                CrateEntry.of(MovingAnimalGame.INSTANCE, GameLoopModuleType.GAME_LOOP),
                CrateEntry.of(MovingAnimalReplayWidget.INSTANCE),
                CrateEntry.of(MovingAnimalWidget.INSTANCE),
                CrateEntry.of(PlatformEngine.INSTANCE),
                CrateEntry.of(PlatformerBgm.INSTANCE),
                CrateEntry.of(SpinningAnimals.INSTANCE),
                CrateEntry.of(SpinningAnimalsWidget.INSTANCE),
                CrateEntry.of(SvgDecomposer.INSTANCE),
                CrateEntry.of(SvgExtruder.INSTANCE),
                CrateEntry.of(AnimalsPlaygroundStyles.INSTANCE),
                CrateEntry.of(AnimalsSecretaryModule.INSTANCE),
                CrateEntry.of(DocViewWidget.INSTANCE),
                CrateEntry.of(DemoStandardMPA.INSTANCE));
    }
}
