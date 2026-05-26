package hue.captains.singapura.js.homing.blocks.scaffold;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** Guide for the Studio Scaffold block. */
public record StudioScaffoldGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0001-4001-8000-000000000001");
    public static final StudioScaffoldGuideDoc INSTANCE = new StudioScaffoldGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Studio Scaffold — Guide"; }
    @Override public String summary() {
        return "How to set up a minimal Homing studio: the Studio interface, a Fixtures "
             + "implementation, the Bootstrap entry point, and the standalone server's "
             + "main method.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
