package hue.captains.singapura.js.homing.blocks.svg;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record SvgDocGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0004-4001-8000-000000000001");
    public static final SvgDocGuideDoc INSTANCE = new SvgDocGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "SvgDoc — Guide"; }
    @Override public String summary() {
        return "How to ship a vector graphic as a first-class Doc — typed SvgGroup with "
             + "SvgBeing variants pointing at classpath .svg resources, themed via "
             + "currentColor.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
