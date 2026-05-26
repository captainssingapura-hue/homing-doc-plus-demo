package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.studio.base.conformance.PlanRegistrationConformanceTest;
import hue.captains.singapura.js.homing.demo.studio.DemoBaseStudio;
import hue.captains.singapura.js.homing.demo.studio.multi.MultiStudio;
import hue.captains.singapura.js.homing.studio.base.Studio;

import java.util.List;

/**
 * Mirrors {@code DemoStudioServer.main()}'s umbrella and asserts that every
 * Plan reachable as a catalogue leaf (via {@code Entry.of(this, plan)}) is
 * also present in some {@code Studio.plans()} list. After Defect 0005's
 * Plan-arm fix (0.0.111) the catalogue-leaf harvest in {@code Bootstrap}
 * makes the missing-from-{@code Studio.plans()} case structurally
 * inexpressible; this test stays as defense in depth and a watch for the
 * reverse drift direction.
 */
class DemoPlanRegistrationConformanceTest extends PlanRegistrationConformanceTest {

    @Override
    protected List<? extends Studio<?>> studios() {
        return List.of(
                MultiStudio.INSTANCE,
                DemoBaseStudio.INSTANCE
        );
    }
}
