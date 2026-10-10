package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.ComponentTrees;
import hue.captains.singapura.js.homing.component.taxonomy.Impl;
import hue.captains.singapura.js.homing.component.taxonomy.Casting;
import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.Root;
import hue.captains.singapura.js.homing.component.taxonomy.Taxonomy;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.design.State;
import hue.captains.singapura.js.homing.design.Target;
import hue.captains.singapura.js.homing.design.semantic.ColorPalette;
import hue.captains.singapura.js.homing.design.semantic.DeriveTreatments;
import hue.captains.singapura.js.homing.design.semantic.DesignStyle;
import hue.captains.singapura.js.homing.design.semantic.Dressed;
import hue.captains.singapura.js.homing.design.semantic.ResolveSemanticClass;
import hue.captains.singapura.js.homing.design.semantic.Resolution;
import hue.captains.singapura.js.homing.design.semantic.SemanticTokens;
import hue.captains.singapura.js.homing.design.semantic.Treatment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * What each house style and palette says of each component, derived from the answers: the
 * matrix the coverage widget shows.
 *
 * <ul>
 *   <li><b>A style's cell</b>, the style worn over its bases: of the component's looks at rest on
 *       every target that is not colour, how many the style answers itself, how many a base answers
 *       above the root, the rest falling to the root - the element as the browser leaves it; and
 *       of its treatments that are not colour, how many the style answers itself and how many a
 *       base does.</li>
 *   <li><b>A palette's cell</b>: of the component's colour looks, how many the palette answers above
 *       the root; and of its colour treatments, how many it answers.</li>
 *   <li><b>A style written over nothing</b> - Base - is a column of its own: what it answers
 *       above the root, which every style written over it inherits.</li>
 *   <li><b>Worn</b>: whether the component's implementation wears the new sheet - its module, a
 *       helper it imports, or the base of a branch in its lineage imports the {@code Look} manager;
 *       a part it holds does not make it so - or still the old design system's classes, or has no
 *       implementation yet.</li>
 * </ul>
 *
 * @param looks    how many looks a style's cell counts out of: the CSS targets that are not colour
 * @param colours  how many a palette's counts out of: the CSS colour targets
 * @param columns  the columns, in order: Base, the styles, the palettes
 * @param rows     a row per component, in the taxonomy's order
 */
record StyleCoverage(int looks, int colours, List<Column> columns, List<Row> rows) {

    /** A column: a style or a palette, by its token, and whether it is Base, a style over it, or a palette. */
    record Column(String id, String label, String is) {}

    /**
     * A cell: what the column answers itself, at rest and in treatments, and what its bases answer
     * above the root for it.
     */
    record Cell(int own, int base, int treated, int baseTreated) {}

    /** A component's row: its token, whether it wears the new sheet, and its cells by column. */
    record Row(String id, String worn, Map<String, Cell> cells) {}

