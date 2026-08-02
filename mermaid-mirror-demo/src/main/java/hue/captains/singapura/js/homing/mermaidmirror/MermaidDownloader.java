package hue.captains.singapura.js.homing.mermaidmirror;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mirrors Mermaid — the entry module <em>and</em> its transitively-imported chunks — from a
 * CDN into {@code ./mirror/}, preserving the relative layout so {@link LocalCdnServer} can
 * serve it and the browser's {@code ./chunks/…} imports resolve.
 *
 * <p>Why not one file: Mermaid's ESM code-splits — the entry statically imports
 * {@code ./chunks/*.mjs} and lazily {@code import()}s diagram types. This util follows both
 * kinds of <b>relative</b> import, breadth-first, until the closure is copied. Absolute
 * ({@code https://…}) imports are reported and skipped — if any appear, the mirror is
 * incomplete and you'll see them logged (jsDelivr's build uses only relative chunks).</p>
 *
 * <pre>{@code
 * mvn -o -f mermaid-mirror-demo/pom.xml \
 *     org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
 *     -Dexec.mainClass=hue.captains.singapura.js.homing.mermaidmirror.MermaidDownloader
 * }</pre>
 *
 * <p>Config: {@code -Dmermaid.base} (default {@code https://cdn.jsdelivr.net/npm/mermaid@11/dist/}),
 * {@code -Dmermaid.entry} (default {@code mermaid.esm.min.mjs}),
 * {@code -Dmermaid.mirror.dir} (default {@code mirror}).</p>
 *
 * <p>If this fails (no connectivity, TLS interception, …), download the files by hand — see
 * the module README.</p>
 */
public final class MermaidDownloader {

    private MermaidDownloader() {}

    // Relative import specifiers: `from "./x"`, `import "./x"`, and dynamic `import("./x")`.
    private static final Pattern STATIC_IMPORT =
            Pattern.compile("(?:from|import)\\s*[\"'](\\.[^\"']+)[\"']");
    private static final Pattern DYNAMIC_IMPORT =
            Pattern.compile("import\\s*\\(\\s*[\"'](\\.[^\"']+)[\"']");

    private static final int MAX_FILES = 500; // runaway guard

    public static void main(String[] args) throws IOException, InterruptedException {
        String base = System.getProperty("mermaid.base",
                "https://cdn.jsdelivr.net/npm/mermaid@11/dist/");
        if (!base.endsWith("/")) base = base + "/";
        String entry = System.getProperty("mermaid.entry", "mermaid.esm.min.mjs");
        Path outDir = Path.of(System.getProperty("mermaid.mirror.dir", "mirror")).toAbsolutePath().normalize();

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        Files.createDirectories(outDir);
        System.out.println("Mirroring from " + base + entry + "\n            into " + outDir);

        Deque<String> queue = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        queue.add(entry);
        seen.add(entry);

        int files = 0;
        long bytes = 0;
        int skippedAbsolute = 0;

        while (!queue.isEmpty()) {
            if (files >= MAX_FILES) {
                System.err.println("Reached MAX_FILES=" + MAX_FILES + " — stopping (mirror may be incomplete).");
                break;
            }
            String rel = queue.poll();
            URI url = URI.create(base).resolve(rel);

            HttpResponse<byte[]> resp = client.send(
                    HttpRequest.newBuilder(url).GET().timeout(Duration.ofSeconds(30)).build(),
                    HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() != 200) {
                System.err.println("  ! " + resp.statusCode() + "  " + rel);
                continue;
            }

            byte[] data = resp.body();
            Path dest = outDir.resolve(rel).normalize();
            if (!dest.startsWith(outDir)) { // safety — refuse to escape the mirror dir
                System.err.println("  ! refusing to write outside mirror: " + rel);
                continue;
            }
            Files.createDirectories(dest.getParent());
            Files.write(dest, data);
            files++;
            bytes += data.length;
            System.out.println("  + " + rel + "  (" + data.length + " bytes)");

            // Discover transitive relative imports and enqueue them.
            String content = new String(data, StandardCharsets.UTF_8);
            skippedAbsolute += enqueueImports(STATIC_IMPORT, content, rel, queue, seen);
            skippedAbsolute += enqueueImports(DYNAMIC_IMPORT, content, rel, queue, seen);
        }

        System.out.println("\nDone. " + files + " files, " + bytes + " bytes.");
        if (skippedAbsolute > 0) {
            System.out.println("Note: " + skippedAbsolute + " absolute (https://…) import(s) were "
                    + "skipped — if diagrams fail to render, those hosts must also be reachable "
                    + "or mirrored. jsDelivr's default build uses only relative chunks.");
        }
        System.out.println("Now start LocalCdnServer to serve " + outDir + ".");
    }

    /** Enqueue relative import targets found by {@code p}; return count of absolute ones skipped. */
    private static int enqueueImports(Pattern p, String content, String currentRel,
                                      Deque<String> queue, Set<String> seen) {
        int skippedAbsolute = 0;
        Matcher m = p.matcher(content);
        while (m.find()) {
            String spec = m.group(1);
            if (!spec.startsWith(".")) { skippedAbsolute++; continue; } // absolute — not mirrored
            // Resolve `spec` relative to the current file's location (dummy host normalises ../).
            String resolved = URI.create("http://x/" + currentRel).resolve(spec).getPath();
            if (resolved.startsWith("/")) resolved = resolved.substring(1);
            if (seen.add(resolved)) queue.add(resolved);
        }
        return skippedAbsolute;
    }
}
