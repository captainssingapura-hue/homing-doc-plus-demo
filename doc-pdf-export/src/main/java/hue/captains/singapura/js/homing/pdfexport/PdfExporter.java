package hue.captains.singapura.js.homing.pdfexport;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.Margin;
import com.microsoft.playwright.options.WaitUntilState;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exports a studio Doc to PDF by driving a headless Chromium with Playwright. Two input modes:
 *
 * <ul>
 *   <li><b>Live</b> — {@code --base <url> --doc <uuid>} (or {@code --url}): open the doc-reader
 *       page, wait for it to settle (including our Mermaid hook swapping {@code ```mermaid}
 *       fences for SVG), then print.</li>
 *   <li><b>Exported HTML</b> — {@code --html <file>}: print a self-contained HTML file produced
 *       by the page's "Export HTML" ({@link HtmlExporter} is the headless way to make one). That
 *       file already has its CSS inlined and Mermaid baked in as SVG, so this path needs
 *       <em>no server and no CDN</em> — pure offline HTML → PDF.</li>
 * </ul>
 *
 * <p>Optionally strips page chrome ({@code data-export-chrome} / {@code data-export-exclude},
 * matching the "content only" HTML export) before printing.</p>
 *
 * <h2>Usage</h2>
 * <pre>
 *   PdfExporter --base &lt;url&gt; --doc &lt;uuid&gt; [options]
 *   PdfExporter --url  &lt;doc-reader url&gt;     [options]
 *   PdfExporter --html &lt;file&gt;              [options]
 *
 *   --out &lt;path&gt;        output file (default: doc.pdf)
 *   --content-only      strip brand bar / TOC / export controls before printing
 *   --format &lt;A4|Letter&gt; page size (default A4)
 *   --timeout &lt;ms&gt;      navigation + settle budget (default 30000)
 * </pre>
 */
public final class PdfExporter {

    private PdfExporter() {}

    public static void main(String[] args) {
        var cfg = Config.parse(args);
        if (cfg == null) { printUsageAndExit(); return; }

        boolean fileMode = cfg.htmlFile != null;
        String target = fileMode
                ? cfg.htmlFile.toAbsolutePath().toUri().toString()   // file:///…
                : cfg.url;

        System.out.println("Exporting: " + target);
        System.out.println("      to : " + cfg.out.toAbsolutePath());

        try (Playwright pw = Playwright.create()) {
            Browser browser = pw.chromium().launch();   // headless by default
            Page page = browser.newPage();

            page.navigate(target, new Page.NavigateOptions()
                    // A live page settles on network-idle; a static export file has no requests,
                    // so wait for load instead.
                    .setWaitUntil(fileMode ? WaitUntilState.LOAD : WaitUntilState.NETWORKIDLE)
                    .setTimeout(cfg.timeoutMs));

            if (!fileMode) {
                // Live page: our doc-reader hook replaces every `code.language-mermaid` fence
                // with an <svg>. When the count reaches 0, diagrams are drawn. No diagrams →
                // satisfied immediately; Mermaid unreachable → time out and print fences as-is.
                try {
                    page.waitForFunction(
                            "() => document.querySelectorAll('code.language-mermaid').length === 0",
                            null,
                            new Page.WaitForFunctionOptions().setTimeout(Math.min(cfg.timeoutMs, 15000)));
                } catch (TimeoutError e) {
                    System.out.println("  (note: Mermaid diagrams did not finish rendering — "
                            + "exporting current state)");
                }
            }
            page.waitForTimeout(300);   // brief settle for fonts/layout

            if (cfg.contentOnly) {
                page.evaluate("() => document.querySelectorAll("
                        + "'[data-export-chrome],[data-export-exclude]').forEach(e => e.remove())");
            }

            page.pdf(new Page.PdfOptions()
                    .setPath(cfg.out)
                    .setFormat(cfg.format)
                    .setPrintBackground(true)
                    .setMargin(new Margin().setTop("14mm").setBottom("14mm")
                            .setLeft("14mm").setRight("14mm")));

            browser.close();

            long size = Files.size(cfg.out);
            System.out.println("Done. Wrote " + size + " bytes → " + cfg.out.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Export failed: " + e.getMessage());
            if (String.valueOf(e.getMessage()).contains("Executable doesn't exist")
                    || String.valueOf(e.getMessage()).contains("Looks like Playwright")) {
                System.err.println("Chromium is not installed. Run once:");
                System.err.println("  mvn -f doc-pdf-export/pom.xml exec:java "
                        + "-Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install chromium\"");
            }
            System.exit(1);
        }
    }

    private static void printUsageAndExit() {
        System.err.println("""
            Usage:
              PdfExporter --base <url> --doc <uuid> [--out f.pdf] [--content-only] [--format A4] [--timeout 30000]
              PdfExporter --url  <doc-reader-url>   [--out f.pdf] [--content-only] ...
              PdfExporter --html <exported.html>    [--out f.pdf] [--content-only] ...
            """);
        System.exit(2);
    }

    /** Parsed CLI configuration. */
    private static final class Config {
        String url;
        Path htmlFile;
        Path out = Path.of("doc.pdf");
        boolean contentOnly = false;
        String format = "A4";
        double timeoutMs = 30_000;

        static Config parse(String[] args) {
            var c = new Config();
            String base = null, doc = null, url = null, html = null;
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--base"         -> base = next(args, ++i);
                    case "--doc"          -> doc = next(args, ++i);
                    case "--url"          -> url = next(args, ++i);
                    case "--html"         -> html = next(args, ++i);
                    case "--out"          -> c.out = Path.of(next(args, ++i));
                    case "--format"       -> c.format = next(args, ++i);
                    case "--timeout"      -> c.timeoutMs = Double.parseDouble(next(args, ++i));
                    case "--content-only" -> c.contentOnly = true;
                    default -> { System.err.println("Unknown arg: " + args[i]); return null; }
                }
            }
            if (html != null) {
                c.htmlFile = Path.of(html);
            } else if (url != null) {
                c.url = url;
            } else if (base != null && doc != null) {
                c.url = base.replaceAll("/+$", "") + "/app?app=doc-reader&doc=" + doc;
            } else {
                return null; // need --html, --url, or --base + --doc
            }
            return c;
        }

        private static String next(String[] args, int i) {
            if (i >= args.length) { System.err.println("Missing value after " + args[i - 1]); System.exit(2); }
            return args[i];
        }
    }
}
