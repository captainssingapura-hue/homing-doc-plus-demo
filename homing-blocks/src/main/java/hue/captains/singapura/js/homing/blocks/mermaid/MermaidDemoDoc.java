package hue.captains.singapura.js.homing.blocks.mermaid;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * A quick demo {@link ClasspathMarkdownDoc} whose markdown body contains Mermaid
 * diagrams (fenced {@code ```mermaid} blocks). Demonstrates authoring
 * diagrams-as-text inside an ordinary doc.
 *
 * <p>Rendered through the same pipeline as every other Doc. Note: marked.js
 * renders a {@code ```mermaid} fence as a code block by default — turning it
 * into a live SVG needs the {@code MermaidProxyModule} wired into the markdown
 * renderer, which is a separate step from adding this doc.</p>
 */
public record MermaidDemoDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("b10c4500-000d-4001-8000-00000000000d");
    public static final MermaidDemoDoc INSTANCE = new MermaidDemoDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Mermaid — Demo"; }
    @Override public String summary() {
        return "A markdown doc containing Mermaid diagrams (flowchart + sequence). Shows "
             + "diagrams-as-text authoring and how the configurable MermaidProxyModule "
             + "realises them.";
    }
    @Override public String category(){ return "DEMO"; }
    @Override public List<Reference> references() { return List.of(); }
}
