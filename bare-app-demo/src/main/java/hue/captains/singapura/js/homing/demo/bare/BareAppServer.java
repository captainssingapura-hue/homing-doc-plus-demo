package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.js.homing.server.AppMeta;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.server.QueryParamResolver;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

import java.util.List;

/**
 * RFC 0066 — the bare app's server: the framework's action registry over
 * one app and one theme registry, hosted directly. No Bootstrap, no
 * Fixtures, no catalogue, no studio — this file is the whole deployment.
 *
 * <pre>
 *   mvn -pl bare-app-demo -am compile exec:java \
 *       -Dexec.mainClass=hue.captains.singapura.js.homing.demo.bare.BareAppServer
 * </pre>
 * Listens on 8093 ({@code -Dbare.port}); the page is {@code /app?app=swatchboard}.
 */
public final class BareAppServer {

    private static final int PORT = Integer.getInteger("bare.port", 8093);

    private BareAppServer() {}

    public static void main(String[] args) {
        var registry = new HomingActionRegistry(
                new QueryParamResolver("/module"),
                new SimpleAppResolver(List.of(Swatchboard.INSTANCE)),
                ResourceReader.INSTANCE,
                BareThemes.Registry.INSTANCE,
                AppMeta.DEFAULT,
                BareAppCrate.servable());
        new VertxActionHost(registry, HostConfig.http(PORT)).start()
                .onSuccess(server -> System.out.println(
                        "Swatchboard listening on http://localhost:" + server.actualPort() + "/app?app=swatchboard"))
                .onFailure(err -> {
                    System.err.println("Failed to start: " + err.getMessage());
                    System.exit(1);
                });
    }
}
