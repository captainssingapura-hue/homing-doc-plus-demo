package hue.captains.singapura.js.homing.mermaidmirror;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Port 2 — a minimal "manual CDN": a static file server over {@code ./mirror/} that adds the
 * headers a <em>cross-origin</em> ES-module import requires. The studio page (port 8090)
 * imports {@code http://localhost:8091/mermaid.esm.min.mjs}, which is cross-origin, so the
 * browser enforces CORS on the module fetch — hence {@code Access-Control-Allow-Origin: *}
 * here. Also serves a JavaScript MIME type (module scripts are MIME-checked strictly) and
 * preserves the relative directory layout so Mermaid's {@code ./chunks/…} imports resolve.
 *
 * <p>Zero framework/3rd-party deps — just the JDK's {@code com.sun.net.httpserver}. Populate
 * {@code ./mirror/} first with {@link MermaidDownloader} (or by hand — see README).</p>
 *
 * <pre>{@code
 * mvn -o -f mermaid-mirror-demo/pom.xml \
 *     org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
 *     -Dexec.mainClass=hue.captains.singapura.js.homing.mermaidmirror.LocalCdnServer
 * }</pre>
 *
 * <p>Config: {@code -Dmermaid.cdn.port} (default 8091), {@code -Dmermaid.mirror.dir}
 * (default {@code mirror}).</p>
 */
public final class LocalCdnServer {

    private LocalCdnServer() {}

    public static void main(String[] args) throws IOException {
        int port = Integer.getInteger("mermaid.cdn.port", 8091);
        Path root = Path.of(System.getProperty("mermaid.mirror.dir", "mirror")).toAbsolutePath().normalize();

        if (!Files.isDirectory(root)) {
            System.err.println("Mirror directory not found: " + root);
            System.err.println("Run MermaidDownloader first (or create it by hand — see README).");
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> handle(exchange, root));
        server.setExecutor(null);
        server.start();
        System.out.println("Local CDN serving " + root + " on http://localhost:" + port);
    }

    private static void handle(HttpExchange ex, Path root) throws IOException {
        // CORS for every response — the module fetch is cross-origin from the studio page.
        var h = ex.getResponseHeaders();
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Methods", "GET, OPTIONS");
        h.set("Cross-Origin-Resource-Policy", "cross-origin");

        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            ex.close();
            return;
        }

        String rel = ex.getRequestURI().getPath();
        if (rel.startsWith("/")) rel = rel.substring(1);
        Path file = root.resolve(rel).normalize();

        // Path-traversal guard: never serve outside the mirror root.
        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            byte[] body = ("Not found: /" + rel).getBytes();
            ex.sendResponseHeaders(404, body.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(body); }
            return;
        }

        h.set("Content-Type", contentType(file.getFileName().toString()));
        byte[] bytes = Files.readAllBytes(file);
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private static String contentType(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".mjs") || n.endsWith(".js")) return "text/javascript; charset=utf-8";
        if (n.endsWith(".json")) return "application/json; charset=utf-8";
        if (n.endsWith(".css"))  return "text/css; charset=utf-8";
        if (n.endsWith(".map"))  return "application/json; charset=utf-8";
        return "application/octet-stream";
    }
}
