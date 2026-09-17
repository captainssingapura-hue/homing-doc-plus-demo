package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.js.homing.server.AppMeta;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.server.QueryParamResolver;
import hue.captains.singapura.js.homing.server.RootRedirectGetAction;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RFC 0066 — the bare app's server: the framework's action registry over
 * one app and one theme registry, hosted directly. No Bootstrap, no
 * Fixtures, no catalogue, no studio — this file is the whole deployment.
 * The one thing added over the base registry is the root: {@code /}
 * redirects to the app, the way the studio's Bootstrap does for its home.
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
        var inner = new HomingActionRegistry(
                new QueryParamResolver("/module"),
                new SimpleAppResolver(List.of(Swatchboard.INSTANCE)),
                ResourceReader.INSTANCE,
                BareThemes.Registry.INSTANCE,
                AppMeta.DEFAULT,
                List.of(BareAppCrate.INSTANCE));
        var rootRedirect = new RootRedirectGetAction(Swatchboard.INSTANCE.simpleName());
        var registry = new ActionRegistry<RoutingContext>() {
            @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
                Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
                all.put("/", rootRedirect);
                return all;
            }
            @Override public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
                return inner.postActions();
            }
        };
        new VertxActionHost(registry, HostConfig.http(PORT)).start()
                .onSuccess(server -> System.out.println(
                        "Swatchboard listening on http://localhost:" + server.actualPort() + "/"))
                .onFailure(err -> {
                    System.err.println("Failed to start: " + err.getMessage());
                    System.exit(1);
                });
    }
}
