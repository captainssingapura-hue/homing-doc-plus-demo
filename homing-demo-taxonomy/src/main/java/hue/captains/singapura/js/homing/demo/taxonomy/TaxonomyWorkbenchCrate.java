package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.relgrid.RelGridCrate;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolCrate;
import hue.captains.singapura.js.homing.reltree.RelTreeCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.workspace.parties.WorkspacePartiesCrate;
import hue.captains.singapura.js.homing.workspace.site.WorkspaceSiteCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceWidgetsCrate;

import java.util.List;

/**
 * The {@link Crate} for {@code homing-demo-taxonomy}: every served JS module of the taxonomy
 * workbench - the taxonomy as data and its index, the pick its widgets share, the widgets, the
 * page - and every crate it imports from named in {@link #requires()}.
 */
public final class TaxonomyWorkbenchCrate implements Crate {

    public static final TaxonomyWorkbenchCrate INSTANCE = new TaxonomyWorkbenchCrate();

    private TaxonomyWorkbenchCrate() {}

    @Override public String name() { return "homing-demo-taxonomy"; }

    @Override
    public List<Crate> requires() {
        return List.of(
                CoreJsCrate.INSTANCE,
                ServerCrate.INSTANCE,
                DesignCrate.INSTANCE,
                // The tree: a relation tree; the parts: a relation grid; their questions and notices the protocol's.
                RelTreeCrate.INSTANCE,
                RelGridCrate.INSTANCE,
                RelGridProtocolCrate.INSTANCE,
                // The workspace: its widgets' contract, their parties, the grouped page.
                WorkspaceWidgetsCrate.INSTANCE,
                WorkspacePartiesCrate.INSTANCE,
                WorkspaceSiteCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(TaxonomyStyles.INSTANCE),
                // The taxonomy as data, written when served, and the index the widgets ask it through.
                CrateEntry.of(TaxonomyDataModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(TaxonomyIndexModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                // The pick the widgets share: its type and its secretary.
                CrateEntry.of(NodeSelectionModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(NodeSelectionSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                // The widgets.
                CrateEntry.of(TaxonomyWidgetModule.INSTANCE),
                CrateEntry.of(TaxonomyTreeModule.INSTANCE),
                CrateEntry.of(PartsTableModule.INSTANCE),
                CrateEntry.of(NodeDetailsModule.INSTANCE),
                // The page.
                CrateEntry.of(TaxonomyWorkspacesModule.INSTANCE),
                CrateEntry.of(TaxonomyLayoutModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(TaxonomyWorkbenchApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
