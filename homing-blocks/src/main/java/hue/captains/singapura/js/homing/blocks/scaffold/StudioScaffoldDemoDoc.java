package hue.captains.singapura.js.homing.blocks.scaffold;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Demo for the Studio Scaffold block — shows the actual code of the
 * BuildingBlocksStudio scaffolding as the canonical example. The
 * markdown body quotes each file inline; the homing-blocks Java source
 * is the live copy you can study.
 */
public record StudioScaffoldDemoDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0001-4001-8000-000000000002");
    public static final StudioScaffoldDemoDoc INSTANCE = new StudioScaffoldDemoDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Studio Scaffold — Demo"; }
    @Override public String summary() {
        return "Live demo: the actual code of the BuildingBlocksStudio scaffolding. "
             + "Reading this doc is reading the scaffolding pattern as deployed.";
    }
    @Override public String category(){ return "DEMO"; }
    @Override public List<Reference> references() { return List.of(); }
}
