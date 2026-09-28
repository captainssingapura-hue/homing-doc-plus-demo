package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.workspace.log.store.FileCheckpointStorage;
import hue.captains.singapura.js.homing.workspace.log.store.StoredCheckpointKeeper;
import hue.captains.singapura.js.homing.workspace.shell.server.WorkspaceServer;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

import java.nio.file.Path;

/**
 * Serves {@link DemoWorkspaceSite} through its MPA, and the workspace's own
 * routes beside them: the server keeps its pages' states in files.
 * {@code mvn -pl homing-demo-workspace exec:java}, on 8104 unless
 * {@code -Ddemo.workspace.port} says otherwise; checkpoints under
 * {@code target/workspace-checkpoints} of where it is started
 * ({@code -Dworkspace.checkpoints}).
 */
public final class DemoWorkspaceServer {

    private static final int PORT = Integer.getInteger("demo.workspace.port", 8104);

    static final FileCheckpointStorage STORAGE =
            new FileCheckpointStorage(Path.of(System.getProperty("workspace.checkpoints", "target/workspace-checkpoints")));

    private DemoWorkspaceServer() {}

    public static void main(String[] args) {
        var routes = WorkspaceServer.with(DemoWorkspaceSite.MPA.registry(DemoWorkspaceSite.INSTANCE), new StoredCheckpointKeeper(STORAGE));
        new VertxActionHost(routes, HostConfig.http(PORT)).start()
                .onSuccess(s -> System.out.println("[DemoWorkspaceServer] http://localhost:" + s.actualPort() + "/ - checkpoints kept under " + STORAGE.root()))
                .onFailure(err -> { err.printStackTrace(); System.exit(1); });
    }
}
