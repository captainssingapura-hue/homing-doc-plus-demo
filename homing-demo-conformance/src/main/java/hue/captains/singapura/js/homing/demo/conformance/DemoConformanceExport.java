package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.engine.ServedModuleRenderer;
import hue.captains.singapura.js.homing.conformance.export.ConformanceReportWriter;
import hue.captains.singapura.js.homing.conformance.rules.report.ConformanceRun;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Build-time: the demo's crates graded by the extended policy, the report written under the
 * directory given - this module's classes, where the studio reads it back. The game loop's
 * module is reported under its own type, held to the downstream rule set.
 */
public final class DemoConformanceExport {

    private DemoConformanceExport() {}

    public static void main(String[] args) throws IOException {
        if (args.length != 1) throw new IllegalArgumentException("Usage: DemoConformanceExport <output-directory>");
        Path dir = Path.of(args[0]);
        ConformanceRun run = new ConformanceEngine(DemoConformance.POLICY, new ServedModuleRenderer())
                .assemble(DemoConformance.TOP_LEVEL, DemoConformance.grader());
        new ConformanceReportWriter().write(dir, run);
        System.out.println("[DemoConformanceExport] wrote report to " + dir
                + " (" + run.modules().size() + " modules, "
                + run.summary().errorCount() + " errors, "
                + run.summary().warningCount() + " warnings)");
    }
}
