package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.studio.ConformanceStudio;
import hue.captains.singapura.js.homing.conformance.studio.ConformanceStudioFixtures;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Umbrella;

/**
 * RFC 0044 — launches the conformance (Crate-)Studio over homing-demo's own
 * {@link HomingDemoCrate}. This is the downstream half of the studio story: the
 * framework ships the reusable studio ({@link ConformanceStudioFixtures} + its
 * widgets, GetActions, and report codec); the demo supplies its own crate list
 * and its build-exported report ({@link DemoConformanceExport}, run at
 * {@code process-classes}), and gets the full studio for free.
 *
 * <p>The report was assembled with the demo's <b>extended</b> policy, so the
 * Conformance pane groups {@code MovingAnimalGame} under the {@code game-loop}
 * type with the downstream {@code raf-game-loop} findings — the RFC 0044
 * policy extension, visible in the UI.</p>
 *
 * <p>Landing: {@code /}. Workspace:
 * {@code /app?app=genericWorkspace&ws_kind=conformance}. Port defaults to
 * {@code 8091} ({@code -Dconformance.port=...}); kept off the demo studio's 8082
 * and the framework studio's 8090 so all three can run side by side.</p>
 */
public final class DemoConformanceStudioServer {

    private DemoConformanceStudioServer() {}

    public static void main(String[] args) {
        int modules = DemoConformance.TOP_LEVEL.stream().mapToInt(c -> c.entries().size()).sum();
        System.out.println("[demo-crate-studio] " + DemoConformance.TOP_LEVEL.size()
                + " top-level crate(s) · " + modules + " served modules");

        var umbrella = new Umbrella.Solo<>(ConformanceStudio.INSTANCE);
        int port = Integer.getInteger("conformance.port", 8091);
        new Bootstrap<>(new ConformanceStudioFixtures(umbrella, DemoConformance.TOP_LEVEL),
                new DefaultRuntimeParams(port)).start();
    }
}
