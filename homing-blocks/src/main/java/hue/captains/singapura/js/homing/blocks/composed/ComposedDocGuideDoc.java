package hue.captains.singapura.js.homing.blocks.composed;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record ComposedDocGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0003-4001-8000-000000000001");
    public static final ComposedDocGuideDoc INSTANCE = new ComposedDocGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "ComposedDoc — Guide"; }
    @Override public String summary() {
        return "How to construct a ComposedDoc: the typed Segment sealed hierarchy, "
             + "Markdown/Text/Code/Svg/Table/Image variants, TOC building, the build() "
             + "convenience pattern.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
