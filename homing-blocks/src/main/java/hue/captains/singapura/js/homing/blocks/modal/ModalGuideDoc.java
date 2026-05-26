package hue.captains.singapura.js.homing.blocks.modal;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** Guide doc for the Modal building block. */
public record ModalGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0008-4001-8000-000000000001");
    public static final ModalGuideDoc INSTANCE = new ModalGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Modal — Guide"; }
    @Override public String summary() {
        return "A draggable, resizable floating panel. Standalone — no coupling to other "
             + "primitives. Reusable for any floating UI: dialogs, palettes, inspectors, "
             + "detached widgets.";
    }
    @Override public String category(){ return "GUIDE"; }
    @Override public List<Reference> references() { return List.of(); }
}
