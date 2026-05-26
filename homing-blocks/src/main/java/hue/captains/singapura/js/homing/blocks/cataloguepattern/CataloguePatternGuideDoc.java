package hue.captains.singapura.js.homing.blocks.cataloguepattern;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record CataloguePatternGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0005-4001-8000-000000000001");
    public static final CataloguePatternGuideDoc INSTANCE = new CataloguePatternGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Catalogue Pattern — Guide"; }
    @Override public String summary() {
        return "How to compose a studio's content into a typed L0 → L1 → L2 hierarchy "
             + "with Entry variants (OfDoc, OfIllustration, OfStudio). DocProvider for "
             + "doc-registry surfacing.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
