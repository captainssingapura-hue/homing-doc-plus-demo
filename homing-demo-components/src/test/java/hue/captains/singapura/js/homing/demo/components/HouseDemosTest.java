package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.VirtualComponent;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.util.SimplePrefixResolver;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseControls;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseText;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseTaxonomy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Every leaf of the house is placed once - a demo, the page around, still to come, or realized by nothing - and the page's registry is the declaration's. */
class HouseDemosTest {

    private static final HouseDemos HOUSE = HouseDemos.INSTANCE;

    @Test
    void everyLeafOfTheHouse_placedOnce() {
        var placed = new ArrayList<Component<?>>();
        HOUSE.demos().forEach(d -> placed.add(d.leaf()));
        HOUSE.aroundIt().forEach(a -> placed.add(a.leaf()));
        placed.addAll(HOUSE.pending());
        placed.addAll(HOUSE.unrealized());
        assertEquals(placed.size(), new HashSet<>(placed).size(), "a leaf placed twice");
        var leaves = HouseTaxonomy.INSTANCE.read().components().stream().filter(c -> !(c instanceof VirtualComponent<?>)).toList();   // a design draws a virtual leaf
        assertEquals(Set.copyOf(leaves), Set.copyOf(placed), "every leaf of the house but a virtual one, and nothing else");
    }

    @Test
    void everyRealizedLeaf_shownInAction_byADemoOrByThePageAroundIt() {
        assertEquals(44, HOUSE.demos().size(), "thirty-one elements - fifteen of them texts, a table cell's words among them (D42), a heading for each level among them, one a link; seven overlays, menus and splits - the split pane retired; six the page around does not show");
        assertEquals(31, HOUSE.aroundIt().size(), "a panel's bar, in the panel's demo, an edge strip's lip, a slider group's rack and the clickable card under the summary view, in theirs; the bar of tabs' tail and the brand's mark; the workspace's seven - a floating pane's title bar among them - the dock grid retired, its regions a split grid's cells and the docks residing in them - the control panel, the chrome's eight - its ground, its bar, the brand and the trail among them - the preferences' nine");
        assertEquals(List.of(), HOUSE.pending(), "none still to come");
        assertEquals(41, HOUSE.unrealized().size(), "the parts nothing realizes - the slider's and the toggle's knobs and grooves apart, the slider's rail - and the new-tab control and its picture, retired for now - the brand and the trail realized now, on the site's bar; the browser's own text box and checkbox, realized by their owners on native ones; a document's code, quote, table, badges and its strong and muted words, realized by the doc's widgets on its own elements");
        assertEquals(List.of("ButtonDemo"), HOUSE.demos().stream().filter(d -> d.leaf() == HouseControls.DangerButton.INSTANCE)
                .map(Demo::className).toList(), "the nine buttons are one button");
        assertEquals(List.of("TextDemo"), HOUSE.demos().stream().filter(d -> d.leaf() == HouseText.Pill.INSTANCE)
                .map(Demo::className).toList(), "the nine texts are one text");
    }

    @Test
    void theRegistry_generatedFromTheDeclaration() {
        String js = String.join("\n", HouseDemosModule.INSTANCE.selfContent(RESOLVER));
        assertTrue(js.contains(where("danger-button", ButtonDemoModule.INSTANCE, "ButtonDemo")), js);
        assertTrue(js.contains(where("summary-view", SummaryViewDemoModule.INSTANCE, "SummaryViewDemo")), js);
        assertTrue(js.contains(where("dialog", DialogDemoModule.INSTANCE, "DialogDemo")), js);
        assertTrue(js.contains(where("tab-opener", TabOpenerDemoModule.INSTANCE, "TabOpenerDemo")), js);
        assertTrue(js.contains(where("heading1", TextDemoModule.INSTANCE, "TextDemo")), "the fourteen texts, one module: " + js);
        assertTrue(js.contains("\"multi-tab-pane\": \"each region of the workspace"), js);
        assertTrue(js.contains("const HOUSE_PENDING = Object.freeze([]);"), js);
        assertTrue(js.contains("\"lamp\""), "an unrealized leaf: " + js);
        assertEquals(0, HouseDemosModule.INSTANCE.imports().getAllImports().size(),
                "no demo imported: a page loads each when a leaf it shows is first picked");
    }

    private static final ModuleNameResolver RESOLVER = new SimplePrefixResolver("/m/");

    /** A leaf's entry as the page has it: the demo module's address, and the class it exports. */
    private static String where(String leaf, EsModule<?> module, String export) {
        return "\"" + leaf + "\": Object.freeze({ module: \"" + RESOLVER.resolve(module).basePath() + "\", export: \"" + export + "\" })";
    }
}
