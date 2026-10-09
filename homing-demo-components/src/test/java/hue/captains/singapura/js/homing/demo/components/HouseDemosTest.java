package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
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
        assertEquals(40, HOUSE.demos().size(), "twenty-four elements - nine of them texts; eight overlays, menus and splits; eight the page around does not show");
        assertEquals(16, HOUSE.aroundIt().size(), "the workspace's six, the chrome's two, the preferences' eight");
        assertEquals(List.of(), HOUSE.pending(), "none still to come");
        assertEquals(29, HOUSE.unrealized().size());
        assertEquals(List.of("ButtonDemo"), HOUSE.demos().stream().filter(d -> d.leaf() == HouseControls.DangerButton.INSTANCE)
                .map(Demo::className).toList(), "the nine buttons are one button");
        assertEquals(List.of("TextDemo"), HOUSE.demos().stream().filter(d -> d.leaf() == HouseText.Pill.INSTANCE)
                .map(Demo::className).toList(), "the nine texts are one text");
    }

    @Test
    void theRegistry_generatedFromTheDeclaration() {
        String js = String.join("\n", HouseDemosModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("\"danger-button\": ButtonDemo"), js);
        assertTrue(js.contains("\"summary-card\": SummaryCardDemo"), js);
        assertTrue(js.contains("\"dock-grid\": \"the workspace itself"), js);
        assertTrue(js.contains("\"dialog\": DialogDemo"), js);
        assertTrue(js.contains("\"add-tab\": AddTabDemo"), js);
        assertTrue(js.contains("const HOUSE_PENDING = Object.freeze([]);"), js);
        assertTrue(js.contains("\"heading\": TextDemo"), js);
        assertTrue(js.contains("\"lamp\""), "an unrealized leaf: " + js);
        assertEquals(24, HouseDemosModule.INSTANCE.imports().getAllImports().size(), "each demo's module imported once, whatever it shows");
    }
}
