# doc-pdf-export

A standalone CLI that exports **any studio Doc to PDF** by driving a headless Chromium with
[Playwright](https://playwright.dev/java/). Because every Doc renders through the `doc-reader`
app, all the tool needs is the doc's UUID and the studio's base URL.

It navigates the page, **waits for it to settle — including our Mermaid hook swapping
`` ```mermaid `` fences for SVG** — optionally strips the page chrome, then prints to PDF.

## One-time: install Chromium

Playwright downloads its own Chromium (~150 MB) on first use. If it isn't already present:

```bash
mvn -f doc-pdf-export/pom.xml exec:java \
    -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

## Usage

With a studio already running (e.g. the mermaid-mirror-demo on :8090):

```bash
mvn -f doc-pdf-export/pom.xml exec:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.pdfexport.PdfExporter \
    -Dexec.args="--base http://localhost:8090 --doc b10c4500-000e-4001-8000-00000000000e --out mermaid.pdf"
```

Or point it at a full URL:

```bash
... -Dexec.args="--url http://localhost:8083/app?app=doc-reader&doc=<uuid> --out doc.pdf --content-only"
```

## Export the HTML, then print *that* to PDF (fully offline)

The doc-reader's "Export HTML" produces a **self-contained** file — CSS inlined, scripts
stripped, Mermaid baked in as SVG. `HtmlExporter` is the headless version of that button, and
`PdfExporter --html` prints the resulting file with **no server or CDN running**:

```bash
# 1) capture the self-contained HTML (studio must be running)
mvn -f doc-pdf-export/pom.xml exec:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.pdfexport.HtmlExporter \
    -Dexec.args="--base http://localhost:8090 --doc <uuid> --out exported.html"

# 2) print the exported file to PDF — offline, no server needed
mvn -f doc-pdf-export/pom.xml exec:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.pdfexport.PdfExporter \
    -Dexec.args="--html exported.html --out from-export.pdf"
```

Both `HtmlExporter` and the live-page `PdfExporter` accept `--content-only` (drives the export
bar's "Include page chrome" toggle / strips `data-export-*` chrome).

## Options

| Flag | Meaning | Default |
|---|---|---|
| `--base <url>` | studio base, combined with `--doc` into the doc-reader URL | — |
| `--doc <uuid>` | doc UUID | — |
| `--url <url>` | full doc-reader URL (instead of `--base`/`--doc`) | — |
| `--html <file>` | print a local self-contained HTML file (from `HtmlExporter`) instead of a live page | — |
| `--out <path>` | output PDF file | `doc.pdf` |
| `--content-only` | strip brand bar / TOC / export controls (uses the framework's `data-export-*` markers) | off |
| `--format <A4\|Letter>` | page size | `A4` |
| `--timeout <ms>` | navigation + settle budget | `30000` |

## Notes

- **Isolated on purpose.** Playwright pulls a browser binary, so this module never sits on a
  serving classpath — it's an operator/CLI tool. (If an in-app "Export PDF" button is wanted
  later, the same navigate → wait-for-render → `page.pdf()` core can back a server endpoint.)
- **Mermaid & connectivity.** The tool waits until the `` ```mermaid `` fences are replaced by
  SVG. If the diagrams can't load (offline / blocked CDN) it times out and exports the fences
  as-is rather than failing — same graceful degradation as the live page.
