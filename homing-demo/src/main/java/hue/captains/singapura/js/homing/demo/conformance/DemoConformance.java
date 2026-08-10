package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.ext.GameLoopConformance;
import hue.captains.singapura.js.homing.conformance.rules.Allowance;
import hue.captains.singapura.js.homing.conformance.rules.Baseline;
import hue.captains.singapura.js.homing.conformance.rules.FindingGrader;
import hue.captains.singapura.js.homing.conformance.rules.JsRulePolicy;
import hue.captains.singapura.js.homing.core.Crate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * RFC 0044 — homing-demo's own conformance configuration, in one place so the
 * build-fail gate ({@code DemoConformanceTest}) and the studio's report export
 * ({@link DemoConformanceExport}) grade identically. Mirrors the framework's
 * {@code HomingConformance}, with two downstream differences:
 * <ul>
 *   <li>{@link #POLICY} is the <b>extended</b> policy ({@link
 *       GameLoopConformance#POLICY}) — the framework rules plus the demo's own
 *       {@code game-loop} rule set — so a {@code GAME_LOOP} module is graded by
 *       the downstream's own rules.</li>
 *   <li>{@link #TOP_LEVEL} is the demo's single {@link HomingDemoCrate}; the
 *       report grades only the demo's served modules (the framework crates it
 *       requires are already gated upstream).</li>
 * </ul>
 */
public final class DemoConformance {

    private DemoConformance() {}

    /** The demo's own crate(s) — what the gate and the studio browse + grade. */
    public static final List<Crate> TOP_LEVEL = List.of(HomingDemoCrate.INSTANCE);

    /** The extended policy: framework rules + the demo's {@code game-loop} rule set. */
    public static final JsRulePolicy POLICY = GameLoopConformance.POLICY;

    /** The demo's documented, intentional exceptions (none today). */
    public static final List<Allowance> ALLOWANCES = List.of();

    /** The committed baseline of grandfathered pre-existing demo violations. */
    public static Baseline baseline() {
        try (InputStream in = DemoConformance.class.getResourceAsStream("/demo-conformance-baseline.txt")) {
            if (in == null) return Baseline.EMPTY;
            var lines = new ArrayList<String>();
            try (var r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                for (String line; (line = r.readLine()) != null; ) lines.add(line);
            }
            return Baseline.of(lines);
        } catch (IOException e) {
            throw new UncheckedIOException("failed to load demo conformance baseline", e);
        }
    }

    /** The framework-strict grader plus the demo's allowances + baseline. */
    public static FindingGrader grader(boolean allowPreExisting) {
        return FindingGrader.STRICT
                .withAllowlist(ALLOWANCES)
                .withBaseline(baseline())
                .allowingPreExisting(allowPreExisting);
    }
}
