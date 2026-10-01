package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueRoutes;
import hue.captains.singapura.js.homing.docview.site.DocRoutes;
import hue.captains.singapura.js.homing.workspace.log.store.FileCheckpointStorage;
import hue.captains.singapura.js.homing.workspace.log.store.StoredCheckpointKeeper;
import hue.captains.singapura.js.homing.workspace.shell.server.WorkspaceServer;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

import java.nio.file.Path;

/**
 * Serves {@link DemoSite} through its MPA, with the catalogue's route - what each listing reads
 * its catalogue from - DocView's - a doc's payload, by its authentic path - and the workspaces' -
 * their states kept in files - before the site's catch-all. {@code mvn -o -pl homing-demo-site
 * exec:java}, on 8082 unless {@code -Ddemo.port} says otherwise; checkpoints under
 * {@code target/workspace-checkpoints} of where it is started ({@code -Dworkspace.checkpoints}).
 */
public final class DemoSiteServer {

    private static final int PORT = Integer.getInteger("demo.port", 8082);

    static final FileCheckpointStorage STORAGE =
            new FileCheckpointStorage(Path.of(System.getProperty("workspace.checkpoints", "target/workspace-checkpoints")));

    private DemoSiteServer() {}

    public static void main(String[] args) {
        var site = DemoSite.MPA.registry(DemoSite.INSTANCE);
        var routes = WorkspaceServer.with(DocRoutes.with(CatalogueRoutes.with(site, DemoSite.ROUTER), DemoSite.ROUTER), new StoredCheckpointKeeper(STORAGE));
        new VertxActionHost(routes, HostConfig.http(PORT)).start()
                .onSuccess(s -> System.out.println("[DemoSiteServer] http://localhost:" + s.actualPort() + "/ - checkpoints kept under " + STORAGE.root()))
                .onFailure(err -> { err.printStackTrace(); System.exit(1); });
    }
}
