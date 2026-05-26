package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.ui.layout.MultiTabPaneDragModule;
import hue.captains.singapura.js.homing.studio.base.ui.layout.MultiTabPaneModule;
import hue.captains.singapura.js.homing.studio.base.ui.layout.SplitPaneModule;
import hue.captains.singapura.js.homing.studio.base.widget.DocWidget;
import hue.captains.singapura.js.homing.workspace.WidgetPickerModule;
import hue.captains.singapura.js.homing.workspace.WorkspaceLayoutJson;
import hue.captains.singapura.js.homing.workspace.WorkspaceLayoutModule;
import hue.captains.singapura.js.homing.workspace.party.PartyModule;

import java.util.List;

/**
 * Minimal chrome widget for {@link AnimalsPlayground}. Per the b.2c/b.2d
 * direction:
 *
 * <ul>
 *   <li><b>Per-pane "+"</b> — each tab strip carries its own "+" when the
 *       pane has capacity; click opens an empty tab and mounts the picker
 *       inside.</li>
 *   <li><b>Workspace-active tab</b> — at most one tab is workspace-active
 *       across the whole workspace. MultiTabPane renders an invisible
 *       mouse-event overlay on every non-active pane-active tab; click
 *       activates. The active tab's widget gets {@code setActive(true)};
 *       the previous active gets {@code setActive(false)}.</li>
 *   <li><b>Mutate-in-place tab swap</b> — when the picker resolves, the
 *       same tab is mutated (title, render, setActive, onClose) and
 *       re-rendered. The tab id doesn't change, so workspace-active stays
 *       put — "control never left the tab" per the b.2d spec.</li>
 * </ul>
 *
 * @since RFC 0025 Ext1b — b.2d workspace-active widget gating.
 */
public final class AnimalsPlaygroundChrome extends DocWidget<Widget._None, AnimalsPlaygroundChrome> {

    public static final AnimalsPlaygroundChrome INSTANCE = new AnimalsPlaygroundChrome();

    private AnimalsPlaygroundChrome() {}

    private record mountInto() implements Widget._MountInto<Widget._None, AnimalsPlaygroundChrome> {}

    @Override public String simpleName() { return "animals-playground-chrome"; }
    @Override public Class<Widget._None> paramsType() { return Widget._None.class; }
    @Override public String title() { return "Animals Playground"; }

