package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * What a platformer party carries: one run of the game, as the widget playing
 * it says it - each thing that changes the world, in the order it happened,
 * and a {@link Tick} for every frame, the clock a watcher steps by. Applied in
 * order, at the frames they came at, they make the same world again: the
 * physics counts frames, and the one thing chance makes - the terrain - is
 * said as it is made ({@link PlatformGenerated}), never made twice.
 *
 * <p>The player tells each; the party says it on, as it came, to every member.
 * A watcher joining mid-run is to be handed the world as it stands - a
 * snapshot, with the platforms under it - which comes with the watcher, and
 * with it the party's nested records.</p>
 */
public sealed interface Platformer {

    /** A direction held: {@code left} or {@code right}. */
    record MoveStarted(String dir) implements Platformer {}

    /** A direction let go. */
    record MoveStopped(String dir) implements Platformer {}

    /** A jump, begun - nothing, in the air. */
    record Jumped() implements Platformer {}

    /** Gravity set: the fall's pull per frame. */
    record GravityChanged(double value) implements Platformer {}

    /** A platform made ahead of the camera: where, how wide, and which of the three looks it wears. */
    record PlatformGenerated(double x, double y, double w, int vehicle) implements Platformer {}

    /** A frame: the world stepped once. */
    record Tick() implements Platformer {}

    /**
     * The type: {@code platformer}, its identity on a page - its constant
     * served as {@code PLATFORMER}, and a root instance's secretary, unless a
     * workspace puts its own, {@code PlatformerSecretary}.
     */
    PartyType<Platformer> TYPE = new PartyType<>("platformer", Platformer.class)
            .servedFrom(new ModuleImports<>(List.of(new PlatformerModule.PLATFORMER()), PlatformerModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new PlatformerSecretaryModule.PlatformerSecretary()), PlatformerSecretaryModule.INSTANCE));
}
