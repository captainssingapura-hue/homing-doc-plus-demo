package hue.captains.singapura.js.homing.blocks;

import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;

/**
 * Standalone server for the Building Blocks reference studio. Listens
 * on port 8083 (distinct from the dogfood demo on 8082).
 *
 * <p>Run with:</p>
 * <pre>{@code
 * mvn -pl homing-blocks exec:java \
 *     -Dexec.mainClass="hue.captains.singapura.js.homing.blocks.BuildingBlocksServer"
 * }</pre>
 */
public final class BuildingBlocksServer {

    private BuildingBlocksServer() {}

    public static void main(String[] args) {
        Umbrella<Studio<?>> umbrella =
                new Umbrella.Solo<>(BuildingBlocksStudio.INSTANCE);

        new Bootstrap<>(
                new BuildingBlocksFixtures<>(umbrella),
                new DefaultRuntimeParams(8083)
        ).start();
    }
}
