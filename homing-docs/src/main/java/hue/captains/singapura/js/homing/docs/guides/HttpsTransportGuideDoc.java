package hue.captains.singapura.js.homing.docs.guides;

import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;

import java.util.List;

/**
 * Public guide — how to run the demo server over plain HTTP or TLS.
 *
 * <p>Content only; the catalogue node that mounts it lives with the studio it hangs
 * under ({@code GuidesCatalogue} beneath {@code MultiStudioHome}), so the dependency
 * points one way — demo → homing-docs.</p>
 */
public final class HttpsTransportGuideDoc {

    private HttpsTransportGuideDoc() {}

    public static final RigidDoc INSTANCE = build();

    private static RigidDoc build() {
        return RigidDoc.root(
                    ComposedDoc.deterministicUuid("rigid:https-transport-guide"),
                    "Serving the Demo over HTTPS",
                    "Run the demo as plain HTTP, or over TLS from a PKCS#12 or legacy JKS keystore — "
                            + "with a dev-keystore recipe and the preflight failures explained.",
                    "GUIDE")

            .relation(
                List.of("Mode", "Command", "Listens on"),
                List.of(
                    List.of("Plain HTTP *(default)*", "`DemoStudioServer`",        "`http://localhost:8082`"),
                    List.of("HTTPS — PKCS#12",        "`DemoStudioServer pkcs12`", "`https://localhost:8443`"),
                    List.of("HTTPS — legacy JKS",     "`DemoStudioServer jks`",    "`https://localhost:8443`")),
                "The three transport modes")
            .text("""
                    The demo server chooses its transport from the first argument. With no
                    arguments it serves plain HTTP, so nothing changes for anyone who just wants
                    the studio — TLS is strictly opt-in.

                    Both HTTPS modes read the same kind of material: a keystore holding the
                    server's certificate and private key, plus the password protecting it. They
                    differ only in the *container format* — `pkcs12` is the modern standard,
                    `jks` is Java's legacy proprietary format.""")

            .l1("Running the demo")
                .text("""
                        Run from the repository root, so the default relative keystore paths
                        resolve. Each mode is a single argument.""")
                .code("""
                        # plain HTTP on 8082 — the default
                        java -cp <classpath> ...demo.studio.DemoStudioServer

                        # HTTPS on 8443, PKCS#12 keystore
                        java -cp <classpath> ...demo.studio.DemoStudioServer pkcs12

                        # HTTPS on 8443, legacy JKS keystore
                        java -cp <classpath> ...demo.studio.DemoStudioServer jks""", "bash")
                .text("""
                        The keystore path and password are optional second and third arguments.
                        They default to `homing-demo/certs/dev-keystore.p12` (or `.jks`) and the
                        password `changeit`, so a locally generated dev keystore needs no flags.""")
                .code("""
                        # explicit keystore and password
                        java -cp <classpath> ...demo.studio.DemoStudioServer \\
                            pkcs12 /etc/homing/server.p12 's3cret'""", "bash")
                .text("""
                        Because the demo's certificate is self-signed, a browser will warn and
                        `curl` needs `-k` to accept it. That is expected for local development —
                        a certificate signed by a real authority produces no warning.""")
                .code("curl -k \"https://localhost:8443/\"", "bash")
            .l1build()

            .l1("Generating a dev keystore")
                .text("""
                        Keystores are never committed — they hold a private key, and the demo's
                        `.gitignore` excludes them. Generate one locally with the JDK's `keytool`.

                        The certificate names `localhost` and carries a subject-alternative name
                        for `localhost` and `127.0.0.1`, which is what lets a client validate the
                        hostname it connected to.""")
                .code("""
                        keytool -genkeypair -alias dev -keyalg RSA -keysize 2048 -validity 365 \\
                          -dname "CN=localhost" -ext "SAN=DNS:localhost,IP:127.0.0.1" \\
                          -storetype PKCS12 -keystore homing-demo/certs/dev-keystore.p12 \\
                          -storepass changeit -keypass changeit""", "bash")
                .text("""
                        For the legacy mode, the same command with `-storetype JKS` and a `.jks`
                        filename produces a JKS keystore. `keytool` will warn that JKS is a
                        proprietary format and recommend migrating to PKCS#12 — that warning is
                        the reason `pkcs12` is the preferred mode.""")
                .code("""
                        keytool -genkeypair -alias dev -keyalg RSA -keysize 2048 -validity 365 \\
                          -dname "CN=localhost" -ext "SAN=DNS:localhost,IP:127.0.0.1" \\
                          -storetype JKS -keystore homing-demo/certs/dev-keystore.jks \\
                          -storepass changeit -keypass changeit""", "bash")
            .l1build()

            .l1("What preflight checks before binding")
                .text("""
                        In an HTTPS mode the demo validates the keystore *before* it opens a
                        socket. A misconfigured keystore is reported as a plain message and the
                        process exits, rather than surfacing later as an opaque transport error.

                        On success it prints the store format and every certificate's expiry, so
                        a stale certificate is visible at a glance.""")
                .code("""
                        Transport: HTTPS on port 8443 (PKCS12 keystore)
                          alias 'dev' valid until 2027-07-28T12:45:42Z
                        Studio listening on https port 8443""", "text")
                .text("On failure the message names the category of the problem:")
                .relation(
                    List.of("Reported kind", "Meaning", "Usual fix"),
                    List.of(
                        List.of("`MATERIAL_UNRESOLVABLE`", "the keystore could not be read",
                                "check the path; generate the keystore"),
                        List.of("`WRONG_PASSWORD`", "the password does not open the keystore",
                                "correct the third argument"),
                        List.of("`BAD_FORMAT`", "the bytes are not a keystore of that format",
                                "match the mode to the file — `pkcs12` vs `jks`"),
                        List.of("`UNSUPPORTED`", "the JVM has no provider for the format",
                                "use a standard JDK build")),
                    "Preflight failures and what they mean")
                .code("""
                        TLS preflight failed [WRONG_PASSWORD]: Incorrect keystore password
                        Generate a dev keystore - see the class javadoc for the keytool command.""", "text")
            .l1build()

            .l1("Sourcing the password yourself")
                .text("""
                        A password baked into a launch argument is fine for a local demo and wrong
                        for a real deployment — it lands in shell history and in the process list.
                        The `pkcs12` and `jks` modes are conveniences over a more general form, and
                        any studio server can opt into that form instead.

                        A TLS credential does not hold a keystore or a password. It holds two
                        *provider functions*, called once when the server starts:

                        - a **byte-source provider** yielding the keystore bytes
                        - a **password provider** yielding the secret that opens them

                        Both are single-method interfaces that may throw `IOException`, so a
                        provider is free to read a file, consult the environment, or make a
                        blocking call to a secret manager.""")
                .l2("A provider is just a lambda")
                    .text("""
                            Nothing needs to be registered. Build the credential directly and pass
                            it to the ordinary `HttpsRuntimeParams` constructor — the same one the
                            `pkcs12` factory uses underneath.""")
                    .code("""
                            // keystore from disk, password from the environment
                            var tls = new TlsConfig(new TlsCredential.Pkcs12(
                                    () -> Files.readAllBytes(Path.of("/etc/homing/server.p12")),
                                    () -> System.getenv("HOMING_KEYSTORE_PASSWORD").toCharArray()));

                            new Bootstrap<>(fixtures, new HttpsRuntimeParams(8443, tls)).start();""", "java")
                    .text("""
                            The provider types live in `hue.captains.singapura.tao.http.config` —
                            `ByteSourceProvider` returns `byte[]`, `PasswordProvider` returns
                            `char[]`. A `char[]` is used rather than a `String` so the caller can
                            overwrite the secret once the keystore has been opened.""")
                    .l2build()
                .l2("Reaching a secret manager")
                    .text("""
                            Because a provider may block and may fail, an out-of-process secret
                            source needs no special support — it is the same lambda, calling
                            whatever client you already use.""")
                    .code("""
                            PasswordProvider fromVault = () -> vaultClient.fetch("homing/keystore");

                            var tls = new TlsConfig(new TlsCredential.Pkcs12(
                                    () -> Files.readAllBytes(Path.of("/etc/homing/server.p12")),
                                    fromVault));""", "java")
                    .text("""
                            Studios that carry their own deployment settings can go one step
                            further and implement `RuntimeParams` directly, overriding `tls()` to
                            return the configuration they assemble. The bootstrap only reads that
                            one method, so everything above stays available.""")
                    .l2build()
                .l2("One-shot secrets and preflight")
                    .text("""
                            Preflight validates by *calling* the providers. A source that can only
                            be read once — a single-use token, a console prompt, a queue that pops
                            its value — would therefore be spent before the server starts.

                            Wrap such a source so the first result is remembered and handed out
                            again. Providers are called on the startup path only, so a small cache
                            is enough.""")
                    .code("""
                            static PasswordProvider once(PasswordProvider source) {
                                var cache = new AtomicReference<char[]>();
                                return () -> {
                                    var held = cache.get();
                                    if (held == null) {
                                        held = source.get();
                                        cache.set(held);
                                    }
                                    return held.clone();   // callers may zero their copy
                                };
                            }""", "java")
                    .text("""
                            Sources that can be read repeatedly — a file, an environment variable,
                            most secret-manager clients — need no wrapper.""")
                    .l2build()
            .l1build()

            .l1("PKCS#12 or JKS?")
                .text("""
                        Prefer `pkcs12`. It is an industry-standard container, the JDK's default
                        keystore type since Java 9, and the format that certificate authorities,
                        OpenSSL and Windows certificate stores all speak. A `.p12` or `.pfx` file
                        from any of those loads directly.

                        The `jks` mode exists for one reason: keystores that already exist in
                        Java's older proprietary format. Both modes are equally supported by the
                        server — the choice is about the file you happen to hold, not about
                        capability.""")
            .l1build()

            .build();
    }
}
