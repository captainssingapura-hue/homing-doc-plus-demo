# mermaid-mirror-demo

Simulates running a **Mermaid-enabled studio against a self-hosted CDN** instead of the
public one — the scenario for enterprise / air-gapped networks where the browser can't reach
`cdn.jsdelivr.net`.

```
 ┌────────────────────┐  import proxy (same-origin)   ┌──────────────────────┐
 │  Browser           │ ────────────────────────────▶ │  Studio  :8090       │
 │                    │ ◀──────────────────────────── │  (MermaidStudioServer)│
 │                    │        proxy JS               └──────────────────────┘
 │                    │   import mermaid.esm.min.mjs   ┌──────────────────────┐
 │                    │ ────────────────────────────▶ │  Local CDN  :8091     │
 │                    │ ◀──────────────────────────── │  (LocalCdnServer)     │
 └────────────────────┘   JS + CORS headers           └──────────▲───────────┘
                                                                  │ mirrors files
                                                       ┌──────────┴───────────┐
                                                       │  MermaidDownloader    │
                                                       └──────────────────────┘
```

The studio's `main()` overrides the proxy URL at boot so Mermaid is fetched from **your** CDN:

```java
ExternalModuleUrlRegistry.INSTANCE.override(
        MermaidProxyModule.class, "http://localhost:8091/mermaid.esm.min.mjs");
```

Because the studio page (`:8090`) imports from a *different* origin (`:8091`), that fetch is
**cross-origin** — so `LocalCdnServer` sends `Access-Control-Allow-Origin: *` (and a JS MIME
type). That's the CORS control added locally.

## Run order

Run all three from **this module's directory** (`mermaid-mirror-demo/`) so the `./mirror/`
path lines up. Exec via Maven:

```bash
# 0) build once
mvn -o -f pom.xml compile

# 1) mirror Mermaid into ./mirror/  (needs connectivity ONCE, from this machine)
mvn -o -f pom.xml org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.mermaidmirror.MermaidDownloader

# 2) start the local CDN on :8091  (leave running)
mvn -o -f pom.xml org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.mermaidmirror.LocalCdnServer

# 3) start the studio on :8090     (leave running)
mvn -o -f pom.xml org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.mermaidmirror.MermaidStudioServer
```

Then open the doc printed by step 3:

```
http://localhost:8090/app?app=doc-reader&doc=b10c4500-000e-4001-8000-00000000000e
```

If the flowchart renders as a picture, the loop works end to end: **downloader → local CDN →
override → studio**. If it shows a fallback note, the CDN couldn't serve the files — check the
`LocalCdnServer` console for 404s and re-run the downloader, or mirror by hand (below).

## Ports & config

| What | Property | Default |
|---|---|---|
| Studio port | `-Dmermaid.studio.port` | `8090` |
| CDN URL the studio overrides to | `-Dmermaid.cdn.url` | `http://localhost:8091/mermaid.esm.min.mjs` |
| CDN port | `-Dmermaid.cdn.port` | `8091` |
| Mirror dir | `-Dmermaid.mirror.dir` | `mirror` |
| Download source base | `-Dmermaid.base` | `https://cdn.jsdelivr.net/npm/mermaid@11/dist/` |
| Download entry file | `-Dmermaid.entry` | `mermaid.esm.min.mjs` |

## If the downloader doesn't work — mirror by hand

The downloader may fail behind a proxy that blocks the CDN, does TLS interception, or returns
a block page. In that case, populate `./mirror/` manually so it mirrors the CDN's layout:

1. **Get the entry file.** From a machine that *can* reach the CDN:
   ```bash
   curl -L -o mirror/mermaid.esm.min.mjs \
        https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.esm.min.mjs
   ```
2. **Get the chunks it imports.** Open `mirror/mermaid.esm.min.mjs` and note every
   `from "./chunks/…"` and `import("./chunks/…")` path. Download each to the **same relative
   path** under `mirror/` — e.g. an import of `./chunks/mermaid.esm.min/chunk-XXXX.mjs`
   becomes:
   ```bash
   curl -L --create-dirs \
        -o mirror/chunks/mermaid.esm.min/chunk-XXXX.mjs \
        https://cdn.jsdelivr.net/npm/mermaid@11/dist/chunks/mermaid.esm.min/chunk-XXXX.mjs
   ```
   Repeat for chunks those chunks import (they nest). The layout under `mirror/` must match
   the CDN's exactly, because the imports are **relative**.
3. **Easier:** download the whole `dist/` tree with a recursive tool and drop it under
   `mirror/`, e.g.
   ```bash
   wget -r -np -nH --cut-dirs=4 -P mirror \
        https://cdn.jsdelivr.net/npm/mermaid@11/dist/
   ```
   or grab the package tarball (`npm pack mermaid@11`), unpack, and copy its `dist/` into
   `mirror/`.
4. Point `-Dmermaid.entry` at whatever entry filename you placed (default
   `mermaid.esm.min.mjs`) and start `LocalCdnServer`.

Any static file host works as the CDN — this module's `LocalCdnServer` is just the smallest
one that also sets the required CORS + MIME headers.
