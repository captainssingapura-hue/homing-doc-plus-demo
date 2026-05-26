package hue.captains.singapura.js.homing.blocks.modal;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.ui.layout.ModalModule;
import hue.captains.singapura.js.homing.studio.base.widget.DocWidget;

import java.util.List;

/**
 * Live demo widget for Modal. Opens three modals side-by-side, each a
 * different use case — settings dialog, floating colour palette, live
 * inspector — to demonstrate that one primitive serves many purposes.
 */
public final class ModalDemoWidget extends DocWidget<ModalDemoWidget.Params, ModalDemoWidget> {

    public static final ModalDemoWidget INSTANCE = new ModalDemoWidget();
    private ModalDemoWidget() {}

    public record Params() implements Widget._Param {}
    private record mountInto() implements Widget._MountInto<Params, ModalDemoWidget> {}

    @Override public String simpleName() { return "modal-demo-widget"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Modal Demo"; }

    @Override
    protected Widget._MountInto<Params, ModalDemoWidget> mountInto() {
        return new mountInto();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(
                        List.of(new ModalModule.Modal()),
                        ModalModule.INSTANCE)
        );
    }

    @Override
    protected List<String> bodyJs() {
        return List.of(
                "    var owner = Object.freeze({ toString: function(){",
                "        return 'modalDemo';",
                "    } });",
                "    var b = branch.createBranch('demo');",
                "    b.activate(owner);",
                "",
                "    // WorkspaceMPA's fullbleedMain() has set display:flex; we want",
                "    // column here so the backdrop sits above the button bar.",
                "    parent.style.flexDirection = 'column';",
                "",
                "    // Main viewport — a backdrop the modals float over.",
                "    var host = b.createElement('host', 'div');",
                "    host.style.cssText = 'position:relative;flex:1;width:100%;'",
                "                       + 'min-height:0;background:#f4f5f7;'",
                "                       + 'background-image:'",
                "                       + '  linear-gradient(rgba(0,0,0,0.04) 1px,transparent 1px),'",
                "                       + '  linear-gradient(90deg,rgba(0,0,0,0.04) 1px,transparent 1px);'",
                "                       + 'background-size:24px 24px;'",
                "                       + 'overflow:hidden;box-sizing:border-box;'",
                "                       + 'padding:24px;color:#444;font-family:sans-serif;';",
                "    parent.appendChild(host);",
                "",
                "    // Backdrop content — explains what's going on; updated by",
                "    // the colour-palette modal as the user picks colours.",
                "    var swatch = document.createElement('div');",
                "    swatch.style.cssText = 'width:160px;height:160px;border-radius:8px;'",
                "                         + 'border:2px solid rgba(0,0,0,0.18);background:#5b8def;'",
                "                         + 'box-shadow:0 4px 12px rgba(0,0,0,0.12);';",
                "    var legend = document.createElement('div');",
                "    legend.style.cssText = 'margin-top:16px;font:14px sans-serif;color:#666;'",
                "                         + 'max-width:520px;line-height:1.5;';",
                "    legend.innerHTML = '<b>Modal — Live Demo.</b> Three modals are open '",
                "                     + 'showcasing different use cases. Each is independent: '",
                "                     + 'no z-index manager, no shared state. Drag the title '",
                "                     + 'bars, drag the corners to resize, close any with the × '",
                "                     + 'button. Re-open via the buttons below.';",
                "    var bar = document.createElement('div');",
                "    bar.style.cssText = 'display:flex;gap:8px;margin-top:16px;';",
                "    host.appendChild(swatch);",
                "    host.appendChild(legend);",
                "    host.appendChild(bar);",
                "",
                "    // ── Use case 1 — Settings dialog ────────────────────────────",
                "    function buildSettingsContent() {",
                "        var box = document.createElement('div');",
                "        box.style.cssText = 'padding:16px;display:flex;flex-direction:column;gap:12px;'",
                "                          + 'font:13px sans-serif;color:#333;';",
                "        box.innerHTML = '<label>Name <input type=\"text\" value=\"Workspace\" style=\"width:100%;padding:4px;\"></label>'",
                "                      + '<label>Theme <select style=\"width:100%;padding:4px;\">'",
                "                      + '<option>Default</option><option>Dark</option><option>Sepia</option></select></label>'",
                "                      + '<label><input type=\"checkbox\" checked> Auto-save on exit</label>'",
                "                      + '<label><input type=\"checkbox\"> Show line numbers</label>'",
                "                      + '<button style=\"padding:6px;\">Save</button>';",
                "        return box;",
                "    }",
                "",
                "    // ── Use case 2 — Floating colour palette ────────────────────",
                "    function buildPaletteContent() {",
                "        var box = document.createElement('div');",
                "        box.style.cssText = 'padding:12px;display:grid;'",
                "                          + 'grid-template-columns:repeat(4,1fr);gap:8px;';",
                "        var palette = ['#5b8def','#e76f51','#2a9d8f','#e9c46a',",
                "                       '#a26ddb','#52b788','#264653','#f4a261'];",
                "        for (var i = 0; i < palette.length; i++) (function(c){",
                "            var sw = document.createElement('div');",
                "            sw.style.cssText = 'aspect-ratio:1;background:' + c + ';'",
                "                             + 'border-radius:4px;cursor:pointer;'",
                "                             + 'border:2px solid rgba(0,0,0,0.1);';",
                "            sw.addEventListener('click', function(){ swatch.style.background = c; });",
                "            box.appendChild(sw);",
                "        })(palette[i]);",
                "        return box;",
                "    }",
                "",
                "    // ── Use case 3 — Live inspector ─────────────────────────────",
                "    function buildInspectorContent() {",
                "        var box = document.createElement('div');",
                "        box.style.cssText = 'padding:12px;font:12px monospace;'",
                "                          + 'background:#1e1e1e;color:#9cdcfe;height:100%;'",
                "                          + 'box-sizing:border-box;';",
                "        var clockLine   = document.createElement('div');",
                "        var moveLine    = document.createElement('div'); moveLine.textContent  = 'last move:   —';",
                "        var resizeLine  = document.createElement('div'); resizeLine.textContent = 'last resize: —';",
                "        var focusLine   = document.createElement('div'); focusLine.textContent  = 'focus count: 0';",
                "        box.appendChild(clockLine);",
                "        box.appendChild(document.createElement('hr'));",
                "        box.appendChild(moveLine);",
                "        box.appendChild(resizeLine);",
                "        box.appendChild(focusLine);",
                "        var tick = setInterval(function(){",
                "            clockLine.textContent = 'now: ' + new Date().toISOString().substring(11, 19);",
                "        }, 250);",
                "        box._tick = tick; // surfaced so destroy can clear",
                "        box._setMove   = function(x, y){ moveLine.textContent   = 'last move:   (' + x.toFixed(0) + ', ' + y.toFixed(0) + ')'; };",
                "        box._setResize = function(w, h){ resizeLine.textContent = 'last resize: ' + w.toFixed(0) + ' x ' + h.toFixed(0); };",
                "        box._bumpFocus = (function(){ var n=0; return function(){ n++; focusLine.textContent = 'focus count: ' + n; }; })();",
                "        return box;",
                "    }",
                "",
                "    // ── Open each modal; track refs so buttons can re-open ──────",
                "    var refs = { settings: null, palette: null, inspector: null };",
                "",
                "    function openSettings() {",
                "        if (refs.settings && refs.settings.isOpen()) return;",
                "        if (refs.settings) refs.settings.destroy();",
                "        refs.settings = new Modal({",
                "            container: document.body, title: 'Settings',",
                "            content: buildSettingsContent(),",
                "            x: 80, y: 240, width: 320, height: 280,",
                "            bounds: host,",
                "            onClose: function(){ refs.settings.destroy(); refs.settings = null; }",
                "        });",
                "    }",
                "    function openPalette() {",
                "        if (refs.palette && refs.palette.isOpen()) return;",
                "        if (refs.palette) refs.palette.destroy();",
                "        refs.palette = new Modal({",
                "            container: document.body, title: 'Colour Palette',",
                "            content: buildPaletteContent(),",
                "            x: 440, y: 240, width: 240, height: 240,",
                "            bounds: host,",
                "            onClose: function(){ refs.palette.destroy(); refs.palette = null; }",
                "        });",
                "    }",
                "    function openInspector() {",
                "        if (refs.inspector && refs.inspector.isOpen()) return;",
                "        if (refs.inspector) refs.inspector.destroy();",
                "        var content = buildInspectorContent();",
                "        refs.inspector = new Modal({",
                "            container: document.body, title: 'Inspector',",
                "            content: content,",
                "            x: 720, y: 240, width: 280, height: 220,",
                "            bounds: host,",
                "            onMove:   function(x, y){ content._setMove(x, y); },",
                "            onResize: function(w, h){ content._setResize(w, h); },",
                "            onFocus:  function(){ content._bumpFocus(); },",
                "            onClose:  function(){ clearInterval(content._tick); refs.inspector.destroy(); refs.inspector = null; }",
                "        });",
                "    }",
                "",
                "    // Toolbar buttons (also serve as 're-open' affordance after close).",
                "    function makeBtn(label, onClick) {",
                "        var btn = document.createElement('button');",
                "        btn.textContent = label;",
                "        btn.style.cssText = 'padding:6px 14px;cursor:pointer;border:1px solid #ccc;'",
                "                          + 'background:#fff;border-radius:4px;font:13px sans-serif;';",
                "        btn.addEventListener('click', onClick);",
                "        return btn;",
                "    }",
                "    bar.appendChild(makeBtn('Open Settings',  openSettings));",
                "    bar.appendChild(makeBtn('Open Palette',   openPalette));",
                "    bar.appendChild(makeBtn('Open Inspector', openInspector));",
                "",
                "    // Open all three on mount.",
                "    openSettings();",
                "    openPalette();",
                "    openInspector();"
        );
    }
}
