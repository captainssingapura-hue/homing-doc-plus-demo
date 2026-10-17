package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.component.taxonomy.ConcreteComponent;
import hue.captains.singapura.js.homing.component.taxonomy.Design;
import hue.captains.singapura.js.homing.component.taxonomy.DesignBranch;
import hue.captains.singapura.js.homing.component.taxonomy.L2_ComponentBranch;
import hue.captains.singapura.js.homing.component.taxonomy.VirtualComponent;
import hue.captains.singapura.js.homing.design.Owns;
import hue.captains.singapura.js.homing.design.Target;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseBranches;

import java.util.List;

/**
 * The platformer's scene: what its stage shows besides the animal, as a design branch of virtual leaves - the
 * backdrop, the sky, a platform, the platform underfoot, the lava, and the card when a run is over. A design of it
 * draws every leaf, in the palette worn; the stage wears a leaf through the design and never learns a class. In the
 * house's taxonomy wherever an instance registers its module ({@link PlatformerSceneComponentModule}), as the grid's
 * frame is.
 *
 * <p>The platform underfoot is a leaf of its own, which the stage wears in the platform's place while the animal
 * stands on it: it owns what a platform owns, so one replaces the other whole.</p>
 */
public record PlatformerScene() implements L2_ComponentBranch<HouseBranches.Region>, DesignBranch {

    public static final PlatformerScene INSTANCE = new PlatformerScene();

    @Override public HouseBranches.Region parent() { return HouseBranches.Region.INSTANCE; }

    /** The stage's own ground, behind everything in it, and its corner. */
    public record Backdrop() implements VirtualComponent<PlatformerScene>, Owns {
        public static final Backdrop INSTANCE = new Backdrop();
        @Override public PlatformerScene parent() { return PlatformerScene.INSTANCE; }
        @Override public List<Target> owns() { return List.of(Target.Color.Background.INSTANCE, Target.Shape.Corner.INSTANCE); }
        public interface Concrete<D extends Design<?, PlatformerScene>> extends ConcreteComponent<Backdrop, PlatformerScene, D> {}
    }

    /** The band of sky along the top of the stage. */
    public record Sky() implements VirtualComponent<PlatformerScene>, Owns {
        public static final Sky INSTANCE = new Sky();
        @Override public PlatformerScene parent() { return PlatformerScene.INSTANCE; }
        @Override public List<Target> owns() { return List.of(Target.Color.Background.INSTANCE); }
        public interface Concrete<D extends Design<?, PlatformerScene>> extends ConcreteComponent<Sky, PlatformerScene, D> {}
    }

    /** A platform the animal may land on: its face, its edge and its corner. */
    public record Platform() implements VirtualComponent<PlatformerScene>, Owns {
        public static final Platform INSTANCE = new Platform();
        @Override public PlatformerScene parent() { return PlatformerScene.INSTANCE; }
        @Override public List<Target> owns() {
            return List.of(Target.Color.Background.INSTANCE, Target.Shape.Border.INSTANCE, Target.Color.Border.INSTANCE, Target.Shape.Corner.INSTANCE);
        }
        public interface Concrete<D extends Design<?, PlatformerScene>> extends ConcreteComponent<Platform, PlatformerScene, D> {}
    }

    /** The platform the animal stands on now, worn in the platform's place. */
    public record PlatformUnderfoot() implements VirtualComponent<PlatformerScene>, Owns {
        public static final PlatformUnderfoot INSTANCE = new PlatformUnderfoot();
        @Override public PlatformerScene parent() { return PlatformerScene.INSTANCE; }
        @Override public List<Target> owns() {
            return List.of(Target.Color.Background.INSTANCE, Target.Shape.Border.INSTANCE, Target.Color.Border.INSTANCE, Target.Shape.Corner.INSTANCE);
        }
        public interface Concrete<D extends Design<?, PlatformerScene>> extends ConcreteComponent<PlatformUnderfoot, PlatformerScene, D> {}
    }

    /** The lava along the foot of the stage, and the line it starts at. */
    public record Lava() implements VirtualComponent<PlatformerScene>, Owns {
        public static final Lava INSTANCE = new Lava();
        @Override public PlatformerScene parent() { return PlatformerScene.INSTANCE; }
        @Override public List<Target> owns() { return List.of(Target.Color.Background.INSTANCE, Target.Shape.Border.INSTANCE, Target.Color.Border.INSTANCE); }
        public interface Concrete<D extends Design<?, PlatformerScene>> extends ConcreteComponent<Lava, PlatformerScene, D> {}
    }

    /** The card over the stage when a run is over: its face, its edge and its corner. */
    public record ScoreCard() implements VirtualComponent<PlatformerScene>, Owns {
        public static final ScoreCard INSTANCE = new ScoreCard();
        @Override public PlatformerScene parent() { return PlatformerScene.INSTANCE; }
        @Override public List<Target> owns() {
            return List.of(Target.Color.Background.INSTANCE, Target.Shape.Border.INSTANCE, Target.Color.Border.INSTANCE, Target.Shape.Corner.INSTANCE);
        }
        public interface Concrete<D extends Design<?, PlatformerScene>> extends ConcreteComponent<ScoreCard, PlatformerScene, D> {}
    }
}
