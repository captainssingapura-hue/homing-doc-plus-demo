package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueRoutes;
import hue.captains.singapura.js.homing.docview.site.DocRoutes;
import hue.captains.singapura.js.homing.libs.ExternalModuleUrlRegistry;
import hue.captains.singapura.js.homing.libs.MermaidProxyModule;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

/**
 * Port 1 — the site serving the Mermaid doc ({@link MermaidSite}), wired to the local CDN.
 *
 * <p>Before serving, it OVERRIDES {@link MermaidProxyModule}'s URL to point at the
 * {@link LocalCdnServer} (port 2). From then on, every page's Mermaid import goes to the
 * local CDN, never the public one — the whole point of the demo. DocView imports Mermaid
 * through that same proxy, so the override is all it takes.</p>
 *
 * <pre>{@code
 * mvn -o -f mermaid-mirror-demo/pom.xml \
 *     org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
 *     -Dexec.mainClass=hue.captains.singapura.js.homing.mermaidmirror.MermaidStudioServer
 * }</pre>
 *
 * <p>Ports: {@code -Dmermaid.studio.port} (default 8108),
 * {@code -Dmermaid.cdn.url} (default {@code http://localhost:8109/mermaid.esm.min.mjs}).</p>
 */
public final class MermaidStudioServer {

    private MermaidStudioServer() {}

    /** The CDN the proxy imports Mermaid from, unless {@code -Dmermaid.cdn.url} says otherwise. */
    static final String DEFAULT_CDN_URL = "http://localhost:8109/mermaid.esm.min.mjs";

    public static void main(String[] args) {
        int port = Integer.getInteger("mermaid.studio.port", 8108);
        String cdnUrl = System.getProperty("mermaid.cdn.url", DEFAULT_CDN_URL);

        // (3) Override the proxy so Mermaid loads from OUR local CDN, not jsDelivr.
        mirrorTo(cdnUrl);

        var routes = DocRoutes.with(CatalogueRoutes.with(MermaidSite.MPA.registry(MermaidSite.INSTANCE), MermaidSite.ROUTER), MermaidSite.ROUTER);
        new VertxActionHost(routes, HostConfig.http(port)).start()
                .onSuccess(s -> {
                    System.out.println("Mermaid site → mermaid served from: " + cdnUrl);
                    System.out.println("Open: http://localhost:" + s.actualPort() + "/mermaid");
                })
                .onFailure(err -> { err.printStackTrace(); System.exit(1); });
    }

    /** Every page's Mermaid import, from {@code cdnUrl}: the proxy's address overridden, process-wide. */
    static void mirrorTo(String cdnUrl) {
        ExternalModuleUrlRegistry.INSTANCE.override(MermaidProxyModule.class, cdnUrl);
    }
}
