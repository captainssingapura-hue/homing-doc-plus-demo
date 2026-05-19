package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.demo.es.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimals;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;

import java.util.List;

/**
 * L1 sub-catalogue under {@link DemoStudio} grouping the three animal-game
 * AppModules — Moving Animal (platform game), Dancing Animals (5×5 keyboard
 * dance grid), Spinning Animals (auto-rotating gallery).
 *
 * <p>The first <i>application</i>-shaped sub-catalogue in homing-doc-plus-demo:
 * every leaf is an {@code AppModule} (a real SPA) rather than a {@code Doc}.
 * Apps and docs are sibling kinds of catalogue leaves under the same chrome
 * — the typed Catalogue / DocViewer / AppModule machinery doesn't care
 * which kind any given leaf is. This is the Site-in-a-Jar shape RFC 0022 +
 * {@code SiteInAJarPlanData} are about: applications served as catalogue
 * leaves alongside the documentation that explains them.</p>
 *
 * <p>Each game leaf is a {@link Navigable} wrapping the game's
 * {@link AppModule}. Click → the framework routes through
 * {@code /app?app=&lt;simple-name&gt;}; the game's full UI renders
 * inside the standard studio chrome (Header + breadcrumb + theme picker +
 * audio cues).</p>
 */
public record AnimalGamesCatalogue() implements L1_Catalogue<DemoStudio, AnimalGamesCatalogue> {

    public static final AnimalGamesCatalogue INSTANCE = new AnimalGamesCatalogue();

    @Override public DemoStudio parent()  { return DemoStudio.INSTANCE; }
    @Override public String     name()    { return "Animal Games"; }
    @Override public String     summary() { return "Three animal-themed SPA games served as catalogue leaves. Apps are siblings of docs under the same chrome — the first application-shaped sub-catalogue in the demo studio."; }
    @Override public String     badge()   { return "GAMES"; }
    @Override public String     icon()    { return "🎮"; }

    @Override public List<Entry<AnimalGamesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, new Navigable<>(
                        MovingAnimal.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Moving Animal",
                        "Platform game — arrow keys to move, space to jump. Tone.js soundtrack; theme-aware playground; animal-of-the-day selector.")),
                Entry.of(this, new Navigable<>(
                        DancingAnimals.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Dancing Animals",
                        "5×5 keyboard dance grid. ← → flip the herd left/right, Space makes them jump. Shared SVG animal cells; one selector preselects all.")),
                Entry.of(this, new Navigable<>(
                        SpinningAnimals.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Spinning Animals",
                        "Auto-rotating animal gallery — keyboard arrows reverse the spin direction. The simplest of the three games; demonstrates the AnimalCell primitive on its own."))
        );
    }
}
