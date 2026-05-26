package hue.captains.singapura.js.homing.blocks.cataloguepattern;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * A leaf doc inside the L2 demo sub-catalogue. Its presence demonstrates
 * how a Doc sits at the bottom of a typed catalogue chain and how the
 * breadcrumb chain reads when rendered.
 */
public record CataloguePatternDemoLeafDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0005-4001-8000-000000000003");
    public static final CataloguePatternDemoLeafDoc INSTANCE = new CataloguePatternDemoLeafDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Catalogue Pattern — L2 Leaf Doc"; }
    @Override public String summary() {
        return "A leaf doc registered three levels deep: L0 (Building Blocks) → L1 "
             + "(Catalogue Pattern) → L2 (L2 Demo Sub-Catalogue) → this doc. The "
             + "breadcrumb chain walks the parent() pointers.";
    }
    @Override public String category(){ return "DEMO"; }
    @Override public List<Reference> references() { return List.of(); }
}
