package hue.captains.singapura.js.homing.blocks.splitpane;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.ui.layout.SplitPaneModule;
import hue.captains.singapura.js.homing.studio.base.widget.DocWidget;

import java.util.List;

/**
 * Live demo widget for the SplitPane primitive. Mounts a single
 * {@link SplitPaneModule.SplitPane} into the widget's branch with a
 * <b>fixed</b> 4-quadrant layout. Each quadrant hosts a different
 * hard-coded mini-widget — a colour swatch, a counter button, a
 * monospace log panel, and a static markdown-ish text block — chosen to
 * make divider drags visibly affect each pane's content differently.
 *
 * <p>"Fixed list of widgets" rather than a dynamic registry — this is
 * Block 6 (SplitPane only), and dynamic widget management is RFC 0025's
 * job. The demo's goal is to prove SplitPane works end-to-end inside the
 * framework's chrome and to give a tactile "drag the dividers" exhibit
 * downstream agents can study.</p>
 *
 * @since RFC 0025 — Phase L1 of the Workspace journey
 */
public final class SplitPaneDemoWidget extends DocWidget<SplitPaneDemoWidget.Params,
                                                        SplitPaneDemoWidget> {

    public static final SplitPaneDemoWidget INSTANCE = new SplitPaneDemoWidget();
    private SplitPaneDemoWidget() {}

    /** No URL params — the layout is hard-coded. */
    public record Params() implements Widget._Param {}

    private record mountInto() implements Widget._MountInto<Params, SplitPaneDemoWidget> {}

    @Override public String simpleName() { return "split-pane-demo-widget"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title()      { return "SplitPane Demo"; }

    @Override
    protected Widget._MountInto<Params, SplitPaneDemoWidget> mountInto() {
        return new mountInto();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(
                        List.of(new SplitPaneModule.SplitPane()),
                        SplitPaneModule.INSTANCE)
        );
    }

    @Override
    protected List<String> bodyJs() {
        return List.of(
                "    // Per-mount owner — when this widget swaps, the leak detector",
                "    // catches any forgotten branch.",
                "    var owner = Object.freeze({ toString: function(){",
                "        return 'splitPaneDemo';",
                "    } });",
                "    var b = branch.createBranch('demo');",
                "    b.activate(owner);",
                "",
                "    // Host fills mainHost which fills remaining viewport. The app",
                "    // extends WorkspaceMPA, which sets fullbleedMain() = true — the",
                "    // framework's chrome bootstrap has already neutralised .st-main's",
                "    // max-width + padding and turned it into a flex container. We just",
                "    // create a flex:1 host and let it stretch.",
                "    var host = b.createElement('host', 'div');",
                "    host.style.cssText = 'position:relative;flex:1;width:100%;'",
                "                       + 'min-height:0;background:#fafafa;'",
                "                       + 'box-sizing:border-box;';",
                "    parent.appendChild(host);",
                "",
                "    // ── State ──",
                "    // `sp` is assigned below; split-button handlers capture it by",
                "    // reference so onclick (which fires async) sees the live",
                "    // controller. `nextPaneId` mints unique slotIds for spawned",
                "    // leaves so renderSlot can dispatch on them.",
                "    var sp;",
                "    var nextPaneId = 1;",
                "    var counter    = 0;",
                "",
                "    // ── Split-button overlay ──",
                "    // Real workspaces hand split control to the residing app's chrome",
                "    // (e.g., MultiTabPane's tab strip button group). For this Block 6",
                "    // demo we put the buttons directly on each leaf so the SplitPane",
                "    // primitive can be exercised standalone.",
                "    function decorate(slotId, leafEl, fill) {",
                "        // Wrap the original render in a positioning container so the",
                "        // overlay can be top-right pinned independent of content.",
                "        leafEl.style.position = 'relative';",
                "        var content = document.createElement('div');",
                "        content.style.cssText = 'position:absolute;inset:0;overflow:auto;'",
                "                              + 'box-sizing:border-box;';",
                "        fill(content);",
                "        leafEl.appendChild(content);",
                "",
                "        var overlay = document.createElement('div');",
                "        overlay.style.cssText = 'position:absolute;top:4px;right:4px;'",
                "                              + 'display:flex;gap:2px;z-index:5;'",
                "                              + 'background:rgba(255,255,255,0.85);'",
                "                              + 'border:1px solid rgba(0,0,0,0.15);'",
                "                              + 'border-radius:3px;padding:1px;';",
                "        overlay.appendChild(makeSplitBtn(slotId, 'horizontal', '⇆', 'split horizontally'));",
                "        overlay.appendChild(makeSplitBtn(slotId, 'vertical',   '⇅', 'split vertically'));",
                "        leafEl.appendChild(overlay);",
                "    }",
                "",
                "    function makeSplitBtn(slotId, orientation, glyph, title) {",
                "        var btn = document.createElement('button');",
                "        btn.textContent = glyph;",
                "        btn.title = title;",
                "        btn.style.cssText = 'border:0;background:transparent;cursor:pointer;'",
                "                          + 'padding:2px 6px;font:14px sans-serif;line-height:1;';",
                "        btn.addEventListener('click', function(ev){",
                "            ev.stopPropagation();",
                "            var newId = 'spawn-' + (nextPaneId++);",
                "            sp.split(slotId, orientation, newId);",
                "        });",
                "        btn.addEventListener('mouseenter', function(){ btn.style.background = 'rgba(0,0,0,0.08)'; });",
                "        btn.addEventListener('mouseleave', function(){ btn.style.background = 'transparent'; });",
                "        return btn;",
                "    }",
                "",
                "    // ── Initial fixed-list mini-widget renderers ──",
                "    function fillSwatch(color, label) {",
                "        return function(el) {",
                "            el.style.cssText += 'background:' + color + ';color:#fff;'",
                "                              + 'display:flex;align-items:center;justify-content:center;'",
                "                              + 'font:600 14px sans-serif;';",
                "            el.textContent = label;",
                "        };",
                "    }",
                "    function fillCounter(el) {",
                "        el.style.cssText += 'background:#fff;display:flex;flex-direction:column;'",
                "                          + 'align-items:center;justify-content:center;gap:12px;'",
                "                          + 'font-family:sans-serif;';",
                "        var n = document.createElement('div');",
                "        n.style.cssText = 'font:700 36px monospace;color:#333;';",
                "        n.textContent = String(counter);",
                "        var btn = document.createElement('button');",
                "        btn.textContent = 'click +1';",
                "        btn.style.cssText = 'padding:6px 14px;cursor:pointer;';",
                "        btn.addEventListener('click', function(){",
                "            counter++; n.textContent = String(counter);",
                "        });",
                "        el.appendChild(n); el.appendChild(btn);",
                "    }",
                "    function fillLog(el) {",
                "        el.style.cssText += 'background:#1e1e1e;color:#9cdcfe;'",
                "                          + 'font:12px/1.5 monospace;padding:10px;';",
                "        el.textContent = [",
                "            '[ok] SplitPane mounted',",
                "            '[ok] 4 leaves: swatch, counter, log, prose',",
                "            '[ok] dividers + split buttons wired',",
                "            '',",
                "            '// drag dividers to resize',",
                "            '// click ⇆ / ⇅ on any pane to split it',",
                "            '// onChange fires on every drag step'",
                "        ].join('\\n');",
                "    }",
                "    function fillProse(el) {",
                "        el.style.cssText += 'background:#fff;padding:14px;font:14px/1.5 sans-serif;'",
                "                          + 'color:#333;';",
                "        el.innerHTML = '<b>SplitPane</b> is the layout primitive. The split '",
                "                     + 'API <code>sp.split(slotId, orientation, newId)</code> '",
                "                     + 'replaces a leaf with a 2-child split. Click the '",
                "                     + '⇆ / ⇅ buttons in the corner of any pane.<br><br>'",
                "                     + 'Real apps drive this from their own chrome — a '",
                "                     + 'MultiTabPane will put the buttons on its tab strip.';",
                "    }",
                "    function fillSpawn(el, paneNum) {",
                "        // Placeholder content for dynamically-split panes. Cycles",
                "        // through a small palette so the new pane is visually distinct.",
                "        var palette = ['#e9c46a','#2a9d8f','#e76f51','#264653','#a26ddb','#52b788'];",
                "        var color = palette[(paneNum - 1) % palette.length];",
                "        el.style.cssText += 'background:' + color + ';color:#fff;'",
                "                          + 'display:flex;align-items:center;justify-content:center;'",
                "                          + 'font:600 16px sans-serif;';",
                "        el.textContent = 'Pane ' + paneNum + ' — split me too';",
                "    }",
                "",
                "    var layout = {",
                "        kind: 'split', orientation: 'vertical',",
                "        children: [",
                "            { ratio: 0.5, pane: {",
                "                kind: 'split', orientation: 'horizontal',",
                "                children: [",
                "                    { ratio: 0.45, pane: { kind: 'leaf', slotId: 'swatch' } },",
                "                    { ratio: 0.55, pane: { kind: 'leaf', slotId: 'counter' } }",
                "                ]",
                "            } },",
                "            { ratio: 0.5, pane: {",
                "                kind: 'split', orientation: 'horizontal',",
                "                children: [",
                "                    { ratio: 0.55, pane: { kind: 'leaf', slotId: 'log' } },",
                "                    { ratio: 0.45, pane: { kind: 'leaf', slotId: 'prose' } }",
                "                ]",
                "            } }",
                "        ]",
                "    };",
                "",
                "    sp = new SplitPane({",
                "        container : host,",
                "        layout    : layout,",
                "        renderSlot: function(slotId, el){",
                "            if      (slotId === 'swatch')              decorate(slotId, el, fillSwatch('#5b8def', 'Swatch A'));",
                "            else if (slotId === 'counter')             decorate(slotId, el, fillCounter);",
                "            else if (slotId === 'log')                 decorate(slotId, el, fillLog);",
                "            else if (slotId === 'prose')               decorate(slotId, el, fillProse);",
                "            else if (slotId.indexOf('spawn-') === 0) {",
                "                var n = parseInt(slotId.substring('spawn-'.length), 10);",
                "                decorate(slotId, el, function(c){ fillSpawn(c, n); });",
                "            }",
                "        }",
                "    });"
        );
    }
}
