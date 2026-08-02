package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** The one Mermaid-enabled doc served by {@link MermaidStudioServer} (port 1). */
public record MermaidMirrorDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("b10c4500-000e-4001-8000-00000000000e");
    public static final MermaidMirrorDoc INSTANCE = new MermaidMirrorDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Mermaid via Local CDN"; }
    @Override public String summary() {
        return "A Mermaid diagram whose library is served by the local CDN (port 8091), not "
             + "the public one — the studio overrides MermaidProxyModule's URL at boot.";
    }
    @Override public String category(){ return "DEMO"; }
    @Override public List<Reference> references() { return List.of(); }
}
