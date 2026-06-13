package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.demo.es.DancingAnimalsWidget;
import hue.captains.singapura.js.homing.demo.es.MovingAnimalWidget;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimalsWidget;
import hue.captains.singapura.js.homing.workspace.RibbonItem;
import hue.captains.singapura.js.homing.workspace.WidgetEntry;
import hue.captains.singapura.js.homing.workspace.WidgetGroup;
import hue.captains.singapura.js.homing.workspace.WidgetIcon;
import hue.captains.singapura.js.homing.workspace.WidgetLabel;
import hue.captains.singapura.js.homing.workspace.shell.ActionDispatch;
import hue.captains.singapura.js.homing.workspace.shell.PartyDecl;
import hue.captains.singapura.js.homing.workspace.shell.WidgetCodecRef;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpec;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpecRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static hue.captains.singapura.js.homing.workspace.RibbonItem.ChoiceOption;

/**
 * Stateless declaration of the Animals Playground workspace, used by
 * {@code GenericWorkspace} (the substrate's single composition AppModule)
 * via {@code ?app=genericWorkspace&ws_kind=animalPlayground}.
 *
 * <p>Parallel reference to the V1 {@link AnimalsPlayground} — both ship
 * simultaneously. V1 stays untouched as the ground-truth live reference;
 * V2 mounts via {@code mountWorkspaceShell(branch, parent, spec)} in
 * {@code WorkspaceShellChromeModule.js}.</p>
 *
 * <p>This class is <i>pure declarations</i> — no body JS, no JSON
 * building, no chrome wiring. Every workspace-specific knob the
 * substrate respects is here:</p>
 *
 * <ul>
 *   <li>{@link #widgetEntries()} — same four widgets as V1.</li>
 *   <li>{@link #ribbonItems()} — same Animal selector as V1.</li>
 *   <li>{@link #parties()} — AnimalsParty declaration (same Secretary
 *       module + ribbon-selector actor).</li>
 *   <li>{@link #actionDispatch()} — typed {@code animal-selected} →
 *       {@link ActionDispatch#tellParty} entry; substrate dispatches
 *       without per-workspace JS.</li>
 *   <li>{@link #widgetCodecs()} — empty in this milestone; the
 *       MovingAnimalWidget custom codec lands as its own module in a
 *       later cycle (task #216) and gets referenced here.</li>
 * </ul>
 *
 * <p>Registration is class-load: a static initializer registers the
 * singleton on first reference to {@link #INSTANCE}.
 * {@code DemoBaseStudio.apps()} references {@link #INSTANCE} during
 * studio bootstrap, so the spec is registered before any request can
 * hit the chrome.</p>
 *
 * @since post-RFC-0034 workspace chrome decomposition — V2 milestone
 */
public final class AnimalPlaygroundSpec implements WorkspaceSpec {

    public static final AnimalPlaygroundSpec INSTANCE;

    static {
        INSTANCE = new AnimalPlaygroundSpec();
        WorkspaceSpecRegistry.INSTANCE.register(INSTANCE);
    }

    private AnimalPlaygroundSpec() {}

    @Override public String kind()  { return "animalPlayground"; }
    @Override public String title() { return "Animals Playground (V2)"; }

    @Override
    public List<WidgetEntry> widgetEntries() {
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

    @Override
    public List<RibbonItem> ribbonItems() {
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

    @Override
    public List<PartyDecl> parties() {
        return List.of(
                PartyDecl.of("animals", AnimalsSecretaryModule.INSTANCE, "AnimalsSecretary")
                         .withActor("animals/ribbon-selector")
                         .exposedAs("animalsParty")
                         .build()
        );
    }

    @Override
    public Map<String, ActionDispatch> actionDispatch() {
        return Map.of(
                "animal-selected",
                ActionDispatch.tellParty(
                        "animals", "animals/ribbon-selector",
                        "AnimalSelectionRequested", "animal")
        );
    }

    @Override
    public List<WidgetCodecRef> widgetCodecs() {
        // Phase-3 candidate: the MovingAnimalWidget custom codec gets
        // extracted into its own JS module in a later cycle (task #216),
        // then referenced here. Until then, the substrate's identity
        // codec covers every widget; MovingAnimal-specific persistence
        // round-trips lose detail until the extraction lands.
        return List.of();
    }

    @Override
    public List<String> pinnedSpawns() {
        // The welcome doc auto-mounts in slot 'tl' at boot. Spec-level
        // declaration — independent of the widget class's default
        // lifecycleHint (DocViewWidget itself stays MULTI; the spec is
        // what decides one instance auto-spawns here).
        return List.of("DocViewWidget");
    }

    /** Defaults for the pinned introduction — mirrors V1 verbatim. */
    private static Map<String, String> introDefaults() {
        var d = new LinkedHashMap<String, String>();
        d.put("title", "Welcome to the Animals Playground (V2)");
        d.put("body", String.join("\n",
                "This is the **composition-model V2** of the Animals Playground —",
                "mounted via the substrate's `GenericWorkspace` + `WorkspaceSpec`",
                "rather than a per-workspace chrome class. V1 lives unchanged at",
                "`?app=animals-playground` as the ground-truth reference; this V2",
                "lives at `?app=genericWorkspace&ws_kind=animalPlayground`.",
                "",
                "Open the picker with the **➕** button — the same widgets V1 hosts",
                "run here unchanged. (Widget independence verified by reuse.)",
                "",
                "_This introduction is **pinned** — you cannot close it._"
        ));
        return d;
    }
}
