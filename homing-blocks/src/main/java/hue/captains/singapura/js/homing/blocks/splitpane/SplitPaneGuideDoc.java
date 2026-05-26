package hue.captains.singapura.js.homing.blocks.splitpane;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** Guide doc for the SplitPane building block. */
public record SplitPaneGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0006-4001-8000-000000000001");
    public static final SplitPaneGuideDoc INSTANCE = new SplitPaneGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "SplitPane — Guide"; }
    @Override public String summary() {
        return "Recursive 2D split-pane primitive. Pane tree (leaf | split), divider drag, "
             + "renderSlot callback, lifecycle. Foundation for the flexible workspace.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
