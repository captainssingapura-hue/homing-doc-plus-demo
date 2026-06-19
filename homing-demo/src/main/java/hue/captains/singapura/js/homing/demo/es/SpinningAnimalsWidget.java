package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.css.SpinningStyles;
import hue.captains.singapura.js.homing.demo.playground.AnimalsPlaygroundStyles;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * RFC 0024 Widget for the SpinningAnimals game body — Workspace-native
 * shape, no chrome. The existing {@link SpinningAnimals} {@code AppModule}
 * becomes a thin {@code SingleWidgetMPA} shim around this widget so the
 * standalone page (URL {@code /app?app=spinning-animals}) keeps working
 * while the same body becomes pickable from the Animals Playground
 * workspace.
 *
 * <p>Port notes (from the legacy {@code SpinningAnimals.js}):
 * <ul>
 *   <li>The {@code appMain} chrome (brand fetch, Header, breadcrumb) is
 *       gone. The hosting AppModule (or the workspace tab strip) supplies
 *       chrome now.</li>
 *   <li>The {@code buildSpin} body is preserved verbatim: title, hint,
 *       controls (speed slider + reverse button + animal selector), and
 *       the 4×3 cell grid with per-cell pause toggling.</li>
 *   <li>requestAnimationFrame loop runs while the widget element is in
 *       the DOM. No explicit teardown — branch dissolution drops the DOM
 *       references and the next frame call finds no cells.</li>
 * </ul>
 *
 * @since RFC 0025 Ext1b b.2a — first WorkspaceWidget port (POC).
 */
public final class SpinningAnimalsWidget extends WorkspaceWidget<WorkspaceWidget._None, SpinningAnimalsWidget> {

    public static final SpinningAnimalsWidget INSTANCE = new SpinningAnimalsWidget();

    private SpinningAnimalsWidget() {}

    private record construct() implements WorkspaceWidget._Construct<_None, SpinningAnimalsWidget> {}

