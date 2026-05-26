package hue.captains.singapura.js.homing.blocks.multitabpane;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.ui.layout.MultiTabPaneModule;
import hue.captains.singapura.js.homing.studio.base.widget.DocWidget;

import java.util.List;

/**
 * Live demo widget for MultiTabPane. Mounts a single MultiTabPane into
 * the widget's branch with the default 2x2 starter and pre-loaded
 * sample tabs so the budget rule is visible immediately.
 *
 * <p>Each of the four starter slots ({@code tl}, {@code tr}, {@code bl},
 * {@code br}) gets one pre-loaded tab at construction time — bringing
 * each pane to 1/4 capacity, leaving plenty of room to split repeatedly
 * until the workspace reaches its 16-pane ceiling. Add more tabs to any
 * pane to exercise the over-capacity behaviour: when a pane with N tabs
 * is split, the original child keeps all N tabs even if that puts it
 * over the new (halved) capacity; the pill turns red, and addTab refuses
 * further additions until tabs are closed.</p>
 *
 * @since RFC 0025 — L2 of the Workspace journey
 */
public final class MultiTabPaneDemoWidget extends DocWidget<MultiTabPaneDemoWidget.Params,
                                                            MultiTabPaneDemoWidget> {

    public static final MultiTabPaneDemoWidget INSTANCE = new MultiTabPaneDemoWidget();
    private MultiTabPaneDemoWidget() {}

    public record Params() implements Widget._Param {}

    private record mountInto() implements Widget._MountInto<Params, MultiTabPaneDemoWidget> {}

    @Override public String simpleName() { return "multi-tab-pane-demo-widget"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "MultiTabPane Demo"; }

    @Override
    protected Widget._MountInto<Params, MultiTabPaneDemoWidget> mountInto() {
        return new mountInto();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(
                        List.of(new MultiTabPaneModule.MultiTabPane()),
                        MultiTabPaneModule.INSTANCE)
        );
    }

    @Override
    protected List<String> bodyJs() {
        return List.of(
                "    var owner = Object.freeze({ toString: function(){",
                "        return 'multiTabPaneDemo';",
                "    } });",
                "    var b = branch.createBranch('demo');",
                "    b.activate(owner);",
                "",
                "    // WorkspaceMPA's fullbleedMain() has already neutralised .st-main",
                "    // and made it a flex container. Host becomes a flex:1 child.",
                "    var host = b.createElement('host', 'div');",
                "    host.style.cssText = 'position:relative;flex:1;width:100%;'",
                "                       + 'min-height:0;background:#fafafa;'",
                "                       + 'box-sizing:border-box;';",
                "    parent.appendChild(host);",
                "",
                "    // ── Tab content factories ──",
                "    // Each pre-loaded tab is a small demo content type. Their",
                "    // render fns are called fresh on every tab switch / layout",
                "    // change — state inside the function body resets, just like",
                "    // SplitPane. Real widgets atop MultiTabPane preserve state",
                "    // by externalising it from the render closure.",
                "    var palette = ['#5b8def','#e9c46a','#2a9d8f','#e76f51',",
                "                   '#a26ddb','#52b788','#264653','#f4a261'];",
                "    var nextColor = 0;",
                "    function nextSwatchColor() { return palette[(nextColor++) % palette.length]; }",
                "",
                "    function makeSwatch(color, label) {",
                "        return function(el){",
                "            el.style.cssText = 'background:' + color + ';color:#fff;'",
                "                             + 'display:flex;align-items:center;justify-content:center;'",
                "                             + 'font:600 16px sans-serif;height:100%;';",
                "            el.textContent = label;",
                "        };",
                "    }",
                "    function makeNotes(text) {",
                "        return function(el){",
                "            el.style.cssText = 'padding:16px;font:14px/1.5 sans-serif;color:#333;'",
                "                             + 'background:#fff;height:100%;box-sizing:border-box;';",
                "            el.innerHTML = text;",
                "        };",
                "    }",
                "",
                "    var tabSeq = 0;",
                "    function mkTab(title, render) {",
                "        return { id: 't' + (++tabSeq), title: title, render: render };",
                "    }",
                "",
                "    // ── MultiTabPane with default 2x2 starter ──",
                "    var mt = new MultiTabPane({",
                "        container : host,",
                "        budget    : 16",
                "        // no initialLayout → default 2x2 with empty slots tl,tr,bl,br",
                "    });",
                "",
                "    // Pre-load 1 tab per starter slot — each pane at 1/4. From here",
                "    // you can split every pane repeatedly until you reach the 16-pane",
                "    // ceiling. Add more tabs to any pane to exercise overcap-on-split.",
                "    mt.addTab('tl', mkTab('Welcome', makeNotes(",
                "        '<b>MultiTabPane</b> live demo. Total tab budget across the '",
                "      + 'workspace is <b>16</b>. Per-pane capacity = budget / 2<sup>depth</sup>. '",
                "      + 'This 2x2 layout is depth 2 — each pane gets capacity 4.'",
                "      + '<br><br>'",
                "      + '<b>Try:</b> click ⇆ or ⇅ on any pane to split. The original '",
                "      + 'child keeps the existing tab; the new sibling starts empty. '",
                "      + 'Keep splitting to reach the 16-pane ceiling. Click ⤢ on a '",
                "      + 'leaf sibling to merge two panes back into one.')));",
                "    mt.addTab('tr', mkTab('Swatch', makeSwatch(nextSwatchColor(), 'Top-Right')));",
                "    mt.addTab('bl', mkTab('Log', function(el){",
                "        el.style.cssText = 'background:#1e1e1e;color:#9cdcfe;'",
                "                         + 'font:12px/1.5 monospace;padding:10px;height:100%;'",
                "                         + 'overflow:auto;box-sizing:border-box;';",
                "        el.textContent = [",
                "            '[ok] MultiTabPane mounted',",
                "            '[ok] budget = 16, layout = 2x2',",
                "            '[ok] capacity / pane at this depth = 4',",
                "            '',",
                "            '// 1 tab per starter pane (4 of 16 used)',",
                "            '// split: ⇆ ⇅   merge: ⤢   close tab: ×',",
                "            '// pill turns red if a pane is over capacity',",
                "            '// addTab refuses once a pane is at its cap'",
                "        ].join('\\n');",
                "    }));",
                "    mt.addTab('br', mkTab('Counter', function(el){",
                "        el.style.cssText = 'background:#fff;display:flex;flex-direction:column;'",
                "                         + 'align-items:center;justify-content:center;gap:12px;'",
                "                         + 'font-family:sans-serif;height:100%;';",
                "        var n = document.createElement('div');",
                "        n.style.cssText = 'font:700 36px monospace;color:#333;';",
                "        var k = 0; n.textContent = '0';",
                "        var btn = document.createElement('button');",
                "        btn.textContent = 'click +1';",
                "        btn.style.cssText = 'padding:6px 14px;cursor:pointer;';",
                "        btn.addEventListener('click', function(){ k++; n.textContent = String(k); });",
                "        el.appendChild(n); el.appendChild(btn);",
                "        var hint = document.createElement('div');",
                "        hint.style.cssText = 'font:11px sans-serif;color:#888;';",
                "        hint.textContent = '(state resets on tab switch / layout change)';",
                "        el.appendChild(hint);",
                "    }));"
        );
    }
}
