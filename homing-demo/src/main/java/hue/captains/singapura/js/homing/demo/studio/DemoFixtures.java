package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Fixtures;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.server.ThemeRegistry;
import hue.captains.singapura.js.homing.studio.starter.StudioStarterFixtures;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceGroupRegistry;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceGroups;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.ontology.ValueObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * homing-demo's Fixtures override. It is {@link StudioStarterFixtures} — the
 * batteries-included studio + workspace wiring (RFC 0040) — plus the one
 * batteries-included studio + workspace wiring (RFC 0040), and nothing more.
 *
 *
 * <p>The Studio Workspace (the {@code genericWorkspace} app, the {@code studio}
 * spec, and {@code GET /catalogue-tree} rooted at the umbrella's primary
 * {@code home()} — here {@code MultiStudioHome}) now arrives for free by
 * delegating to the starter; no hand-wired workspace endpoint or app remains.
 * (The trees() seam went with the tree app - RFC 0053.)
 *
 *
 */
public record DemoFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {

    public DemoFixtures {
        Objects.requireNonNull(umbrella);
        // RFC 0058 — the two workspace groups, after the starter has registered the
        // studio spec (a group validates that every kind it holds is registered),
        // then law 4: every group placed exactly once under the umbrella's home,
        // walked through the hosted studios' proxies.
        new StudioStarterFixtures<>(umbrella);
        DemoWorkspaceGroups.register();
        WorkspaceGroups.assertPlacedOnce(umbrella.studios().get(0).home(), WorkspaceGroupRegistry.INSTANCE);
    }

    /** The batteries-included starter we are, plus the demo's trees. */
    private StudioStarterFixtures<S> starter() {
        return new StudioStarterFixtures<>(umbrella);
    }

    @Override public List<AppModule<?, ?>> harnessApps() {
        return starter().harnessApps();
    }

    @Override public NodeChrome chromeFor(Umbrella<S> node) {
        return starter().chromeFor(node);
    }

    @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> harnessGetActions() {
        return starter().harnessGetActions();
    }

    /** RFC 0066 — the eleven themes the starter installs; studio-base ships none. */
    @Override public ThemeRegistry themeRegistry() {
        return starter().themeRegistry();
    }
}
