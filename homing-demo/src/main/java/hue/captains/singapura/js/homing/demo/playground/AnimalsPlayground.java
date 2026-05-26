package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.demo.es.DancingAnimalsWidget;
import hue.captains.singapura.js.homing.demo.es.MovingAnimalWidget;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimalsWidget;
import hue.captains.singapura.js.homing.workspace.RibbonItem;
import hue.captains.singapura.js.homing.workspace.WidgetEntry;
import hue.captains.singapura.js.homing.workspace.WidgetGroup;
import hue.captains.singapura.js.homing.workspace.WidgetIcon;
import hue.captains.singapura.js.homing.workspace.WidgetLabel;
import hue.captains.singapura.js.homing.workspace.WorkspaceShell;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static hue.captains.singapura.js.homing.workspace.RibbonItem.ChoiceOption;

/**
 * RFC 0025 Ext1b — the demo workspace. URL contract:
 *
 * <pre>/app?app=animals-playground</pre>
 *
 * <p>Registers two widget types for V0 (b.2a POC):</p>
 *
 * <ul>
 *   <li>{@link DocViewWidget} as {@code PINNED} — auto-spawns the
 *       playground introduction into the top-left pane at boot.</li>
 *   <li>{@link SpinningAnimalsWidget} as {@code MULTI} (the default) —
 *       user opens it any number of times via the {@code "+"} picker.</li>
 * </ul>
 *
 * <p>b.2b will add {@code MovingAnimalsWidget} and {@code DancingAnimalsWidget}
 * to the registry. The other animals' standalone {@code @LegacyAppMain}
 * pages (e.g. {@code /app?app=spinning-animals}) remain functional —
 * the workspace is additive, not a replacement.</p>
 *
 * <p>Chrome is minimal — see {@link AnimalsPlaygroundChrome} for the
 * boot wiring. Polished workspace chrome (toolbar, persisted layout, …)
 * is explicitly deferred per the b.2a scope: "do not work on the
 * workspace chrome yet."</p>
 *
 * @since RFC 0025 Ext1b — b.2a POC.
 */
public final class AnimalsPlayground extends WorkspaceShell<AppModule._None, AnimalsPlayground> {

    public static final AnimalsPlayground INSTANCE = new AnimalsPlayground();

    private AnimalsPlayground() {}

    public record appMain() implements AppModule._AppMain<AppModule._None, AnimalsPlayground> {}
    public record link() implements AppLink<AnimalsPlayground> {}

    @Override public String simpleName() { return "animals-playground"; }
    @Override public Class<AppModule._None> paramsType() { return AppModule._None.class; }
    @Override public String title() { return "Animals Playground"; }

    @Override
    protected AppModule._AppMain<AppModule._None, AnimalsPlayground> appMain() {
        return new appMain();
    }

    @Override
    protected Widget<?, ?> widget() {
        return AnimalsPlaygroundChrome.INSTANCE;
    }

    @Override
    protected List<WidgetEntry> widgetEntries() {
        return List.of(
                WidgetEntry.of(DocViewWidget.class, WidgetLabel.of("Introduction"))
                        .withIcon(new WidgetIcon.Emoji("📖"))
                        .withGroup(WidgetGroup.of("Reference"))
                        .withDefaults(introDefaults()),
                WidgetEntry.of(SpinningAnimalsWidget.class, WidgetLabel.of("Spinning Animals"))
                        .withIcon(new WidgetIcon.Emoji("🎡"))
                        .withGroup(WidgetGroup.of("Games")),
                WidgetEntry.of(DancingAnimalsWidget.class, WidgetLabel.of("Dancing Animals"))
                        .withIcon(new WidgetIcon.Emoji("💃"))
                        .withGroup(WidgetGroup.of("Games")),
                WidgetEntry.of(MovingAnimalWidget.class, WidgetLabel.of("Moving Animal"))
                        .withIcon(new WidgetIcon.Emoji("🏃"))
                        .withGroup(WidgetGroup.of("Games"))
        );
    }

    /**
     * RFC 0028 cycle 4 — workspace-wide Animal selector. Lives in the
     * default Ribbon tab. On change, the chrome dispatches into the
     * {@code AnimalsParty} (the workspace's first downstream Party);
     * the Secretary broadcasts {@code AnimalChanged}; every open Animal
     * widget receives it and switches its rendered animal in unison.
     *
     * <p>The selector index of {@code -1} ("Random") means "leave each
     * cell to pick a random animal at refresh"; per-widget per-cell
     * randomness handled inside {@code AnimalCell.js}.</p>
     */
    @Override
    protected List<RibbonItem> ribbonItems() {
        return List.of(
                new RibbonItem.Choice(
                        "Animal",
                        List.of(
                                new ChoiceOption("Random", "-1"),
                                new ChoiceOption("Turtle", "0"),
                                new ChoiceOption("Ghost",  "1"),
                                new ChoiceOption("Broom",  "2"),
                                new ChoiceOption("Penguin","3"),
                                new ChoiceOption("Crocodile","4"),
                                new ChoiceOption("Whale",  "5")),
                        "animal-selected"));
    }

    /**
     * Defaults for the pinned introduction — title + Markdown body. The
     * body explains the workspace mechanics so a first-time visitor
     * understands the "+" affordance and the drag-to-dock flow without
     * needing external docs.
     */
    private static Map<String, String> introDefaults() {
        var d = new LinkedHashMap<String, String>();
        d.put("title", "Welcome to the Animals Playground");
        d.put("body", String.join("\n",
                "Open the picker with the **➕** button in the bottom-right corner.",
                "",
                "Pick a widget tile to instantiate it in a floating modal,",
                "then **drag the modal's title bar onto any pane strip** to dock it as a tab.",
                "",
                "Each widget runs independently in its own DomOpsParty branch —",
                "close its tab to dispose it.",
                "",
                "_This introduction is **pinned** — you cannot close it._"
        ));
        return d;
    }
}
