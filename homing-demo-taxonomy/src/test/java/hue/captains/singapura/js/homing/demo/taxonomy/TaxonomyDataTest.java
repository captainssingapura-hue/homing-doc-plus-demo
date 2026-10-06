package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.design.Trees;
import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseTaxonomy;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What the page is handed is the house's taxonomy, whole: read as JavaScript through the index,
 * every node is there with what it is, where the tree places it and how it falls back - a part
 * through its base, never its owner - and its semantic classes, one per target leaf; and the
 * node-selection secretary keeps one pick and tells it.
 */
class TaxonomyDataTest extends JsModuleTestBase {

    private static final String DIR = "/homing/js/hue/captains/singapura/js/homing/demo/taxonomy/";

    @BeforeEach
    void load() {
        js = buildContext();
        js.eval("js", "var TAXONOMY = " + TaxonomyDataModule.json(HouseTaxonomy.INSTANCE.read(), Trees.targetLeaves()) + ";");
        loadModule(DIR + "TaxonomyIndexModule.js");
        loadModule(DIR + "NodeSelectionSecretaryModule.js");
        js.eval("js", "var t = TaxonomyIndex.the();");
    }

    private Value eval(String src) { return js.eval("js", src); }

    @Test
    void everyNodeIsThere_theRootFirst() {
        var house = HouseTaxonomy.INSTANCE.read();
        assertEquals(house.nodes().size(), eval("t.nodes().length").asInt());
        assertEquals("root", eval("t.root().id").asString());
        assertEquals(16, eval("t.count('kind')").asInt());
        assertEquals(81, eval("t.count('component')").asInt());
        assertEquals(house.parts().size(), eval("t.count('part')").asInt());
        assertEquals(81, eval("t.componentsUnder('root').length").asInt(), "every component under the root");
    }

    @Test
    void aComponent_itsPartsUnderIt_andWhereItPlaysARole() {
        assertEquals("card-head,card-title,card-badge,card-body,card-text,card-foot,card-link", eval("t.node('card').children.join()").asString());
        assertEquals("Title · Heading", eval("t.label('card-title')").asString(), "a part said as its role and what plays it");
        assertTrue(eval("t.node('heading').playedIn.indexOf('card-title') >= 0").asBoolean(), "a heading plays the card's title");
        assertEquals("container", eval("t.node('card').parent").asString());
    }

    @Test
    void aPartFallsBackThroughItsBase_theTreePlacesItUnderItsOwner() {
        assertEquals("card-title,heading,text,root", eval("t.node('card-title').fallback.join()").asString());
        assertEquals("card,container,root", eval("t.above('card-title').join()").asString(), "under its owner in the tree");
        assertEquals("dialog-frame,floating-pane,pane,container,root", eval("t.node('dialog-frame').fallback.join()").asString());
    }

    @Test
    void theParts_aComponentsOwn_nothingElseHasAny() {
        assertEquals("card-head,card-title,card-badge,card-body,card-text,card-foot,card-link",
                     eval("t.partsOf('card').map(function (p) { return p.id; }).join()").asString(), "a component's own, in the order it names them");
        assertEquals("dialog-backdrop,dialog-frame,dialog-actions", eval("t.partsOf('dialog').map(function (p) { return p.id; }).join()").asString());
        assertEquals(0, eval("t.partsOf('heading').length").asInt(), "a component that names none");
        assertEquals(0, eval("t.partsOf('pane').length").asInt(), "a kind has none: the parts are a component's");
        assertEquals(0, eval("t.partsOf('root').length").asInt());
        assertEquals(0, eval("t.partsOf('card-title').length").asInt(), "a part has none");
    }

    @Test
    void everyNodeHasASemanticClass_perTargetLeaf() {
        assertEquals(Trees.targetLeaves().size(), eval("t.classes('card-title').length").asInt());
        assertTrue(eval("t.classes('card-title').indexOf('card-title-color-ink') >= 0").asBoolean());
    }

    @Test
    void theSecretary_keepsOnePick_andTellsIt() {
        Value s = global("NodeSelectionSecretary");
        Value r = s.getMember("behavior").execute(s.getMember("initial"), envelope("Select", Map.of("id", "card"), "tree"));
        assertEquals("card", r.getMember("newState").getMember("id").asString());
        assertEquals("BroadcastToMembers", r.getMember("actions").getArrayElement(0).getMember("kind").asString());
        Value again = s.getMember("behavior").execute(r.getMember("newState"), envelope("Select", Map.of("id", "card"), "details"));
        assertEquals(0, again.getMember("actions").getArraySize(), "the same pick again says nothing");
    }
}