    @Override protected _Construct<_None, SpinningAnimalsWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Spinning Animals"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(List.of(
                        new AnimalCell.createAnimalCell(),
                        new AnimalCell.createAnimalSelector(),
                        new AnimalCell.createAnimalScope(),
                        // RFC 0028 cycle 4 — reactor entry point used when the
                        // widget joins the workspace-level AnimalsParty.
                        new AnimalCell.setScopeAnimal()),
                        AnimalCell.INSTANCE),
                new ModuleImports<>(List.of(
                        new SpinningStyles.spin_title(),
                        new SpinningStyles.spin_hint(),
                        new SpinningStyles.spin_controls(),
                        new SpinningStyles.spin_grid(),
                        new SpinningStyles.spin_cell(),
                        new SpinningStyles.paused()),
                        SpinningStyles.INSTANCE),
                new ModuleImports<>(List.of(new AnimalsPlaygroundStyles.pg_widget_root()),
                        AnimalsPlaygroundStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> constructBodyJs() {
        // Ported from legacy SpinningAnimals.js / buildSpin(). Chrome bits
        // (Header/brand/breadcrumb) excluded — workspace supplies chrome.
        // All DOM mutations route through branch.createElement so dissolution
        // releases everything cleanly.
        return List.of(
                "    var root = branch.createElement('root', 'div');",
                "    css.setClass(root, pg_widget_root);",
                "    var h1 = branch.createElement('title', 'h1');",
                "    css.setClass(h1, spin_title);",
                "    h1.textContent = 'Spinning Animals';",
                "    root.appendChild(h1);",
                "    var hint = branch.createElement('hint', 'p');",
                "    css.setClass(hint, spin_hint);",
                "    hint.textContent = 'Click an animal to pause/resume it. Use the controls to adjust speed and direction.';",
                "    root.appendChild(hint);",
                "    var controls = branch.createElement('controls', 'div');",
                "    css.setClass(controls, spin_controls);",
                "    var speedLabel = branch.createElement('speedLabel', 'label');",
                "    speedLabel.textContent = 'Speed ';",
                "    var speedSlider = branch.createElement('speedSlider', 'input');",
                "    speedSlider.type = 'range';",
                "    speedSlider.min = '1'; speedSlider.max = '20'; speedSlider.value = '5';",
                "    speedLabel.appendChild(speedSlider);",
                "    var reverseBtn = branch.createElement('reverseBtn', 'button');",
                "    reverseBtn.textContent = 'Reverse';",
                "    var animalScope = createAnimalScope();   // per-instance — no cross-widget bleed",
                "    var selector = createAnimalSelector(animalScope);",
                "    controls.appendChild(speedLabel);",
                "    controls.appendChild(reverseBtn);",
                "    controls.appendChild(selector);",
                "    root.appendChild(controls);",
                "    var grid = branch.createElement('grid', 'div');",
                "    css.setClass(grid, spin_grid);",
                "    root.appendChild(grid);",
                "    var direction = 1, speed = 5;",
                "    var COUNT = 12;",
                "    var cells = [], angles = [], pausedState = [], offsets = [];",
                "    for (var i = 0; i < COUNT; i++) {",
                "        (function(idx) {",
                "            var cell = createAnimalCell(css.className(spin_cell), animalScope);",
                "            grid.appendChild(cell);",
                "            cells.push(cell); angles.push(0); pausedState.push(false);",
                "            offsets.push((Math.random() - 0.5) * 0.6);",
                "            cell.addEventListener('click', function () {",
                "                pausedState[idx] = !pausedState[idx];",
                "                css.toggleClass(cell, paused, pausedState[idx]);",
                "            });",
                "        })(i);",
                "    }",
                "    reverseBtn.addEventListener('click', function () { direction *= -1; });",
                "    speedSlider.addEventListener('input', function (e) { speed = parseInt(e.target.value); });",
                "    var last = 0;",
                "    function frame(ts) {",
                "        var dt = last ? (ts - last) / 1000 : 0;",
                "        last = ts;",
                "        for (var j = 0; j < COUNT; j++) {",
                "            if (pausedState[j]) continue;",
                "            var rate = speed * (1 + offsets[j]);",
                "            angles[j] += direction * rate * 360 * dt;",
                "            cells[j].style.transform = 'rotate(' + angles[j] + 'deg)';",
                "        }",
                "        if (root.isConnected) requestAnimationFrame(frame);",
                "    }",
                "    requestAnimationFrame(frame);",
                "    // RFC 0028 cycle 4 — join the workspace's AnimalsParty if the",
                "    // workspace provided one. The Actor's reactor for AnimalChanged",
                "    // applies the workspace-wide selection to this widget's own",
                "    // animalScope; per-widget per-cell randomness still lives in",
                "    // setScopeAnimal via AnimalCell._refreshCellsIn.",
                "    var actorId = null;",
                "    if (workspaceCtx && workspaceCtx.animalsParty) {",
                "        actorId = 'animals/spinning-' + Math.random().toString(36).slice(2, 8);",
                "        workspaceCtx.animalsParty.joinActor({",
                "            id: actorId,",
                "            parentSecretary: 'animals',",
                "            reactors: {",
                "                AnimalChanged: function (msg) { setScopeAnimal(animalScope, msg.animal); }",
                "            }",
                "        });",
                "        // Late-join sync — ask the Secretary for the current workspace-wide",
                "        // animal. If one is set, the Secretary replies via SendToMember with",
                "        // an AnimalChanged carrying the current value; our reactor above syncs",
                "        // this widget's scope. If still at default ('-1' Random), no response —",
                "        // widget keeps its own initial state.",
                "        workspaceCtx.animalsParty.tellFrom(actorId, { kind: 'CurrentAnimalRequested' });",
                "    }",
                "    // The per-cell click handlers are mouse-only and gated by the",
                "    // overlay when inactive. No document listeners, no audio — setActive",
                "    // is a no-op (still required by contract).",
                "    return {",
                "        root: root,",
                "        setActive: function (active) {},",
                "        partyDeregister: function () {",
                "            if (actorId && workspaceCtx && workspaceCtx.animalsParty) {",
                "                try { workspaceCtx.animalsParty.leave(actorId); } catch (e) {}",
                "            }",
                "        }",
                "    };"
        );
    }
}
