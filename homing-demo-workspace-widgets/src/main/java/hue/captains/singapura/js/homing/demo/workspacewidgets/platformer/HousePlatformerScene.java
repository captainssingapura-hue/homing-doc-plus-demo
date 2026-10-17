package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.component.taxonomy.Design;
import hue.captains.singapura.js.homing.design.Target;
import hue.captains.singapura.js.homing.design.color.Paint;
import hue.captains.singapura.js.homing.design.color.SemanticColor;
import hue.captains.singapura.js.homing.design.semantic.Drawing;
import hue.captains.singapura.js.homing.design.semantic.Drawn;
import hue.captains.singapura.js.homing.design.semantic.Dressed;
import hue.captains.singapura.js.homing.ui.styles.BaseColours;
import hue.captains.singapura.js.homing.ui.styles.BaseScheme;

import java.util.List;

/**
 * The house's design of the platformer's scene, in every house style and palette, drawn in the palette's own colours:
 * the stage's backdrop its recessed layer, the sky a wash of its accent, a platform its title colour - as a tab in use
 * is drawn - the platform underfoot its accent, lit where the animal stands, and the card when a run is over
 * raised over the stage. The lava alone keeps the house's danger, the same in every palette, so it always reads as
 * danger. Every colour is the house's own (RFC 0067, D43), so the scene follows the palette worn.
 */
public record HousePlatformerScene() implements Design<BaseScheme, PlatformerScene> {

    public static final HousePlatformerScene INSTANCE = new HousePlatformerScene();

    @Override public BaseScheme scheme() { return BaseScheme.INSTANCE; }

    private static final Target.Color.Background GROUND = Target.Color.Background.INSTANCE;
    private static final Target.Shape.Border BORDER = Target.Shape.Border.INSTANCE;
    private static final Target.Color.Border BORDER_COLOUR = Target.Color.Border.INSTANCE;
    private static final Target.Shape.Corner CORNER = Target.Shape.Corner.INSTANCE;

    /** A ground said whole: a colour, and no ramp nor mark over it. */
    private static Paint.Ground<BaseScheme> ground(SemanticColor<? super BaseScheme> colour) { return new Paint.Ground<>(colour); }

    /** The stage's ground: the recessed layer, its corner a little round. */
    public record Backdrop() implements PlatformerScene.Backdrop.Concrete<HousePlatformerScene>, Drawn {
        public static final Backdrop INSTANCE = new Backdrop();
        @Override public List<Drawing<?>> drawings(Dressed theme) {
            return List.of(new Drawing.Painted<>(GROUND, ground(new BaseColours.Recessed())),
                           new Drawing.Own<>(CORNER, "border-radius: 6px;"));
        }
    }

    /** The sky: a wash of the palette's accent, a band over the recessed stage. */
    public record Sky() implements PlatformerScene.Sky.Concrete<HousePlatformerScene>, Drawn {
        public static final Sky INSTANCE = new Sky();
        @Override public List<Drawing<?>> drawings(Dressed theme) {
            return List.of(new Drawing.Painted<>(GROUND, ground(new BaseColours.AccentWash())));
        }
    }

    /** A platform: the palette's title colour, face and edge, as a tab in use is drawn - firm enough to land on at a glance; the one underfoot its accent. */
    public record Platform() implements PlatformerScene.Platform.Concrete<HousePlatformerScene>, Drawn {
        public static final Platform INSTANCE = new Platform();
        @Override public List<Drawing<?>> drawings(Dressed theme) {
            return List.of(new Drawing.Painted<>(GROUND, ground(new BaseColours.Title())),
                           new Drawing.Own<>(BORDER, "border-width: 2px; border-style: solid;"),
                           new Drawing.Painted<>(BORDER_COLOUR, new Paint.With<>(new BaseColours.Title())),
                           new Drawing.Own<>(CORNER, "border-radius: 3px;"));
        }
    }

    /** The platform underfoot: the palette's accent, its edge the accent's - the one platform that is not the title's. */
    public record PlatformUnderfoot() implements PlatformerScene.PlatformUnderfoot.Concrete<HousePlatformerScene>, Drawn {
        public static final PlatformUnderfoot INSTANCE = new PlatformUnderfoot();
        @Override public List<Drawing<?>> drawings(Dressed theme) {
            return List.of(new Drawing.Painted<>(GROUND, ground(new BaseColours.Accent())),
                           new Drawing.Own<>(BORDER, "border-width: 2px; border-style: solid;"),
                           new Drawing.Painted<>(BORDER_COLOUR, new Paint.With<>(new BaseColours.AccentEdge())),
                           new Drawing.Own<>(CORNER, "border-radius: 3px;"));
        }
    }

    /** The lava: the danger along the foot, the pressed danger's line where it starts. */
    public record Lava() implements PlatformerScene.Lava.Concrete<HousePlatformerScene>, Drawn {
        public static final Lava INSTANCE = new Lava();
        @Override public List<Drawing<?>> drawings(Dressed theme) {
            return List.of(new Drawing.Painted<>(GROUND, ground(new BaseColours.Danger())),
                           new Drawing.Own<>(BORDER, "border-width: 2px 0 0 0; border-style: solid;"),
                           new Drawing.Painted<>(BORDER_COLOUR, new Paint.With<>(new BaseColours.DangerPressed())));
        }
    }

    /** The card when a run is over: the raised layer over the stage, its edge the groove's. */
    public record ScoreCard() implements PlatformerScene.ScoreCard.Concrete<HousePlatformerScene>, Drawn {
        public static final ScoreCard INSTANCE = new ScoreCard();
        @Override public List<Drawing<?>> drawings(Dressed theme) {
            return List.of(new Drawing.Painted<>(GROUND, ground(new BaseColours.Raised())),
                           new Drawing.Own<>(BORDER, "border-width: 1px; border-style: solid;"),
                           new Drawing.Painted<>(BORDER_COLOUR, new Paint.With<>(new BaseColours.GrooveEdge())),
                           new Drawing.Own<>(CORNER, "border-radius: 8px;"));
        }
    }
}
