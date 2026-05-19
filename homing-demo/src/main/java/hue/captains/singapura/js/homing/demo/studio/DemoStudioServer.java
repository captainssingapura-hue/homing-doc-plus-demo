package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.demo.studio.multi.MultiStudio;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;

import java.util.List;

/**
 * RFC 0012 — multi-studio demo server. Composes two typed studios under one
 * umbrella: {@link MultiStudio} (the launcher providing {@code MultiStudioHome}
 * and the category L1s) plus {@link DemoBaseStudio} (the dogfood content —
 * docs + animal-game SPAs). Brand resolution falls through to MultiStudio's
 * standalone brand — the turtle-logoed multi-studio umbrella.
 *
 * <p>The demo deliberately does <i>not</i> compose the framework's own
 * self-documentation studio (which lives in a separate repo) and as of
 * 0.0.111 no longer composes a public {@code SkillsStudio} either — the
 * demo studio is the canonical worked example now. A downstream adapter
 * plugs in its own studios as additional {@code Solo<>} entries in the
 * umbrella group.</p>
 *
 * <p>Listens on port 8082.</p>
 */
public final class DemoStudioServer {

    private DemoStudioServer() {}

    public static void main(String[] args) {

        Umbrella<Studio<?>> umbrella = new Umbrella.Group<>(
                "Homing Multi-Studio Demo",
                "Source studios composed onto one server, launched from a typed umbrella.",
                List.of(
                        new Umbrella.Solo<>(MultiStudio.INSTANCE),
                        new Umbrella.Solo<>(DemoBaseStudio.INSTANCE)
                ));

        new Bootstrap<>(new DemoFixtures<>(umbrella), new DefaultRuntimeParams(8082)).start();
    }
}
