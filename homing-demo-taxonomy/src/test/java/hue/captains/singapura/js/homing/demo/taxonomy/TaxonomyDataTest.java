package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.ComponentPartDSL;
import hue.captains.singapura.js.homing.component.taxonomy.L1_ComponentBranch;
import hue.captains.singapura.js.homing.component.taxonomy.L2_ComponentBranch;
import hue.captains.singapura.js.homing.component.taxonomy.ReadTaxonomy;
import hue.captains.singapura.js.homing.component.taxonomy.Root;
import hue.captains.singapura.js.homing.component.taxonomy.Slot;
import hue.captains.singapura.js.homing.component.taxonomy.Taxonomy;
import hue.captains.singapura.js.homing.design.Trees;
import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseSaying;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseShaping;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseTaxonomy;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What the page is handed, read as JavaScript through the index: every node with what it is, where
 * the levelled tree places it and how it falls back - a part through its base, never its owner -
 * and its semantic classes, one per target leaf. The house is handed whole; its parts are yet to be
 * declared, so the parts are read off a small hypothetical taxonomy over the house's roles. And the
 * node-selection secretary keeps one pick and tells it.
 */
class TaxonomyDataTest extends JsModuleTestBase {

    private static final String DIR = "/homing/js/hue/captains/singapura/js/homing/demo/taxonomy/";

    // ── a hypothetical taxonomy with parts, over the house's roles ──────────

    record Text() implements L1_ComponentBranch<Root> {
        static final Text INSTANCE = new Text();
        @Override public Root parent() { return Root.INSTANCE; }
    }

    record Container() implements L1_ComponentBranch<Root> {
        static final Container INSTANCE = new Container();
        @Override public Root parent() { return Root.INSTANCE; }
    }

    record Pane() implements L2_ComponentBranch<Container> {
        static final Pane INSTANCE = new Pane();
        @Override public Container parent() { return Container.INSTANCE; }
    }

    record Heading() implements Component<Text> {
        static final Heading INSTANCE = new Heading();
        @Override public Text parent() { return Text.INSTANCE; }
    }

    record Caption() implements Component<Text> {
        static final Caption INSTANCE = new Caption();
        @Override public Text parent() { return Text.INSTANCE; }
    }

    record FloatingPane() implements Component<Pane> {
        static final FloatingPane INSTANCE = new FloatingPane();
        @Override public Pane parent() { return Pane.INSTANCE; }
    }

    record Card() implements Component<Container> {
        static final Card INSTANCE = new Card();
        private static final ComponentPartDSL DSL = ComponentPartDSL.INSTANCE;
        @Override public Container parent() { return Container.INSTANCE; }
        @Override public List<Slot<?>> parts() {
            return List.of(DSL.part(Heading.INSTANCE).as(HouseSaying.Title.INSTANCE).one(),
                           DSL.part(Caption.INSTANCE).as(HouseSaying.Summary.INSTANCE).optional());
        }
    }

    record Dialog() implements Component<Container> {
        static final Dialog INSTANCE = new Dialog();
        private static final ComponentPartDSL DSL = ComponentPartDSL.INSTANCE;
        @Override public Container parent() { return Container.INSTANCE; }
        @Override public List<Slot<?>> parts() {
            return List.of(DSL.part(FloatingPane.INSTANCE).as(HouseShaping.Window.INSTANCE).one(),
                           DSL.part(Heading.INSTANCE).as(HouseSaying.Title.INSTANCE).optional());
        }
    }

    private static final Taxonomy SAMPLE = new ReadTaxonomy().read(List.of(Card.INSTANCE, Dialog.INSTANCE));

    private Value load(Taxonomy taxonomy) {
        js = buildContext();
        js.eval("js", "var TAXONOMY = " + TaxonomyDataModule.json(taxonomy, Trees.targetLeaves()) + ";");
        loadModule(DIR + "TaxonomyIndexModule.js");
        loadModule(DIR + "NodeSelectionSecretaryModule.js");
        return js.eval("js", "var t = TaxonomyIndex.the(); t");
    }

    private Value eval(String src) { return js.eval("js", src); }

    @Test
    void theHouse_whole_itsBranchesLevelled() {
        var house = HouseTaxonomy.INSTANCE.read();
        load(house);
        assertEquals(house.nodes().size(), eval("t.nodes().length").asInt());
        assertEquals("root", eval("t.root().id").asString());
        assertEquals(16, eval("t.count('branch')").asInt());
        assertEquals(81, eval("t.count('component')").asInt());
        assertEquals(0, eval("t.count('part')").asInt(), "its slots are yet to be declared over the role catalogue");
        assertEquals(81, eval("t.componentsUnder('root').length").asInt(), "every component under the root");
        assertEquals("container", eval("t.node('pane').parent").asString(), "a level-2 branch under its level-1 parent");
    }

