package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Fixtures;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.studio.base.app.tree.ContentTree;
import hue.captains.singapura.js.homing.studio.starter.StudioStarterFixtures;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.ontology.ValueObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * homing-demo's Fixtures override. It is {@link StudioStarterFixtures} — the
 * batteries-included studio + workspace wiring (RFC 0040) — plus the one
 * demo-specific thing: {@link #trees()}, surfacing the {@link AnimalsTree}
 * demo ContentTrees.
 *
 * <p>The Studio Workspace (the {@code genericWorkspace} app, the {@code studio}
 * spec, and {@code GET /catalogue-tree} rooted at the umbrella's primary
 * {@code home()} — here {@code MultiStudioHome}) now arrives for free by
 * delegating to the starter; no hand-wired workspace endpoint or app remains.
 * The {@code trees()} seam still demonstrates how a studio registers
 * data-authored ContentTrees — Bootstrap wires {@code TreeRegistry} /
 * {@code TreeGetAction} / {@code TreeAppHost} automatically when non-empty.</p>
 */
public record DemoFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {

    public DemoFixtures {
        Objects.requireNonNull(umbrella);
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

    @Override public List<ContentTree> trees() {
        return List.of(AnimalsTree.INSTANCE, InteractiveAnimalsTree.INSTANCE);
    }
}
