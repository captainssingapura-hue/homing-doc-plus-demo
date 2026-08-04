package hue.captains.singapura.js.homing.pdfexport;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitUntilState;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Headless equivalent of the doc-reader's "Export HTML" button: opens a live doc, waits for it
 * to settle (Mermaid rendered), then clicks the export control and captures the downloaded
 * self-contained HTML file. The result is CSS-inlined, script-free, and has Mermaid baked in as
 * SVG — so it renders offline with no server/CDN. Feed it to {@link PdfExporter} {@code --html}
 * to complete the "export HTML → print to PDF" flow.
 *
 * <h2>Usage</h2>
 * <pre>
 *   HtmlExporter --base &lt;url&gt; --doc &lt;uuid&gt; [--out exported.html] [--content-only] [--timeout 30000]
 *   HtmlExporter --url  &lt;doc-reader url&gt;    [--out exported.html] ...
 * </pre>
 */
public final class HtmlExporter {

    private HtmlExporter() {}

    public static void main(String[] args) {
        String base = null, doc = null, url = null;
        Path out = Path.of("exported.html");
        boolean contentOnly = false;
        double timeoutMs = 30_000;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--base"         -> base = args[++i];
                case "--doc"          -> doc = args[++i];
                case "--url"          -> url = args[++i];
                case "--out"          -> out = Path.of(args[++i]);
                case "--timeout"      -> timeoutMs = Double.parseDouble(args[++i]);
                case "--content-only" -> contentOnly = true;
                default -> { System.err.println("Unknown arg: " + args[i]); System.exit(2); }
            }
        }
        if (url == null) {
            if (base == null || doc == null) {
                System.err.println("Usage: HtmlExporter --base <url> --doc <uuid> [--out exported.html] [--content-only]");
                System.exit(2);
            }
            url = base.replaceAll("/+$", "") + "/app?app=doc-reader&doc=" + doc;
        }

        final Path outPath = out;
        System.out.println("Exporting HTML from: " + url);
        System.out.println("                to : " + outPath.toAbsolutePath());

        try (Playwright pw = Playwright.create()) {
            Browser browser = pw.chromium().launch();
            Page page = browser.newPage();

            page.navigate(url, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.NETWORKIDLE).setTimeout(timeoutMs));

            try {
                page.waitForFunction(
                        "() => document.querySelectorAll('code.language-mermaid').length === 0",
                        null,
                        new Page.WaitForFunctionOptions().setTimeout(Math.min(timeoutMs, 15000)));
            } catch (TimeoutError e) {
                System.out.println("  (note: Mermaid did not finish rendering — exporting current state)");
            }
            page.waitForTimeout(300);

            // "content only" is driven through the real UI: untick the export bar's
            // "Include page chrome" checkbox before clicking, exactly as a user would.
            if (contentOnly) {
                page.locator("input[type='checkbox']").first().uncheck();
            }

            // Clicking "Export HTML" builds the file and triggers a Blob download; capture it.
            Download download = page.waitForDownload(() ->
                    page.click("button:has-text('Export HTML')"));
            download.saveAs(outPath);

            browser.close();

            long size = Files.size(outPath);
            System.out.println("Done. Wrote " + size + " bytes → " + outPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("HTML export failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