    @Override
    protected Widget._MountInto<Widget._None, AnimalsPlaygroundChrome> mountInto() {
        return new mountInto();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(List.of(new SplitPaneModule.SplitPane()),       SplitPaneModule.INSTANCE),
                new ModuleImports<>(List.of(new MultiTabPaneModule.MultiTabPane()), MultiTabPaneModule.INSTANCE),
                new ModuleImports<>(List.of(new MultiTabPaneDragModule.TabDragController()), MultiTabPaneDragModule.INSTANCE),
                new ModuleImports<>(List.of(new WidgetPickerModule.WidgetPicker()), WidgetPickerModule.INSTANCE),
                new ModuleImports<>(List.of(new WorkspaceLayoutModule.WorkspaceLayout()), WorkspaceLayoutModule.INSTANCE),
                // RFC 0028 cycle 4 — the workspace's first downstream Party.
                // The chrome constructs an AnimalsParty at boot and wires the
                // Ribbon's "Animal" selector into it; widgets join and react.
                new ModuleImports<>(List.of(new PartyModule.Party()), PartyModule.INSTANCE),
                // RFC 0028 cycle 6 phase 1 — AnimalsSecretary as a standalone
                // module so its pure behavior can be unit-tested via the
                // GraalVM harness without dragging the DOM along.
                new ModuleImports<>(List.of(new AnimalsSecretaryModule.AnimalsSecretary()),
                        AnimalsSecretaryModule.INSTANCE),
                new ModuleImports<>(List.of(
                        new AnimalsPlaygroundStyles.pg_host(),
                        new AnimalsPlaygroundStyles.pg_loading(),
                        new AnimalsPlaygroundStyles.pg_pinned_loading()),
                        AnimalsPlaygroundStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> bodyJs() {
        String entriesJson = AnimalsPlayground.INSTANCE.widgetEntriesJson();
        String ribbonJson  = AnimalsPlayground.INSTANCE.ribbonItemsJson();
        String footerJson  = AnimalsPlayground.INSTANCE.footerItemsJson();
        String title       = AnimalsPlayground.INSTANCE.title();
        return List.of(
                "    // ─── Host element (flex:1 + min-height:0).",
                "    var host = branch.createElement('host', 'div');",
                "    css.setClass(host, pg_host);",
                "    parent.appendChild(host);",
                "",
                "    // ─── RFC 0028 cycle 4 — AnimalsParty (the workspace's first",
                "    //     downstream Party). One Secretary at path 'animals' owns the",
                "    //     authoritative selectedAnimal state. Behaviour lives in the",
                "    //     standalone AnimalsSecretary module (RFC 0028 cycle 6 phase 1)",
                "    //     so it is importable in isolation by the GraalVM JS test harness.",
                "    var animalsParty = new Party({",
                "        name: 'animals',",
                "        root: {",
                "            path     : 'animals',",
                "            initial  : AnimalsSecretary.initial,",
                "            behavior : AnimalsSecretary.behavior",
                "        }",
                "    });",
                "    // The ribbon's animal selector Actor — sender-only (no reactor).",
                "    animalsParty.joinActor({",
                "        id: 'animals/ribbon-selector',",
                "        parentSecretary: 'animals',",
                "        reactors: {}",
                "    });",
                "",
                "    // ─── Workspace layout — ribbon (with title + fullscreen toggle) +",
                "    //     content area + optional footer. All structural; chrome reads the",
                "    //     workspace's typed ribbonItems/footerItems and renders.",
                "    var layout = new WorkspaceLayout({",
                "        container   : host,",
                "        title       : " + WorkspaceLayoutJson.quoteString(title) + ",",
                "        ribbonItems : " + ribbonJson + ",",
                "        footerItems : " + footerJson + ",",
                "        onAction    : function (actionId, value) {",
                "            // Workspace-specific ribbon/footer dispatch. Routes the",
                "            // animal-selected action into AnimalsParty; other action ids",
                "            // log and no-op for now.",
                "            if (actionId === 'animal-selected') {",
                "                animalsParty.tellFrom('animals/ribbon-selector',",
                "                    { kind: 'AnimalSelectionRequested', animal: value });",
                "                return;",
                "            }",
                "            console.log('[chrome] ribbon/footer action:', actionId, value);",
                "        }",
                "    });",
                "",
                "    // Workspace-level context passed to every widget's construct. Each",
                "    // widget decides which Parties to join (per the multi-Party Agent",
                "    // pattern from RFC 0028).",
                "    var workspaceCtx = { animalsParty: animalsParty };",
                "",
                "    // ─── Parent branch for per-widget branches.",
                "    var widgetsOwner = Object.freeze({ toString: function(){ return 'animalsPlayground:widgets'; } });",
                "    var widgetsBranch = branch.createBranch('widgets');",
                "    widgetsBranch.activate(widgetsOwner);",
                "",
                "    // ─── Registry (emitted inline).",
                "    var ENTRIES = " + entriesJson + ";",
                "    var pickerEntries = ENTRIES.filter(function (e) { return e.lifecycleHint !== 'PINNED'; });",
                "",
                "    // ─── SINGLETON tracker. simpleName → live tabId.",
                "    var singletonOpen = {};",
                "    var counter = 0;",
                "",
                "    function findTabSlot(tabId) {",
                "        var state = mt.getState();",
                "        for (var slotId in state.tabs) {",
                "            if (!state.tabs.hasOwnProperty(slotId)) continue;",
                "            var arr = state.tabs[slotId].tabs;",
                "            for (var k = 0; k < arr.length; k++) if (arr[k].id === tabId) return slotId;",
                "        }",
                "        return null;",
                "    }",
                "",
                "    /** Find the tab descriptor object by id; null if not found. */",
                "    function findTabObj(tabId) {",
                "        var state = mt.getState();",
                "        for (var slotId in state.tabs) {",
                "            if (!state.tabs.hasOwnProperty(slotId)) continue;",
                "            // getState() returns a copy with shape {id, title} only — we need",
                "            // the live descriptor with render/setActive/onClose. Reach through",
                "            // the internal map.",
                "        }",
                "        var found = null;",
                "        mt._tabsBySlot.forEach(function (s) {",
                "            if (found) return;",
                "            for (var i = 0; i < s.tabs.length; i++) {",
                "                if (s.tabs[i].id === tabId) { found = s.tabs[i]; return; }",
                "            }",
                "        });",
                "        return found;",
                "    }",
                "",
                "    /** Validate the widget controller shape returned by construct(). */",
                "    function unwrapWidget(result, simpleName) {",
                "        if (!result || !result.root || typeof result.setActive !== 'function') {",
                "            throw new Error(",
                "                '[chrome] ' + simpleName + '.construct must return ' +",
                "                '{ root: Element, setActive: (boolean) => void } — got: ' + result);",
                "        }",
                "        return result;",
                "    }",
                "",
                "    // ─── Open picker in a brand-new empty tab inside slotId.",
                "    //     b.2i contract: MultiTabPane calls tab.render(contentEl) EXACTLY ONCE",
                "    //     in the tab's lifetime — the contentEl is a persistent per-tab DOM",
                "    //     host that's never detached on tab switch. To swap a tab's UI later",
                "    //     (picker → loading → real widget), capture the contentEl on first",
                "    //     render and mutate its children directly. NO switchTab-to-force-re-render",
                "    //     pattern — that contract is gone.",
                "    function openPickerInNewTab(slotId) {",
                "        var tabId = 'picker:' + (++counter);",
                "        var pickerHost = document.createElement('div');",
                "        var holder = { contentEl: null };   // populated on first render",
                "        var tab = {",
                "            id      : tabId,",
                "            title   : 'New tab',",
                "            render  : function (contentEl) {",
                "                holder.contentEl = contentEl;",
                "                contentEl.appendChild(pickerHost);",
                "            },",
                "            setActive: function (active) {},   // picker UI is mouse-only; overlay gates it",
                "            onClose : function () { /* empty picker tab — nothing to dispose */ }",
                "        };",
                "        try { mt.addTab(slotId, tab); }",
                "        catch (e) { console.error('[chrome] addTab failed:', e); return; }",
                "        mt.switchTab(slotId, tabId);",
                "        // New tab takes workspace-active immediately — user just clicked '+'",
                "        // there, that's the locus of attention.",
                "        mt.setWorkspaceActiveTab(tabId);",
                "",
                "        var disabledIds = {};",
                "        for (var n in singletonOpen) if (singletonOpen.hasOwnProperty(n)) disabledIds[n] = true;",
                "",
                "        var picker = new WidgetPicker({",
                "            entries     : pickerEntries,",
                "            disabledIds : disabledIds,",
                "            onPick      : function (entry, params) {",
                "                if (params === null) {",
                "                    // SINGLETON focus-existing path: focus existing instance,",
                "                    // dispose the empty picker tab.",
                "                    var existingId = singletonOpen[entry.simpleName];",
                "                    if (existingId) {",
                "                        var existingSlot = findTabSlot(existingId);",
                "                        if (existingSlot) {",
                "                            mt.switchTab(existingSlot, existingId);",
                "                            mt.setWorkspaceActiveTab(existingId);",
                "                        }",
                "                    }",
                "                    mt.removeTab(slotId, tabId);",
                "                    return;",
                "                }",
                "                mutatePickerTabIntoWidget(slotId, tabId, entry, params, holder);",
                "            },",
                "            onCancel    : function () { mt.removeTab(slotId, tabId); }",
                "        });",
                "        picker.mountInto(pickerHost);",
                "    }",
                "",
                "    // ─── Mutate the picker tab in place into a widget tab. Same tabId,",
                "    //     so workspace-active stays put — control never left the tab.",
                "    //     b.2i: swap children of holder.contentEl directly. No switchTab",
                "    //     re-render trick — tab.render is one-shot.",
                "    function mutatePickerTabIntoWidget(slotId, tabId, entry, params, holder) {",
                "        var tab = findTabObj(tabId);",
                "        if (!tab) return;   // tab removed between pick and resolve (unlikely)",
                "        var contentEl = holder.contentEl;",
                "        if (!contentEl) return;   // first render hadn't fired yet — shouldn't happen",
                "",
                "        function setOnly(node) {",
                "            while (contentEl.firstChild) contentEl.removeChild(contentEl.firstChild);",
                "            contentEl.appendChild(node);",
                "        }",
                "",
                "        // Phase 1: loading placeholder + retitle. Strip rebuild picks up the",
                "        // new title; content swap happens immediately, no re-render needed.",
                "        var loading = document.createElement('div');",
                "        css.setClass(loading, pg_loading);",
                "        loading.textContent = 'Loading ' + entry.label + '…';",
                "        setOnly(loading);",
                "        tab.title = entry.label;",
                "        // Touch the strip so the title chip refreshes. switchTab on the",
                "        // currently-active tab is a cheap no-op for content (display already",
                "        // block) but redraws the strip chips with the new title.",
                "        mt.switchTab(slotId, tabId);",
                "",
                "        // Phase 2: dynamic import + construct.",
                "        var instanceId = entry.simpleName + ':' + (++counter);",
                "        var wBranch = widgetsBranch.createBranch('w-' + counter);",
                "        var owner = Object.freeze({ toString: (function (n) { return function () { return 'widget:' + n; }; })(instanceId) });",
                "        wBranch.activate(owner);",
                "",
                "        import(entry.moduleUrl).then(function (mod) {",
                "            if (typeof mod.construct !== 'function') {",
                "                loading.textContent = 'Module missing construct(): ' + entry.simpleName;",
                "                return;",
                "            }",
                "            var controller;",
                "            try { controller = unwrapWidget(mod.construct(wBranch, params, workspaceCtx), entry.simpleName); }",
                "            catch (e) {",
                "                loading.textContent = 'Widget construct failed: ' + (e && e.message ? e.message : String(e));",
                "                return;",
                "            }",
                "            // Phase 3: install the real widget. Swap loading→controller.root",
                "            // in place; rewire setActive/onClose. workspace-active id is",
                "            // unchanged so we manually fire setActive(true) on the fresh",
                "            // controller (the old picker stub never registered one).",
                "            setOnly(controller.root);",
                "            tab.setActive = controller.setActive;",
                "            // RFC 0028: forward the widget's partyDeregister hook so",
                "            // MultiTabPane's close path invokes it before onClose.",
                "            if (typeof controller.partyDeregister === 'function') {",
                "                tab.partyDeregister = controller.partyDeregister;",
                "            }",
                "            tab.onClose   = function () {",
                "                try { wBranch.dissolve(); } catch (e) {}",
                "                if (entry.lifecycleHint === 'SINGLETON') delete singletonOpen[entry.simpleName];",
                "            };",
                "            if (entry.lifecycleHint === 'SINGLETON') singletonOpen[entry.simpleName] = tabId;",
                "            if (mt.getWorkspaceActiveTab() === tabId) {",
                "                try { controller.setActive(true); } catch (e) {}",
                "            }",
                "        }).catch(function (err) {",
                "            loading.textContent = 'Module load failed: ' + (err && err.message ? err.message : String(err));",
                "        });",
                "    }",
                "",
                "    // ─── PINNED auto-spawn. Same b.2i pattern: capture contentEl on first",
                "    //     render, swap loading→controller.root via direct DOM mutation.",
                "    function spawnPinned(entry, slotId) {",
                "        var tabId = entry.simpleName + ':pinned';",
                "        var wBranch = widgetsBranch.createBranch('p-' + entry.simpleName);",
                "        var owner = Object.freeze({ toString: (function (n) { return function () { return 'pinned:' + n; }; })(entry.simpleName) });",
                "        wBranch.activate(owner);",
                "        var loading = document.createElement('div');",
                "        css.setClass(loading, pg_pinned_loading);",
                "        loading.textContent = 'Loading ' + entry.label + '…';",
                "        var holder = { contentEl: null };",
                "        var tab = {",
                "            id       : tabId,",
                "            title    : entry.label,",
                "            pinned   : true,",
                "            render   : function (contentEl) {",
                "                holder.contentEl = contentEl;",
                "                contentEl.appendChild(loading);",
                "            },",
                "            setActive: function (active) {},",
                "            onClose  : function () { try { wBranch.dissolve(); } catch (e) {} }",
                "        };",
                "        mt.addTab(slotId, tab);",
                "        import(entry.moduleUrl).then(function (mod) {",
                "            if (typeof mod.construct !== 'function') {",
                "                loading.textContent = 'Module missing construct(): ' + entry.simpleName;",
                "                return;",
                "            }",
                "            try {",
                "                var controller = unwrapWidget(mod.construct(wBranch, entry.defaults || {}, workspaceCtx), entry.simpleName);",
                "                if (holder.contentEl) {",
                "                    while (holder.contentEl.firstChild) holder.contentEl.removeChild(holder.contentEl.firstChild);",
                "                    holder.contentEl.appendChild(controller.root);",
                "                }",
                "                tab.setActive = controller.setActive;",
                "                if (typeof controller.partyDeregister === 'function') {",
                "                    tab.partyDeregister = controller.partyDeregister;",
                "                }",
                "                if (mt.getWorkspaceActiveTab() === tabId) {",
                "                    try { controller.setActive(true); } catch (e) {}",
                "                }",
                "            } catch (e) { loading.textContent = 'Construct failed: ' + (e && e.message ? e.message : String(e)); }",
                "        }).catch(function (err) {",
                "            loading.textContent = 'Module load failed: ' + (err && err.message ? err.message : String(err));",
                "        });",
                "    }",
                "",
                "    // ─── MultiTabPane wired with onAddTab. MUST mount into",
                "    //     layout.contentEl — not `host` — otherwise MultiTabPane",
                "    //     becomes a sibling of the workspace layout root and the",
                "    //     wl_content area sits empty above the tabs, showing as a",
                "    //     wasted-space gap between workspace border and split-pane.",
                "    var mt = new MultiTabPane({",
                "        container : layout.contentEl,",
                "        budget    : 16,",
                "        onAddTab  : openPickerInNewTab",
                "    });",
                "",
                "    // ─── PINNED auto-spawn at boot. First PINNED tab becomes",
                "    //     workspace-active so the user has a focused starting point.",
                "    var firstPinnedId = null;",
                "    for (var p = 0; p < ENTRIES.length; p++) {",
                "        if (ENTRIES[p].lifecycleHint === 'PINNED') {",
                "            spawnPinned(ENTRIES[p], 'tl');",
                "            if (firstPinnedId === null) firstPinnedId = ENTRIES[p].simpleName + ':pinned';",
                "        }",
                "    }",
                "    if (firstPinnedId) mt.setWorkspaceActiveTab(firstPinnedId);"
        );
    }
}
