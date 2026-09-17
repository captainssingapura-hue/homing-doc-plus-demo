package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.theme.color.GlobalColorPalette;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * What the page draws, generated from the Java that owns it: the tokens the
 * global palette declares and the themes the registry lists. The JS never
 * spells a token name or a theme slug — it reads these two constants — so
 * adding a token or a theme is a Java edit and the board follows.
 *
 * <p>A {@link SelfContent} module: the framework appends the export prologue;
 * the body is these lines.</p>
 */
public record SwatchboardData() implements EsModule<SwatchboardData>, SelfContent {

    public static final SwatchboardData INSTANCE = new SwatchboardData();

    /** {@code [{ name: "--color-surface", scale: false }, …]} in name order. */
    public record TOKENS() implements Exportable._Constant<SwatchboardData> {}
    /** {@code [{ slug: "chalk", label: "Chalk", inspiration: "…" }, …]} in registry order. */
    public record THEMES() implements Exportable._Constant<SwatchboardData> {}

    @Override
    public List<String> selfContent(ModuleNameResolver nameResolver) {
        List<String> lines = new ArrayList<>();
        lines.add("// Generated from GlobalColorPalette and BareThemes.Registry — do not hand-edit.");
        List<CssVar> tokens = new ArrayList<>(new GlobalColorPalette.global_color_palette().declares());
        tokens.sort(Comparator.comparing(CssVar::name));
        StringBuilder t = new StringBuilder("const TOKENS = Object.freeze([");
        boolean first = true;
        for (CssVar v : tokens) {
            if (!first) t.append(", ");
            first = false;
            boolean scale = !v.name().startsWith("--color-");
            t.append("{ name: \"").append(v.name()).append("\", scale: ").append(scale).append(" }");
        }
        lines.add(t.append("]);").toString());

        StringBuilder th = new StringBuilder("const THEMES = Object.freeze([");
        first = true;
        for (Theme theme : BareThemes.Registry.INSTANCE.themes()) {
            if (!first) th.append(", ");
            first = false;
            th.append("{ slug: \"").append(theme.slug()).append("\", label: \"").append(theme.label())
              .append("\", inspiration: \"").append(theme.inspiration().replace("\"", "\\\"")).append("\" }");
        }
        lines.add(th.append("]);").toString());
        return lines;
    }

    @Override public ImportsFor<SwatchboardData> imports() { return ImportsFor.noImports(); }

    @Override public ExportsOf<SwatchboardData> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new TOKENS(), new THEMES()));
    }
}
