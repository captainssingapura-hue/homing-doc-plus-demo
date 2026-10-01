package hue.captains.singapura.js.homing.demo.workspacewidgets;

import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every name a served module of this repo takes from another module is one it
 * imports. The JavaScript tests load modules into one scope, where a class is
 * there whether it was imported or not; a browser loads each module with only
 * what it declares, and a name used but not imported is undefined there — as
 * {@code LayoutAlgebra} once was in the fold, and a stored log was cleared for
 * it. So this reads each module's script for the names another module in the
 * closure exports, and holds them to its declared imports.
 */
class DeclaredImportsTest {

    private static final List<Crate> TOP = List.of(DemoWorkspaceWidgetsCrate.INSTANCE);

    @Test
    void everyNameTakenFromAnotherModuleIsImported() throws IOException {
        var closure = new ArrayList<>(CrateClosure.of(TOP));
        Map<String, String> exporter = new HashMap<>();
        for (Crate c : closure) for (CrateEntry e : c.entries()) {
            for (var x : e.module().exports().exports()) exporter.putIfAbsent(x.getClass().getSimpleName(), e.moduleClass());
        }
        var problems = new ArrayList<String>();
        for (Crate c : closure) {
            if (!c.name().startsWith("homing-demo-workspace")) continue;
            for (CrateEntry e : c.entries()) {
                String script = script(e.moduleClass());
                if (script == null) continue;
                Set<String> own = new HashSet<>(), imported = new HashSet<>();
                for (var x : e.module().exports().exports()) own.add(x.getClass().getSimpleName());
                for (var mi : e.module().imports().getAllImports().values()) {
                    for (var x : mi.allImports()) imported.add(x.getClass().getSimpleName());
                }
                String code = strip(script);
                Set<String> local = new HashSet<>();
                Matcher d = DECLARED.matcher(code);
                while (d.find()) local.add(d.group(1));
                Set<String> missing = new java.util.TreeSet<>();
                Matcher m = NAME.matcher(code);
                while (m.find()) {
                    String n = m.group(1);
                    String from = exporter.get(n);
                    if (from != null && !from.equals(e.moduleClass()) && !own.contains(n) && !imported.contains(n) && !local.contains(n)) missing.add(n);
                }
                if (!missing.isEmpty()) problems.add(e.moduleClass() + " uses " + missing + " without importing them");
            }
        }
        assertEquals(List.of(), problems);
    }

    /**
     * A plain module never imports a DOM module. A DOM module is served with the
     * page's theme, and a plain module's imports without it: a DOM module a plain
     * one imports loads a second time, a copy of its own - its classes not the
     * page's, so an instance made by one fails `instanceof` in the other (the
     * Keyboard's §15 hazard). The log's events came out of the layers so, and the
     * store refused every one.
     */
    @Test
    void noPlainModuleImportsADomModule() {
        var problems = new ArrayList<String>();
        for (Crate c : CrateClosure.of(TOP)) {
            if (!c.name().startsWith("homing-demo-workspace")) continue;
            for (CrateEntry e : c.entries()) {
                if (e.module() instanceof hue.captains.singapura.js.homing.core.DomModule<?>) continue;
                for (var mi : e.module().imports().getAllImports().values()) {
                    if (mi.from() instanceof hue.captains.singapura.js.homing.core.DomModule<?> d) problems.add(e.moduleClass() + " is plain, and imports the DOM module " + d.getClass().getName());
                }
            }
        }
        assertEquals(List.of(), problems);
    }

    /** A capitalised name read as a value, not as a member: not after a dot. */
    private static final Pattern NAME = Pattern.compile("(?<![\\w$.])([A-Z][A-Za-z0-9_$]*)\\b");
    private static final Pattern DECLARED = Pattern.compile("\\b(?:class|function|const|let|var)\\s+([A-Za-z_$][\\w$]*)");

    private static String strip(String js) {
        return js.replaceAll("(?s)/\\*.*?\\*/", " ")
                 .replaceAll("(?m)//.*$", " ")
                 .replaceAll("\"(?:[^\"\\\\\\n]|\\\\.)*\"", "\"\"")
                 .replaceAll("'(?:[^'\\\\\\n]|\\\\.)*'", "''")
                 .replaceAll("(?s)`(?:[^`\\\\]|\\\\.)*`", "``");
    }

    private static String script(String moduleClass) throws IOException {
        try (InputStream in = DeclaredImportsTest.class.getResourceAsStream("/homing/js/" + moduleClass.replace('.', '/') + ".js")) {
            return in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
