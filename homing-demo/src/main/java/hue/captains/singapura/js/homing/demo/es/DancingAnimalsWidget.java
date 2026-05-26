package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.css.SubwayStyles;
import hue.captains.singapura.js.homing.demo.playground.AnimalsPlaygroundStyles;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * WorkspaceWidget port of the legacy {@link DancingAnimals} game body.
 * Chrome stripped — the workspace tab supplies it; only the dance grid
 * + keyboard handler + jump physics survive.
 *
 * <p>Returns a controller per the b.2d contract:
 * {@code { root, setActive(boolean) }}. {@code setActive(true)}
 * attaches a {@code document.keydown} listener; {@code setActive(false)}
 * detaches it. Only the workspace-active instance reacts to arrow keys —
 * three open Dancing widgets in different panes coexist without
 * keyboard collision. Mouse interactions on the cells are gated by the
 * framework's overlay automatically (no widget code needed).</p>
 *
 * @since RFC 0025 Ext1b b.2b — second WorkspaceWidget port
 *        (controller contract added b.2d).
 */
public final class DancingAnimalsWidget extends WorkspaceWidget<WorkspaceWidget._None, DancingAnimalsWidget> {

    public static final DancingAnimalsWidget INSTANCE = new DancingAnimalsWidget();

    private DancingAnimalsWidget() {}

    private record construct() implements WorkspaceWidget._Construct<_None, DancingAnimalsWidget> {}

    @Override protected _Construct<_None, DancingAnimalsWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Dancing Animals"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(List.of(
                        new AnimalCell.createAnimalCell(),
                        new AnimalCell.createAnimalSelector(),
                        new AnimalCell.createAnimalScope(),
                        new AnimalCell.setScopeAnimal()),
                        AnimalCell.INSTANCE),
                new ModuleImports<>(List.of(new JumpPhysics.createJumpPhysics()),
                        JumpPhysics.INSTANCE),
                new ModuleImports<>(List.of(
                        new SubwayStyles.subway_title(),
                        new SubwayStyles.subway_hint(),
                        new SubwayStyles.subway_grid(),
                        new SubwayStyles.subway_cell()),
                        SubwayStyles.INSTANCE),
                new ModuleImports<>(List.of(new AnimalsPlaygroundStyles.pg_widget_root()),
                        AnimalsPlaygroundStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> constructBodyJs() {
        // Ported from legacy DancingAnimals.js / buildDance(). Chrome bits
        // gone. document.createElement → branch.createElement; root.isConnected
        // guards the rAF loop so dissolved branches stop animating cleanly.
        return List.of(
                "    var root = branch.createElement('root', 'div');",
                "    css.setClass(root, pg_widget_root);",
                "    var h1 = branch.createElement('title', 'h1');",
                "    css.setClass(h1, subway_title);",
                "    h1.textContent = 'Dancing Animals';",
                "    root.appendChild(h1);",
                "    var hint = branch.createElement('hint', 'p');",
                "    css.setClass(hint, subway_hint);",
                "    hint.textContent = 'Press ← or → arrow keys to make them dance! Space to jump.';",
                "    root.appendChild(hint);",
                "    var animalScope = createAnimalScope();   // per-instance — no cross-widget bleed",
                "    var selector = createAnimalSelector(animalScope);",
                "    root.appendChild(selector);",
                "    var grid = branch.createElement('grid', 'div');",
                "    css.setClass(grid, subway_grid);",
                "    root.appendChild(grid);",
                "    var GRAVITY = 1.8, JUMP_STRENGTH = 8;",
                "    var cells = [], reversed = [], physicsArr = [], offsets = [];",
                "    for (var i = 0; i < 25; i++) {",
                "        var cell = createAnimalCell(css.className(subway_cell), animalScope);",
                "        grid.appendChild(cell);",
                "        cells.push(cell);",
                "        reversed.push(Math.random() < 0.5);",
                "        physicsArr.push(createJumpPhysics(GRAVITY, JUMP_STRENGTH));",
                "        offsets.push(0);",
                "    }",
                "    var keyHandler = function (e) {",
                "        if (e.key === 'ArrowLeft') {",
                "            for (var j = 0; j < cells.length; j++) cells[j].style.transform = reversed[j] ? 'scaleX(1)' : 'scaleX(-1)';",
                "        } else if (e.key === 'ArrowRight') {",
                "            for (var k = 0; k < cells.length; k++) cells[k].style.transform = reversed[k] ? 'scaleX(-1)' : 'scaleX(1)';",
                "        } else if (e.key === ' ') {",
                "            e.preventDefault();",
                "            for (var m = 0; m < physicsArr.length; m++) physicsArr[m].jump();",
                "        }",
                "    };",
                "    function frame() {",
                "        for (var n = 0; n < cells.length; n++) {",
                "            offsets[n] = physicsArr[n].update(offsets[n], 0);",
                "            cells[n].style.marginTop = offsets[n] + 'px';",
                "        }",
                "        if (root.isConnected) requestAnimationFrame(frame);",
                "    }",
                "    requestAnimationFrame(frame);",
                "    // RFC 0028 cycle 4 — join AnimalsParty if the workspace provided one.",
                "    var actorId = null;",
                "    if (workspaceCtx && workspaceCtx.animalsParty) {",
                "        actorId = 'animals/dancing-' + Math.random().toString(36).slice(2, 8);",
                "        workspaceCtx.animalsParty.joinActor({",
                "            id: actorId,",
                "            parentSecretary: 'animals',",
                "            reactors: {",
                "                AnimalChanged: function (msg) { setScopeAnimal(animalScope, msg.animal); }",
                "            }",
                "        });",
                "        // Late-join sync — see SpinningAnimalsWidget for the full comment.",
                "        workspaceCtx.animalsParty.tellFrom(actorId, { kind: 'CurrentAnimalRequested' });",
                "    }",
                "    // setActive(true/false) toggles the document keydown listener so this",
                "    // instance only reacts when it's the workspace-active tab. Mouse is",
                "    // gated by the framework overlay (handled outside).",
                "    return {",
                "        root: root,",
                "        setActive: function (active) {",
                "            if (active) document.addEventListener('keydown', keyHandler);",
                "            else        document.removeEventListener('keydown', keyHandler);",
                "        },",
                "        partyDeregister: function () {",
                "            if (actorId && workspaceCtx && workspaceCtx.animalsParty) {",
                "                try { workspaceCtx.animalsParty.leave(actorId); } catch (e) {}",
                "            }",
                "        }",
                "    };"
        );
    }
}
