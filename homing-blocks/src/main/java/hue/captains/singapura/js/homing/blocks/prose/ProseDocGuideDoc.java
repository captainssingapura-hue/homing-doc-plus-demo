package hue.captains.singapura.js.homing.blocks.prose;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record ProseDocGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0002-4001-8000-000000000001");
    public static final ProseDocGuideDoc INSTANCE = new ProseDocGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "ProseDoc — Guide"; }
    @Override public String summary() {
        return "How to create a ProseDoc — a Doc whose body is a markdown file on the "
             + "classpath. ClasspathMarkdownDoc + the .md companion + UUID + references.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
