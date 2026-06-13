package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.Fixtures;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.studio.base.app.tree.ContentTree;
import hue.captains.singapura.js.homing.studio.workspace.CatalogueTreeGetAction;
import hue.captains.singapura.js.homing.studio.workspace.OpenDocGetAction;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.ontology.ValueObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * RFC 0016 — homing-demo's Fixtures override; delegates to
 * {@link DefaultFixtures} for everything except {@link #trees()}, which
 * surfaces the {@link AnimalsTree} demo ContentTree.
 *
 * <p>Demonstrates the seam: downstream studios register data-authored
 * trees via {@code Fixtures.trees()}; Bootstrap wires the
 * {@code TreeRegistry}, {@code TreeGetAction}, and {@code TreeAppHost}
 * automatically when the list is non-empty.</p>
 */
public record DemoFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {

    public DemoFixtures {
        Objects.requireNonNull(umbrella);
    }

    @Override public List<AppModule<?, ?>> harnessApps() {
        return new DefaultFixtures<>(umbrella).harnessApps();
    }

    @Override public NodeChrome chromeFor(Umbrella<S> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);
    }

    @Override public List<ContentTree> trees() {
        return List.of(AnimalsTree.INSTANCE, InteractiveAnimalsTree.INSTANCE);
    }

    /**
     * Wire the Studio Workspace's {@code /catalogue-tree} endpoint, rooted
     * at {@link DemoStudio} (the demo's content L0). The {@code studio}
     * WorkspaceSpec's pinned {@code TreeWidget} fetches this to draw the
     * navigation tree at {@code ?app=genericWorkspace&ws_kind=studio}.
     *
     * <p>Downstream wiring (not Bootstrap): the action needs the studio's
     * root {@link hue.captains.singapura.js.homing.studio.base.app.Catalogue},
     * which the studio already declares.</p>
     */
    @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> harnessGetActions() {
        return Map.of(
                "/catalogue-tree", new CatalogueTreeGetAction(DemoStudio.INSTANCE),
                "/open",           new OpenDocGetAction(DemoStudio.INSTANCE));
    }
}
