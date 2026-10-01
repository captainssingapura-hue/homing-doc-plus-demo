package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueRoutes;
import hue.captains.singapura.js.homing.docview.site.DocRoutes;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

/**
 * Serves {@link DemoSite} through its MPA, with the catalogue's route - what each listing reads
 * its catalogue from - and DocView's - a doc's payload, by its authentic path - before the site's
 * catch-all. {@code mvn -o -pl homing-demo-site exec:java}, on 8082 unless {@code -Ddemo.port}
 * says otherwise.
 */
public final class DemoSiteServer {

    private static final int PORT = Integer.getInteger("demo.port", 8082);

    private DemoSiteServer() {}

    public static void main(String[] args) {
        var routes = DocRoutes.with(CatalogueRoutes.with(DemoSite.MPA.registry(DemoSite.INSTANCE), DemoSite.ROUTER), DemoSite.ROUTER);
        new VertxActionHost(routes, HostConfig.http(PORT)).start()
                .onSuccess(s -> System.out.println("[DemoSiteServer] http://localhost:" + s.actualPort() + "/"))
                .onFailure(err -> { err.printStackTrace(); System.exit(1); });
    }
}
