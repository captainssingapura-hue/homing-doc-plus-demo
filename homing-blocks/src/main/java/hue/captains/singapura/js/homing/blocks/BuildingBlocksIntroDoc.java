package hue.captains.singapura.js.homing.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Landing page for the Building Blocks studio. Explains what the
 * catalogue is for, who it's aimed at, and how to navigate.
 */
public record BuildingBlocksIntroDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0000-4001-8000-000000000001");
    public static final BuildingBlocksIntroDoc INSTANCE = new BuildingBlocksIntroDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Building Blocks — Read Me First"; }
    @Override public String summary() {
        return "Landing page for the Building Blocks reference studio. Explains how to use the "
             + "catalogue, the per-block guide-plus-demo pattern, and where to look next.";
    }
    @Override public String category(){ return "DOC"; }

    @Override public List<Reference> references() {
        return List.of();
    }
}
