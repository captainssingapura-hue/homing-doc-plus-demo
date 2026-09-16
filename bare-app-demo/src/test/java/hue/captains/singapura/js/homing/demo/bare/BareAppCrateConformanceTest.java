package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.conformance.rules.CrateDependencyRule;
import hue.captains.singapura.js.homing.conformance.rules.OrphanCheck;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0044 — the bare app's crate gate. The second assertion is the proof the
 * module exists for: every JS import of a themed, served app resolves into
 * core-js, server or theme-color. Nothing reaches the studio, because the
 * studio is not on this classpath at all.
 */
class BareAppCrateConformanceTest {

    @Test
    void everyServedModuleIsCrated() {
        assertEquals(List.of(), OrphanCheck.check(BareAppCrate.INSTANCE),
                "every served JS module in this Maven module must be declared in its crate");
    }

    @Test
    void importsRespectCrateBoundaries() {
        assertEquals(List.of(), CrateDependencyRule.check(BareAppCrate.INSTANCE),
                "every JS import must resolve to the importer's own crate or one it directly requires");
    }

    @Test
    void theStudioIsNotOnTheClasspath() {
        assertTrue(absent("hue.captains.singapura.js.homing.studio.base.Bootstrap"), "studio-base leaked in");
        assertTrue(absent("hue.captains.singapura.js.homing.studio.base.css.StudioStyles"), "StudioStyles leaked in");
    }

    private static boolean absent(String className) {
        try { Class.forName(className); return false; } catch (ClassNotFoundException e) { return true; }
    }
}
