package hue.captains.singapura.js.homing.blocks.multitabpane;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** Guide doc for the MultiTabPane building block. */
public record MultiTabPaneGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0007-4001-8000-000000000001");
    public static final MultiTabPaneGuideDoc INSTANCE = new MultiTabPaneGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "MultiTabPane — Guide"; }
    @Override public String summary() {
        return "Tabs on SplitPane with a conserved 16-tab budget. Per-pane capacity = "
             + "budget / 2^depth. Split + merge are inverses; no separate close-pane op.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
