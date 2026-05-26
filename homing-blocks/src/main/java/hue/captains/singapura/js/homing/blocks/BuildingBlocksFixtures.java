package hue.captains.singapura.js.homing.blocks;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.Fixtures;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.tao.ontology.ValueObject;

import java.util.List;
import java.util.Objects;

/**
 * Plain delegation to {@link DefaultFixtures}. Building Blocks has no
 * custom harness apps or trees; the framework's defaults are everything
 * it needs.
 */
public record BuildingBlocksFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {

    public BuildingBlocksFixtures {
        Objects.requireNonNull(umbrella);
    }

    @Override public List<AppModule<?, ?>> harnessApps() {
        return new DefaultFixtures<>(umbrella).harnessApps();
    }

    @Override public NodeChrome chromeFor(Umbrella<S> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);
    }
}
