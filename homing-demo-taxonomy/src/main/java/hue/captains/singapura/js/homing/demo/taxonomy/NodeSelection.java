package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * What a node-selection party carries: the one node of the taxonomy the workbench's widgets are
 * looking at, by its token - {@code ""} for none. A member does - {@link Select}, {@link
 * CurrentRequested}; the party says - {@link Selected}, to every member after every pick and to a
 * member that asks. The widgets know nothing of each other.
 */
public sealed interface NodeSelection {

    /** A member picked a node: this token, or {@code ""} for none. */
    record Select(String id) implements NodeSelection {}

    /** A member asks what is picked - one that joins late - and is answered alone. */
    record CurrentRequested() implements NodeSelection {}

    /** The party says: this is the node picked, at this revision. */
    record Selected(String id, int revision) implements NodeSelection {}

    /** The type: {@code node-selection}, served as {@code NODE_SELECTION}; its secretary {@code NodeSelectionSecretary}. */
    PartyType<NodeSelection> TYPE = new PartyType<>("node-selection", NodeSelection.class)
            .servedFrom(new ModuleImports<>(List.of(new NodeSelectionModule.NODE_SELECTION()), NodeSelectionModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new NodeSelectionSecretaryModule.NodeSelectionSecretary()), NodeSelectionSecretaryModule.INSTANCE));
}
