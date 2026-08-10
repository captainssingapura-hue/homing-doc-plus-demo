package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.engine.ServedModuleRenderer;
import hue.captains.singapura.js.homing.conformance.export.ConformanceReportWriter;
import hue.captains.singapura.js.homing.conformance.rules.report.ConformanceRun;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * RFC 0044 — build-time entry point for the demo's conformance studio: assemble
 * homing-demo's own conformance report and write it (arg 0) in the studio's D5
 * layout, read back at runtime by {@code ConformanceReportSource}. Mirrors the
 * framework's {@code SelfConformanceExport}, but assembles with the <b>extended
 * policy</b> ({@link DemoConformance#POLICY}) via the policy-taking engine
 * constructor — so the served report grades {@code MovingAnimalGame} against the
 * downstream {@code game-loop} rule set, not the default consumer discipline.
 *
 * <p>Invoked by the Maven {@code exec} plugin at {@code process-classes}, so a
 * report is produced on every build. Exported with {@code allowPreExisting =
 * true}: warn-mode, but every finding keeps its disposition so the studio can
 * tell fresh violations from grandfathered debt.</p>
 */
public final class DemoConformanceExport {

    private DemoConformanceExport() {}

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: DemoConformanceExport <output-directory>");
        }
        Path dir = Paths.get(args[0]);

        ConformanceRun run = new ConformanceEngine(DemoConformance.POLICY, new ServedModuleRenderer())
                .assemble(DemoConformance.TOP_LEVEL, DemoConformance.grader(true));
        new ConformanceReportWriter().write(dir, run);

        System.out.println("[DemoConformanceExport] wrote report to " + dir
                + " (" + run.modules().size() + " modules, "
                + run.summary().errorCount() + " errors, "
                + run.summary().warningCount() + " warnings)");
    }
}
