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
 * A watcher that joins mid-run asks for the world as it stands ({@link
 * WorldRequested}); the party asks the player ({@link WorldWanted}), and the
 * player answers it alone - a {@link Snapshot}, the {@link World} whole, its
 * {@link Platform}s with it: a message of records inside records. What the
 * stream told before it is in it already; what comes after, the watcher
 * applies. A run begun - or joined, or begun again - is a snapshot to every
 * member.</p>
 */
public sealed interface Platformer {

    /** A platform, as the world holds it: where, how wide, which of the three looks. Not a kind: what a {@link World} holds. */
    record Platform(double x, double y, double w, int vehicle) {}

    /**
     * The world as it stands, whole: where the animal is and which way it faces, the camera, the
     * run's score, whether it is over and how many steps it has taken, gravity's pull, the fall -
     * how fast, and whether in the air - the moves held, and the platforms. Not a kind: what a
     * {@link Snapshot} carries.
     */
    record World(double x, double y, double cameraX, boolean facingRight, int score, boolean over, int tick,
                 double gravity, double vy, boolean inAir, boolean left, boolean right, List<Platform> platforms) {}

    /** A watcher asks for the world as it stands - one that joins mid-run - and is answered alone. */
    record WorldRequested() implements Platformer {}

    /** The party asks the player for the world, for the member that asked. */
    record WorldWanted(String asker) implements Platformer {}

    /** The world as it stands: to the member that asked ({@code to}) alone - or, {@code to} blank, to every member. */
    record Snapshot(String to, World world) implements Platformer {}

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