    @Test
    void aComponent_itsPartsUnderIt_andWhereItPlaysARole() {
        load(SAMPLE);
        assertEquals("card-title,card-summary", eval("t.node('card').children.join()").asString());
        assertEquals("Title · Heading", eval("t.label('card-title')").asString(), "a part said as its role and what plays it");
        assertEquals("card-title,dialog-title", eval("t.node('heading').playedIn.join()").asString(), "a heading plays two titles");
        assertEquals("container", eval("t.node('card').parent").asString());
    }

    @Test
    void aPartFallsBackThroughItsBase_theTreePlacesItUnderItsOwner() {
        load(SAMPLE);
        assertEquals("card-title,heading,text,root", eval("t.node('card-title').fallback.join()").asString());
        assertEquals("card,container,root", eval("t.above('card-title').join()").asString(), "under its owner in the tree");
        assertEquals("dialog-window,floating-pane,pane,container,root", eval("t.node('dialog-window').fallback.join()").asString(),
                "through a base two levels down");
    }

    @Test
    void theParts_aComponentsOwn_nothingElseHasAny() {
        load(SAMPLE);
        assertEquals("dialog-window,dialog-title", eval("t.partsOf('dialog').map(function (p) { return p.id; }).join()").asString());
        assertEquals(0, eval("t.partsOf('heading').length").asInt(), "a component that names none");
        assertEquals(0, eval("t.partsOf('pane').length").asInt(), "a branch has none: the parts are a component's");
        assertEquals(0, eval("t.partsOf('root').length").asInt());
        assertEquals(0, eval("t.partsOf('card-title').length").asInt(), "a part has none");
    }

    @Test
    void theCatalogue_theHousesRoles_filedUnderTheirBranches() {
        load(HouseTaxonomy.INSTANCE.read());
        assertEquals("role-root", eval("t.roleRoot().id").asString());
        assertEquals(21, eval("t.roleCount('branch')").asInt(), "three at level 1, eighteen at level 2");
        assertEquals(67, eval("t.roleCount('role')").asInt());
        assertEquals(67, eval("t.rolesUnder('role-root').length").asInt(), "every role under the root");
        assertEquals("saying,doing,shaping", eval("t.roleChildren('role-root').map(function (n) { return n.id; }).join()").asString());
        assertEquals("title,subtitle,name,category,symbol", eval("t.roleChildren('naming').map(function (n) { return n.id; }).join()").asString());
        assertEquals(2, eval("t.roleNode('naming').level").asInt());
        assertEquals("saying", eval("t.roleNode('naming').parent").asString());
        assertEquals(0, eval("t.usesOf('title').length").asInt(), "named by no component yet");
    }

    @Test
    void aRolesUses_everyPartThatNamesIt_whatPlaysIt_howMany() {
        load(SAMPLE);
        assertEquals("card-title,dialog-title", eval("t.usesOf('title').map(function (p) { return p.id; }).join()").asString());
        assertEquals("title", eval("t.node('card-title').roleId").asString(), "a part says which role it plays");
        assertEquals("1", eval("t.node('card-title').count").asString());
        assertEquals("0..1", eval("t.node('card-summary').count").asString(), "and how many");
        assertEquals("heading,heading", eval("t.usesOf('title').map(function (p) { return p.base; }).join()").asString());
        assertEquals("layers", eval("t.roleNode('window').parent").asString(), "only the branches the roles reached");
    }

    @Test
    void everyNodeHasASemanticClass_perTargetLeaf() {
        load(SAMPLE);
        assertEquals(Trees.targetLeaves().size(), eval("t.classes('card-title').length").asInt());
        assertTrue(eval("t.classes('card-title').indexOf('card-title-color-ink') >= 0").asBoolean());
    }

    @Test
    void theSecretary_keepsOnePick_andTellsIt() {
        load(SAMPLE);
        Value s = global("NodeSelectionSecretary");
        Value r = s.getMember("behavior").execute(s.getMember("initial"), envelope("Select", Map.of("id", "card"), "tree"));
        assertEquals("card", r.getMember("newState").getMember("id").asString());
        assertEquals("BroadcastToMembers", r.getMember("actions").getArrayElement(0).getMember("kind").asString());
        Value again = s.getMember("behavior").execute(r.getMember("newState"), envelope("Select", Map.of("id", "card"), "details"));
        assertEquals(0, again.getMember("actions").getArraySize(), "the same pick again says nothing");
    }
}
