package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.ComponentBranch;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseBranches;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseTaxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The coverage matrix, derived from the answers: a style's cell counts what it answers itself of a
 * component's looks at rest and of its treatments, Base's what it answers above the root, a
 * palette's the colours; the nine buttons and the nine texts wear the new sheet; a style's
 * looks reach the buttons and the texts alone, its treatments every control.
 */
class StyleCoverageTest {

    private static final StyleCoverage HOUSE = StyleCoverageModule.HOUSE;

    private static StyleCoverage.Cell cell(String component, String column) {
        return HOUSE.rows().stream().filter(r -> r.id().equals(component)).findFirst().orElseThrow().cells().get(column);
    }

    @Test
    void theColumns_areBase_theSevenStyles_andThePalettes() {
        assertEquals(List.of("base", "editorial", "neo-brutalism", "neo-futurism", "neumorphism", "glassmorphism", "retro-futurism", "sketchy",
                        "palette-harbour", "palette-neo-brutalism", "palette-neo-futurism", "palette-clay", "palette-frost", "palette-synthwave", "palette-marker", "palette-forest"),
                HOUSE.columns().stream().map(StyleCoverage.Column::id).toList());
        assertEquals(24, HOUSE.looks(), "the CSS targets that are not colour");
        assertEquals(9, HOUSE.colours(), "the semantic colours");
    }

    @Test
    void aButtonsCells_countWhatEachColumnAnswersItself() {
        // Editorial: corner, border, face, ease, shadow, inset, gap, extent at rest; Base the cursor and the type scale above the root;
        // its own lift, press and ring and how it is disabled, Base the disabled shadow and transform
        assertEquals(new StyleCoverage.Cell(8, 2, 10, 2), cell("primary-button", "editorial"));
        assertEquals(new StyleCoverage.Cell(7, 2, 8, 2), cell("primary-button", "sketchy"), "ink casts no shadow: no shadow at rest, no lift's shadow");
        assertEquals(new StyleCoverage.Cell(7, 2, 9, 2), cell("primary-button", "neumorphism"), "no border; a hover's lift but no press");
        assertEquals(new StyleCoverage.Cell(2, 0, 2, 0), cell("primary-button", "base"), "what Base answers above the root: every style inherits it");
        assertEquals(new StyleCoverage.Cell(3, 0, 4, 0), cell("primary-button", "palette-harbour"), "its background, ink and border; the ring's four states");
        for (String p : List.of("forest", "frost", "neo-brutalism"))
            assertEquals(cell("primary-button", "palette-harbour"), cell("primary-button", "palette-" + p), "one rule, eight sets of seeds: " + p);
    }

    @Test
    void theNineButtonsTheNineTextsAndTheCompositesSoFarWearTheNewSheet_andOnlyTheirBranchesAreStyled() {
        var worn = HOUSE.rows().stream().filter(r -> r.worn().equals("look")).map(StyleCoverage.Row::id).collect(Collectors.toSet());
        assertEquals(Set.of("plain-button", "primary-button", "secondary-button", "danger-button", "warning-button", "success-button", "toggle-button",
                "close-button", "icon-button",
                "heading", "kicker", "caption", "lede", "label", "readout", "badge", "pill", "wordmark",
                "link", "crumb", "scrim", "separator", "delimiter",
                "split-grid-mirror", "slider", "context-menu", "tab-strip", "tab-pane", "single-tab-pane", "toggle-widget",
                "choice-widget", "list-master-widget", "overview-widget", "focus-monitor", "scale-widget", "steward-monitor", "dialog",
                "summary-card", "slider-group"), worn,
                "the buttons, the texts, the ways, the marks, the veil; and the composites so far, the split grid's mirror, the slider, the context menu"
                + " and the tab strip - with the tab-pane whose chip is a tab, and the single-tab pane whose bar is a strip - the toggle widget's switch, the items the choice, list-master and overview widgets and the focus monitor draw, the scale widget's range, the steward monitor's lamp, the dialog's row of actions, and the cards - a summary card, a group of sliders");
        assertTrue(HOUSE.rows().stream().anyMatch(r -> r.worn().equals("old")), "the realized components still on the old design's classes");
        assertTrue(HOUSE.rows().stream().anyMatch(r -> r.worn().equals("none")), "the leaves with no implementation yet");
        var styled = new java.util.HashSet<>(under(HouseBranches.Button.INSTANCE));
        styled.addAll(under(HouseBranches.Text.INSTANCE));
        styled.addAll(Set.of("link", "crumb", "scrim", "separator", "pane-thumb", "slider", "groove", "knob", "detent", "tick", "context-menu", "menu-item", "tab", "tab-strip",
                "choice-option", "list-row", "setting-row", "lamp", "action-bar",
                "plain-card", "summary-card", "slider-group", "preferences-button"));
        assertEquals(styled, answeredBySomeStyle(true),
                "a style's looks are the buttons', the texts', the ways', the separator's, the veil's, a pane thumbnail's rule, the slider's parts, the menu's frame and rows, a tab and its strip, and the items - a choice, a listed row, a setting's row - and a lamp, whose corners are a button's; a row of actions, capped as the style rules groups apart; the cards, and a group of sliders framed as one; a preferences button, framed as a button is: every one's, and nothing else's");
        var treated = new java.util.HashSet<>(under(HouseBranches.Control.INSTANCE));
        treated.addAll(under(HouseBranches.Item.INSTANCE));
        treated.addAll(under(HouseBranches.Card.INSTANCE));
        treated.add("slider-group");
        assertEquals(treated, answeredBySomeStyle(false),
                "its treatments reach every control - the ring - and every control and item when it cannot be used; every card, lifted under the hand and pressed, and a group of sliders ringed while it holds the keys");
        assertEquals(HOUSE.rows().size(), HOUSE.json().split("\"worn\"").length - 1, "a row each in what the page is handed");
    }

    /** The components some style answers itself: its looks at rest, or its treatments. */
    private static Set<String> answeredBySomeStyle(boolean looks) {
        return HOUSE.rows().stream()
                .filter(r -> HOUSE.columns().stream().filter(c -> c.is().equals("style"))
                        .anyMatch(c -> (looks ? r.cells().get(c.id()).own() : r.cells().get(c.id()).treated()) > 0))
                .map(StyleCoverage.Row::id).collect(Collectors.toSet());
    }

    /** The components whose lineage holds a branch. */
    private static Set<String> under(ComponentBranch branch) {
        var taxonomy = HouseTaxonomy.INSTANCE.read();
        return taxonomy.components().stream().filter(c -> taxonomy.fallback(c).contains(branch)).map(Component::token).collect(Collectors.toSet());
    }
}
