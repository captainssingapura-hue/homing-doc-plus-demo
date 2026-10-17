package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.demo.components.DemoComponentsCrate;
import hue.captains.singapura.js.homing.design.styling.Styling;
import hue.captains.singapura.js.homing.relgrid.house.GridFrameBaseModule;
import hue.captains.singapura.js.homing.ui.look.LookModule;
import hue.captains.singapura.js.homing.ui.styles.HouseStyles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The coverage matrix, read from the house's style manager (RFC 0067, appendix The Style Jars): a row per casting, Base
 * drawing every one, each house style its own CSS or a delegation for what it refines and Base's for the rest; a role
 * drawn as the component on its own a delegation; the house's marks and the modules' own components left to Base.
 */
class StyleCoverageTest {

    /** The house's styling with the grid's frame, as the demo's site registers it. */
    private static final Styling STYLING = HouseStyles.INSTANCE.styling(List.of(GridFrameBaseModule.INSTANCE));

    private static final StyleCoverage HOUSE = StyleCoverage.of(STYLING, List.of(DemoComponentsCrate.INSTANCE), LookModule.INSTANCE);

    private static String cell(String casting, String column) {
        return HOUSE.rows().stream().filter(r -> r.id().equals(casting)).findFirst().orElseThrow().cells().get(column);
    }

    private static long count(String column, String kind) {
        return HOUSE.rows().stream().filter(r -> r.cells().get(column).startsWith(kind)).count();
    }

    @Test
    void theColumns_areBase_thenTheSevenStyles() {
        assertEquals(List.of("base", "editorial", "neo-brutalism", "neo-futurism", "neumorphism", "glassmorphism", "retro-futurism", "sketchy"),
                HOUSE.columns().stream().map(StyleCoverage.Column::id).toList());
        assertEquals("base", HOUSE.columns().get(0).is());
    }

    @Test
    void aRowPerCasting_theComponentOnItsOwn_thenInItsRoles() {
        assertEquals(STYLING.taxonomy().castings().size(), HOUSE.rows().size(), "every casting of the house");
        var go = HOUSE.rows().stream().filter(r -> r.id().equals("plain-button_go")).findFirst().orElseThrow();
        assertEquals("plain-button", go.component());
        assertEquals("go", go.role());
        assertEquals(null, HOUSE.rows().stream().filter(r -> r.id().equals("plain-button")).findFirst().orElseThrow().role());
        assertEquals(HOUSE.rows().size(), HOUSE.json().split("\"worn\"").length - 1, "a row each in what the page is handed");
    }

    @Test
    void aCell_isTheStylesOwnCss_aDelegation_orLeftToBase() {
        assertEquals("own", cell("plain-button", "base"));
        assertEquals("own", cell("plain-button", "neo-brutalism"));
        assertEquals("as:plain-button", cell("plain-button_go", "editorial"), "the Go button drawn as the plain button on its own");
        assertEquals("base", cell("boundary-line", "sketchy"), "a boundary is Base's, alike in every style");
        assertEquals("own", cell("boundary-line", "base"));
        assertEquals("base", cell("theme-widget", "editorial"), "a module's own component, no look of its own but its parts'");
        for (var r : HOUSE.rows())
            for (var e : r.cells().entrySet())
                if (e.getValue().startsWith("as:")) {
                    String to = e.getValue().substring(3);
                    assertTrue(HOUSE.rows().stream().anyMatch(o -> o.id().equals(to) && o.component().equals(r.component())),
                            r.id() + " in " + e.getKey() + " delegates to " + to + ", a casting of its own component");
                }
    }

    @Test
    void baseDrawsEveryCasting_eachStyle124ItselfAndLeaves52() {
        assertEquals(0, count("base", "base"), "the base layer leaves nothing");
        assertEquals(135, count("base", "own"), "the text cell among them (D42), and every icon, each its own picture (D52)");
        assertEquals(60, count("base", "as:"), "the text cell as a title and as a value among them (D42); an icon never: its look its own whatever it plays (D52)");
        // each style draws its own cross but Editorial, whose is Base's, and Sketchy five icons more (D52); the rest it leaves to Base
        var own = new java.util.TreeMap<String, Long>();
        var as = new java.util.TreeMap<String, Long>();
        var base = new java.util.TreeMap<String, Long>();
        for (var c : HOUSE.columns()) {
            if (c.is().equals("base")) continue;
            own.put(c.id(), count(c.id(), "own"));
            as.put(c.id(), count(c.id(), "as:"));
            base.put(c.id(), count(c.id(), "base"));
        }
        assertEquals(java.util.Map.of("editorial", 73L, "neo-brutalism", 74L, "neo-futurism", 74L, "neumorphism", 74L, "glassmorphism", 74L,
                "retro-futurism", 74L, "sketchy", 79L), own, "each style's own CSS");
        for (var e : as.entrySet()) assertEquals(51L, e.getValue(), e.getKey() + "'s delegations, each a role drawn as its component on its own");
        assertEquals(java.util.Map.of("editorial", 71L, "neo-brutalism", 70L, "neo-futurism", 70L, "neumorphism", 70L, "glassmorphism", 70L,
                "retro-futurism", 70L, "sketchy", 65L), base, "what each leaves to Base - the text cell's three among them (D42), the icons it draws as Base does (D52)");
    }

    @Test
    void whatWearsTheNewSheet_isReadFromTheDemosImplementations() {
        assertTrue(HOUSE.rows().stream().anyMatch(r -> r.worn().equals("look")), "the realized components that wear the new sheet");
        assertTrue(HOUSE.rows().stream().anyMatch(r -> r.worn().equals("none")), "the castings whose component has no implementation among the demos'");
        assertEquals("look", HOUSE.rows().stream().filter(r -> r.id().equals("plain-button")).findFirst().orElseThrow().worn());
    }
}
