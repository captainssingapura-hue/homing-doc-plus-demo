package hue.captains.singapura.js.homing.blocks.prose;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Live ProseDoc demo. The doc you're reading IS a ProseDoc — its
 * Java record + companion markdown are the working example.
 */
public record ProseDocDemoDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0002-4001-8000-000000000002");
    public static final ProseDocDemoDoc INSTANCE = new ProseDocDemoDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "ProseDoc — Demo"; }
    @Override public String summary() {
        return "A live ProseDoc demonstrating the pattern. Reading this is reading what "
             + "your ProseDoc would look like.";
    }
    @Override public String category(){ return "DEMO"; }
    @Override public List<Reference> references() { return List.of(); }
}
