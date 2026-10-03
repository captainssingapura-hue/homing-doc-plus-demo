package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.design.DesignClass;
import hue.captains.singapura.js.homing.design.Trees;

import java.util.ArrayList;
import java.util.List;

/**
 * What the page draws, generated from the Java that owns it: the words the
 * board reads and the colours the registry offers its base. The JS never
 * spells a variable or a theme slug — it reads these two constants — so
 * adding a word or a palette is a Java edit and the board follows.
 *
 * <p>A {@link SelfContent} module: the framework appends the export prologue;
 * the body is these lines.</p>
 */
public record SwatchboardData() implements EsModule<SwatchboardData>, SelfContent {

    public static final SwatchboardData INSTANCE = new SwatchboardData();

    /** {@code [{ name: "--base-color-surface-background-color" }, …]} in board order: each word's variable at rest. */
    public record TOKENS() implements Exportable._Constant<SwatchboardData> {}

    /** {@code [{ slug: "bare", label: "Chalk", inspiration: "…" }, …]}: the base dressed in each of its colours, in registry order. */
    public record THEMES() implements Exportable._Constant<SwatchboardData> {}

    @Override
    public List<String> selfContent(ModuleNameResolver nameResolver) {
        List<String> lines = new ArrayList<>();
        lines.add("// Generated from SwatchboardStyles.BOARD and BareThemes.REGISTRY — do not hand-edit.");

        StringBuilder t = new StringBuilder("const TOKENS = Object.freeze([");
        boolean first = true;
        for (DesignClass<?> word : SwatchboardStyles.BOARD) {
            if (!first) t.append(", ");
            first = false;
            String property = word.targetLeaf().properties().contains("background-color") ? "background-color"
                            : word.targetLeaf().properties().contains("border-color") ? "border-color" : "color";
            t.append("{ name: \"").append(Trees.variable(word, property)).append("\" }");
        }
        lines.add(t.append("]);").toString());

        StringBuilder th = new StringBuilder("const THEMES = Object.freeze([");
        first = true;
        Theme base = BareThemes.REGISTRY.bases().get(0);
        for (Theme colours : BareThemes.REGISTRY.colours()) {
            if (!first) th.append(", ");
            first = false;
            th.append("{ slug: \"").append(BareThemes.REGISTRY.dressed(base, colours).slug()).append("\", label: \"").append(colours.label())
              .append("\", inspiration: \"").append(colours.inspiration().replace("\"", "\\\"")).append("\" }");
        }
        lines.add(th.append("]);").toString());
        return lines;
    }

    @Override public ImportsFor<SwatchboardData> imports() { return ImportsFor.noImports(); }

    @Override public ExportsOf<SwatchboardData> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new TOKENS(), new THEMES()));
    }
}
