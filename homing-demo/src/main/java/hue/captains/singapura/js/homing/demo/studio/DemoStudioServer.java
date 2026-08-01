package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksStudio;
import hue.captains.singapura.js.homing.demo.studio.multi.MultiStudio;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.HttpsRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.RuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.TlsPreflight;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.tao.http.config.TlsValidationException;

import java.time.Instant;
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
 * <h2>Transport modes</h2>
 * <pre>
 *   DemoStudioServer                             plain HTTP  on 8082 (default)
 *   DemoStudioServer pkcs12 [keystore] [pass]    HTTPS       on 8443, PKCS#12 keystore
 *   DemoStudioServer jks    [keystore] [pass]    HTTPS       on 8443, legacy JKS keystore
 * </pre>
 *
 * <p>The keystore path and password default to {@code homing-demo/certs/dev-keystore.p12}
 * (or {@code .jks}) and {@code changeit}. Generate a self-signed dev keystore with:</p>
 * <pre>
 *   keytool -genkeypair -alias dev -keyalg RSA -keysize 2048 -validity 365 \
 *     -dname "CN=localhost" -ext "SAN=DNS:localhost,IP:127.0.0.1" \
 *     -storetype PKCS12 -keystore homing-demo/certs/dev-keystore.p12 \
 *     -storepass changeit -keypass changeit
 * </pre>
 *
 * <p>In HTTPS mode the keystore is validated up front via {@link TlsPreflight} — a bad
 * path, wrong password, or expired certificate is reported plainly instead of surfacing
 * as a transport error when the socket binds.</p>
 */
public final class DemoStudioServer {

    private static final int HTTP_PORT = 8082;
    private static final int HTTPS_PORT = 8443;
    private static final String DEFAULT_PASSWORD = "changeit";
    private static final String CERTS_DIR = "homing-demo/certs/";

    private DemoStudioServer() {}

    public static void main(String[] args) {
        RuntimeParams params;
        try {
            params = runtimeParams(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(2);
            return;
        }

        if (!preflight(params)) {
            System.exit(1);
            return;
        }

        Umbrella<Studio<?>> umbrella = new Umbrella.Group<>(
                "Homing Multi-Studio Demo",
                "Source studios composed onto one server, launched from a typed umbrella.",
                List.of(
                        new Umbrella.Solo<>(MultiStudio.INSTANCE),
                        new Umbrella.Solo<>(DemoBaseStudio.INSTANCE),
                        // 0.1.0 onward — the building-blocks reference studio
                        // joins the umbrella so a deployed demo surfaces the
                        // framework's primitives reference under the same
                        // chrome as the animal-game content. Sourced from
                        // the homing-blocks Maven module (sibling).
                        new Umbrella.Solo<>(BuildingBlocksStudio.INSTANCE)
                ));

        new Bootstrap<>(new DemoFixtures<>(umbrella), params).start();
    }

    /** Selects the transport from the arguments: no args = plain HTTP. */
    private static RuntimeParams runtimeParams(String[] args) {
        if (args.length == 0) {
            return new DefaultRuntimeParams(HTTP_PORT);
        }
        return switch (args[0].toLowerCase()) {
            case "http" -> new DefaultRuntimeParams(HTTP_PORT);
            case "pkcs12", "p12" -> HttpsRuntimeParams.pkcs12(
                    HTTPS_PORT, keystore(args, ".p12"), password(args));
            case "jks" -> HttpsRuntimeParams.jks(
                    HTTPS_PORT, keystore(args, ".jks"), password(args));
            default -> throw new IllegalArgumentException("Unknown mode: " + args[0]);
        };
    }

    private static String keystore(String[] args, String extension) {
        return args.length > 1 ? args[1] : CERTS_DIR + "dev-keystore" + extension;
    }

    private static String password(String[] args) {
        return args.length > 2 ? args[2] : DEFAULT_PASSWORD;
    }

    /** Validates TLS material before binding; returns false when the demo should not start. */
    private static boolean preflight(RuntimeParams params) {
        try {
            var report = new TlsPreflight().inspect(params);
            if (report.isEmpty()) {
                System.out.println("Transport: plain HTTP on port " + HTTP_PORT);
                return true;
            }
            var tls = report.get();
            System.out.println("Transport: HTTPS on port " + HTTPS_PORT
                    + " (" + tls.storeType() + " keystore)");
            for (var entry : tls.entries()) {
                System.out.println("  alias '" + entry.alias() + "' valid until " + entry.notAfter());
            }
            var expired = tls.expiredAt(Instant.now());
            if (!expired.isEmpty()) {
                System.err.println("  WARNING: expired certificate(s): " + expired);
            }
            return true;
        } catch (TlsValidationException e) {
            System.err.println("TLS preflight failed [" + e.kind() + "]: " + e.getMessage());
            System.err.println("Generate a dev keystore - see the class javadoc for the keytool command.");
            return false;
        }
    }

    private static void printUsage() {
        System.err.println("""
                Usage: DemoStudioServer [mode] [keystore] [password]

                  (no args)                    plain HTTP on 8082
                  http                         plain HTTP on 8082
                  pkcs12 [keystore] [password] HTTPS on 8443 with a PKCS#12 keystore
                  jks    [keystore] [password] HTTPS on 8443 with a legacy JKS keystore

                Keystore defaults to homing-demo/certs/dev-keystore.p12 (or .jks),
                password defaults to 'changeit'.""");
    }
}
