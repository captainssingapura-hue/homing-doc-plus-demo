package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
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
        assertEquals(Set.copyOf(HouseTaxonomy.INSTANCE.read().components()), Set.copyOf(placed), "every leaf of the house, and nothing else");
    }

    @Test
    void everyRealizedLeaf_shownInAction_byADemoOrByThePageAroundIt() {
        assertEquals(39, HOUSE.demos().size(), "twenty-five elements - nine of them texts, one a link; eight overlays, menus and splits; six the page around does not show");
        assertEquals(21, HOUSE.aroundIt().size(), "the workspace's seven, the control panel, the chrome's four, the preferences' nine");
        assertEquals(List.of(), HOUSE.pending(), "none still to come");
        assertEquals(27, HOUSE.unrealized().size(), "the parts nothing realizes, and the new-tab control and its picture, retired for now");
        assertEquals(List.of("ButtonDemo"), HOUSE.demos().stream().filter(d -> d.leaf() == HouseControls.DangerButton.INSTANCE)
                .map(Demo::className).toList(), "the nine buttons are one button");
        assertEquals(List.of("TextDemo"), HOUSE.demos().stream().filter(d -> d.leaf() == HouseText.Pill.INSTANCE)
                .map(Demo::className).toList(), "the nine texts are one text");
    }

    @Test
    void theRegistry_generatedFromTheDeclaration() {
        String js = String.join("\n", HouseDemosModule.INSTANCE.selfContent(RESOLVER));
        assertTrue(js.contains(where("danger-button", ButtonDemoModule.INSTANCE, "ButtonDemo")), js);
        assertTrue(js.contains(where("summary-card", SummaryCardDemoModule.INSTANCE, "SummaryCardDemo")), js);
        assertTrue(js.contains(where("dialog", DialogDemoModule.INSTANCE, "DialogDemo")), js);
        assertTrue(js.contains(where("tab-opener", TabOpenerDemoModule.INSTANCE, "TabOpenerDemo")), js);
        assertTrue(js.contains(where("heading", TextDemoModule.INSTANCE, "TextDemo")), "the nine texts, one module: " + js);
        assertTrue(js.contains("\"dock-grid\": \"the workspace itself"), js);
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
