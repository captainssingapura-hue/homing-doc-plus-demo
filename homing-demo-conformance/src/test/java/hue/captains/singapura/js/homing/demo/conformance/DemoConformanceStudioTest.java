package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerPlayModule;
import hue.captains.singapura.js.homing.site.Path;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The demo's studio: the report the build exported, the game loop in it under its own type; the studio made over the demo's crates, the old stack nowhere. */
class DemoConformanceStudioTest {

    @Test
    void theReportCoversTheDemo_theGameLoopUnderItsOwnType() {
        ConformanceReportSource report = ConformanceStudio.exportedReport(DemoConformanceStudioServer.class);
        for (String fqcn : DemoConformance.ownModules())
            assertTrue(report.module(fqcn).isPresent(), fqcn + ": graded by the demo's build");
        assertEquals(0, report.summary().errorCount(), "the demo passes, with no debt: " + report.summary());
        var loop = report.module(PlatformerPlayModule.class.getCanonicalName()).orElseThrow();
        assertEquals("game-loop", loop.type(), "the play loop is reported as the type the widgets declared");
    }

    @Test
    void theStudioIsMadeOverTheDemosCrates_noOldStudioOnTheClasspath() {
        var studio = ConformanceStudio.of("Homing · demo conformance", DemoConformance.TOP_LEVEL,
                ConformanceStudio.exportedReport(DemoConformanceStudioServer.class));
        assertTrue(studio.site().router().resolve(Path.parse("/conformance")).isPresent());
        assertThrows(ClassNotFoundException.class, () -> Class.forName("hue.captains.singapura.js.homing.studio.base.Bootstrap"),
                "the old studio stack is not on the classpath");
    }
}
