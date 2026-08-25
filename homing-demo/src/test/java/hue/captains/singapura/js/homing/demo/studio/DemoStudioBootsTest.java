package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksStudio;
import hue.captains.singapura.js.homing.demo.studio.multi.MultiStudio;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Boot gate for the demo server's full catalogue closure — the demo-repo
 * counterpart of self-studio's {@code StudioCatalogueConstructsTest}.
 *
 * <p>Every catalogue law {@code CatalogueRegistry} enforces (strict tree,
 * closure completeness, doc reachability, one hosting per source L0, and
 * RFC 0051's at-most-one-position) fires only when the registry is built.
 * Before this test the demo repo never built one outside {@code main()},
 * so a violation introduced here would have survived a full green CI run
 * and only surfaced when someone started the server by hand.</p>
 *
 * <p>{@link Bootstrap#compose()} does all the assembly {@code start()} does
 * up to opening a socket, so this fails at the same moment a real boot
 * would, without needing a port.</p>
 */
class DemoStudioBootsTest {

    @Test
    void fullUmbrellaComposesCleanly() {
        // Mirrors DemoStudioServer.main's umbrella exactly.
        Umbrella<Studio<?>> umbrella = new Umbrella.Group<>(
                "Homing Multi-Studio Demo",
                "Source studios composed onto one server, launched from a typed umbrella.",
                List.of(
                        new Umbrella.Solo<>(MultiStudio.INSTANCE),
                        new Umbrella.Solo<>(DemoBaseStudio.INSTANCE),
                        new Umbrella.Solo<>(BuildingBlocksStudio.INSTANCE)
                ));

        assertDoesNotThrow(() ->
                new Bootstrap<>(new DemoFixtures<>(umbrella), new DefaultRuntimeParams(0)).compose());
    }
}