    /**
     * The matrix: every component of the taxonomy against the base, every style over it and every
     * palette, with what wears the {@code look} module read from the implementations in the crates.
     */
    static StyleCoverage of(Taxonomy taxonomy, DesignStyle base, List<DesignStyle> styles, List<ColorPalette> palettes,
                            List<Crate> topLevel, EsModule<?> look) {
        var tokens = SemanticTokens.INSTANCE;
        List<Target> plain = tokens.cssLeaves().stream().filter(t -> !(t instanceof Target.Color)).toList();
        List<Target> colour = tokens.cssLeaves().stream().filter(t -> t instanceof Target.Color).toList();
        ColorPalette any = palettes.get(0);
        var columns = new ArrayList<Column>();
        var themes = new LinkedHashMap<String, Dressed>();
        columns.add(new Column(base.token(), label(base.token()), "base"));
        themes.put(base.token(), new Dressed(base, any));
        for (DesignStyle s : styles) {
            columns.add(new Column(s.token(), label(s.token()), "style"));
            themes.put(s.token(), new Dressed(s, any));
        }
        for (ColorPalette p : palettes) {
            // a palette's column has an id of its own: Neo-brutalism's and Neo-futurism's own colours share their styles' tokens
            columns.add(new Column("palette-" + p.token(), label(p.token()), "palette"));
            themes.put("palette-" + p.token(), new Dressed(styles.get(0), p));
        }
        var treatments = new HashMap<String, List<Treatment>>();
        themes.forEach((id, theme) -> treatments.put(id, DeriveTreatments.INSTANCE.of(taxonomy, theme)));
        Map<Component<?>, String> worn = worn(taxonomy, topLevel, look);

        var rows = new ArrayList<Row>();
        for (Component<?> c : taxonomy.components()) {
            var cells = new LinkedHashMap<String, Cell>();
            for (Column col : columns) {
                Dressed theme = themes.get(col.id());
                boolean palette = col.is().equals("palette");
                String own = palette ? theme.palette().token() : theme.style().token();
                int ownLooks = 0, baseLooks = 0, ownTreated = 0, baseTreated = 0;
                for (Target t : palette ? colour : plain) {
                    var r = ResolveSemanticClass.INSTANCE.resolve(taxonomy, theme, new Casting(c), State.REST, t);
                    if (!(r.outcome() instanceof Resolution.Answered a) || a.node().component() instanceof Root) continue;
                    if (a.layer().equals(own)) ownLooks++; else baseLooks++;
                }
                for (Treatment tr : treatments.get(col.id())) {
                    if (!tr.component().equals(c) || (tr.target() instanceof Target.Color) != palette) continue;
                    var a = (Resolution.Answered) ResolveSemanticClass.INSTANCE.resolve(taxonomy, theme, new Casting(c), tr.state(), tr.target()).outcome();
                    if (a.layer().equals(own)) ownTreated++; else baseTreated++;
                }
                cells.put(col.id(), new Cell(ownLooks, baseLooks, ownTreated, baseTreated));
            }
            rows.add(new Row(c.token(), worn.getOrDefault(c, "none"), cells));
        }
        return new StyleCoverage(plain.size(), colour.size(), List.copyOf(columns), List.copyOf(rows));
    }

    /** Each realized component, and whether its implementation wears the new sheet ({@code look}) or the old ({@code old}). */
    private static Map<Component<?>, String> worn(Taxonomy taxonomy, List<Crate> topLevel, EsModule<?> look) {
        var out = new HashMap<Component<?>, String>();
        for (Crate crate : ComponentTrees.closure(topLevel))
            for (CrateEntry e : crate.entries())
                if (e.module() instanceof Impl impl && impl.anchor() instanceof Component<?> c)
                    out.put(c, wears(e.module(), look, Set.copyOf(taxonomy.fallback(c)), new HashSet<>()) ? "look" : "old");
        return out;
    }

    /**
     * Whether a component's module wears what it is asked of - imports it itself, through a helper,
     * or through the base of a branch in its own lineage. What it is made of is not what it wears:
     * another leaf's module, a part it holds, is never walked into, nor a branch's base outside its
     * lineage.
     */
    private static boolean wears(EsModule<?> m, EsModule<?> wanted, Set<?> lineage, Set<EsModule<?>> seen) {
        if (m.equals(wanted)) return true;
        if (!seen.add(m)) return false;
        for (ModuleImports<?> mi : m.imports().getAllImports().values()) {
            if (!(mi.from() instanceof EsModule<?> next)) continue;
            if (next instanceof Impl impl && !lineage.contains(impl.anchor())) continue;   // another leaf, or a branch not its own
            if (wears(next, wanted, lineage, seen)) return true;
        }
        return false;
    }

    /** {@code neo-brutalism} as a column says it: {@code Neo-brutalism}. */
    private static String label(String token) { return Character.toUpperCase(token.charAt(0)) + token.substring(1); }

    /** The matrix as the page is handed it. */
    String json() {
        var b = new StringBuilder("{\"looks\":").append(looks).append(",\"colours\":").append(colours).append(",\"columns\":[");
        for (int i = 0; i < columns.size(); i++) {
            Column c = columns.get(i);
            b.append(i == 0 ? "" : ",").append("{\"id\":").append(quote(c.id())).append(",\"label\":").append(quote(c.label()))
             .append(",\"is\":").append(quote(c.is())).append('}');
        }
        b.append("],\"rows\":[");
        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            b.append(i == 0 ? "" : ",").append("{\"id\":").append(quote(r.id())).append(",\"worn\":").append(quote(r.worn())).append(",\"cells\":{");
            int j = 0;
            for (var e : r.cells().entrySet()) {
                Cell c = e.getValue();
                b.append(j++ == 0 ? "" : ",").append(quote(e.getKey())).append(":[").append(c.own()).append(',').append(c.base())
                 .append(',').append(c.treated()).append(',').append(c.baseTreated()).append(']');
            }
            b.append("}}");
        }
        return b.append("]}").toString();
    }

    private static String quote(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }
}
