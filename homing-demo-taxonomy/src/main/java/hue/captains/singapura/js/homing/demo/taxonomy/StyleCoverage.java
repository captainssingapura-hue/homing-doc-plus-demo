package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.ComponentTrees;
import hue.captains.singapura.js.homing.component.taxonomy.Casting;
import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.Impl;
import hue.captains.singapura.js.homing.component.taxonomy.Taxonomy;
import hue.captains.singapura.js.homing.component.taxonomy.VirtualComponent;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.design.semantic.DesignStyle;
import hue.captains.singapura.js.homing.design.styles.CastingStyle;
import hue.captains.singapura.js.homing.design.styles.StylingRes;
import hue.captains.singapura.js.homing.design.styling.StyleManager;
import hue.captains.singapura.js.homing.design.styling.Styling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * How each house style draws each casting, as the central style manager has it (RFC 0067, appendix The Style Jars): the
 * matrix the coverage widget shows - a row per casting, a component on its own and then in each role it is cast in, and
 * a column per style, its base layer first.
 *
 * <ul>
 *   <li><b>A cell</b>: {@code own} - the style's own CSS for the casting; {@code as <casting>} - a delegation, the
 *       casting drawn as another casting of its component; {@code base} - the style leaves it, and its base layer draws
 *       it. The base layer's column is never {@code base}.</li>
 *   <li><b>Worn</b>: whether the component's implementation wears the new sheet - its module, a helper it imports, or
 *       the base of a branch in its lineage imports the {@code Look} manager; a part it holds does not make it so - or
 *       still the old design system's classes, or has no implementation yet.</li>
 * </ul>
 *
 * @param columns the columns, in order: the base layer, then the styles
 * @param rows    a row per casting, in the taxonomy's order
 */
record StyleCoverage(List<Column> columns, List<Row> rows) {

    /** A column: a style, by its token, and whether it is the base layer or a style over it. */
    record Column(String id, String label, String is) {}

    /**
     * A casting's row.
     *
     * @param id        the casting's token
     * @param component its component's token
     * @param role      its role's name, none for the component on its own
     * @param worn      whether its component wears the new sheet
     * @param cells     by column: {@code own}, {@code base}, or {@code as:<casting>}
     */
    record Row(String id, String component, String role, String worn, Map<String, String> cells) {}

    /** The matrix: every casting of the styling's taxonomy against its base layer and every style it serves. */
    static StyleCoverage of(Styling styling, List<Crate> topLevel, EsModule<?> look) {
        StyleManager manager = styling.manager();
        Taxonomy taxonomy = styling.taxonomy();
        DesignStyle<?> base = manager.base();
        var styles = new ArrayList<DesignStyle<?>>();
        var columns = new ArrayList<Column>();
        styles.add(base);
        columns.add(new Column(base.token(), base.label(), "base"));
        for (DesignStyle<?> s : styling.styles()) {
            styles.add(s);
            columns.add(new Column(s.token(), s.label(), "style"));
        }
        Map<Component<?>, String> worn = worn(taxonomy, topLevel, look);
        var rows = new ArrayList<Row>();
        for (Casting c : taxonomy.castings()) {
            if (c.cast() instanceof VirtualComponent<?>) continue;   // a design draws it, never a style
            var cells = new LinkedHashMap<String, String>();
            for (DesignStyle<?> s : styles) cells.put(s.token(), cell(manager, manager.implementation(s, c)));
            rows.add(new Row(c.token(), c.cast().token(), c.standalone() ? null : c.role().name().value(),
                    worn.getOrDefault(c.cast(), "none"), cells));
        }
        return new StyleCoverage(List.copyOf(columns), List.copyOf(rows));
    }

    /** What a style's own implementation of a casting is: its own CSS, a delegation to another casting, or none. */
    private static String cell(StyleManager manager, Optional<CastingStyle<?, ?, ?, ?>> implementation) {
        if (implementation.isEmpty()) return "base";
        if (implementation.get().style() instanceof StylingRes.Delegation<?, ?> d)
            return "as:" + manager.casting(d.to()).map(Casting::token).orElse("?");
        return "own";
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

    /** The matrix as the page is handed it. */
    String json() {
        var b = new StringBuilder("{\"columns\":[");
        for (int i = 0; i < columns.size(); i++) {
            Column c = columns.get(i);
            b.append(i == 0 ? "" : ",").append("{\"id\":").append(quote(c.id())).append(",\"label\":").append(quote(c.label()))
             .append(",\"is\":").append(quote(c.is())).append('}');
        }
        b.append("],\"rows\":[");
        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            b.append(i == 0 ? "" : ",").append("{\"id\":").append(quote(r.id())).append(",\"component\":").append(quote(r.component()))
             .append(",\"role\":").append(r.role() == null ? "null" : quote(r.role())).append(",\"worn\":").append(quote(r.worn())).append(",\"cells\":{");
            int j = 0;
            for (var e : r.cells().entrySet()) b.append(j++ == 0 ? "" : ",").append(quote(e.getKey())).append(':').append(quote(e.getValue()));
            b.append("}}");
        }
        return b.append("]}").toString();
    }

    private static String quote(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }
}
