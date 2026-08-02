package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.libs.ExternalModuleUrlRegistry;
import hue.captains.singapura.js.homing.libs.MermaidProxyModule;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.studio.starter.StudioStarterFixtures;

/**
 * Port 1 — the studio serving the Mermaid doc, wired to the local CDN.
 *
 * <p>Before booting, it OVERRIDES {@link MermaidProxyModule}'s URL to point at the
 * {@link LocalCdnServer} (port 2). From then on, every page's Mermaid import goes to the
 * local CDN, never the public one — the whole point of the demo.</p>
 *
 * <pre>{@code
 * mvn -o -f mermaid-mirror-demo/pom.xml \
 *     org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
 *     -Dexec.mainClass=hue.captains.singapura.js.homing.mermaidmirror.MermaidStudioServer
 * }</pre>
 *
 * <p>Ports: {@code -Dmermaid.studio.port} (default 8090),
 * {@code -Dmermaid.cdn.url} (default {@code http://localhost:8091/mermaid.esm.min.mjs}).</p>
 */
public final class MermaidStudioServer {

    private MermaidStudioServer() {}

    public static void main(String[] args) {
        int studioPort = Integer.getInteger("mermaid.studio.port", 8090);
        String cdnUrl = System.getProperty("mermaid.cdn.url",
                "http://localhost:8091/mermaid.esm.min.mjs");

        // (3) Override the proxy so Mermaid loads from OUR local CDN, not jsDelivr.
        ExternalModuleUrlRegistry.INSTANCE.override(MermaidProxyModule.class, cdnUrl);

        Umbrella<Studio<?>> umbrella = new Umbrella.Solo<>(MermaidDemoStudio.INSTANCE);
        System.out.println("Mermaid studio → mermaid served from: " + cdnUrl);
        System.out.println("Open: http://localhost:" + studioPort
                + "/app?app=doc-reader&doc=" + MermaidMirrorDoc.INSTANCE.uuid());
        new Bootstrap<>(new StudioStarterFixtures<>(umbrella), new DefaultRuntimeParams(studioPort)).start();
    }
}
