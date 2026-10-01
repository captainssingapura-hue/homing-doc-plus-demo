package hue.captains.singapura.js.homing.demo.workspacewidgets;

import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.demo.workspacewidgets.video.EmbeddedVideoDeclaration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Linked to the workspace on its core alone, and made so rather than merely
 * meant: the studio's old workspace - its widget, its spec, its crates - is not
 * on this module's classpath, so nothing here can reach it; and no crate of it
 * is in this crate's closure. The two stacks share the package
 * {@code hue.captains.singapura.js.homing.workspace}, so it is the old one's own
 * classes that are looked for, never a package.
 */
class NoOldWorkspaceTest {

    /** Classes only the old stack has. */
    static final List<String> OLD = List.of(
            "hue.captains.singapura.js.homing.workspace.WorkspaceWidget",
            "hue.captains.singapura.js.homing.workspace.WorkspaceCrate",
            "hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpec",
            "hue.captains.singapura.js.homing.workspace.persistence.WorkspacePersistenceCrate",
            "hue.captains.singapura.js.homing.studio.workspace.StudioWorkspaceCrate",
            "hue.captains.singapura.js.homing.studio.starter.StudioStarterFixtures");

    @Test
    void theOldWorkspaceIsNotOnTheClasspath() {
        for (String name : OLD) {
            assertThrows(ClassNotFoundException.class, () -> Class.forName(name, false, EmbeddedVideoDeclaration.class.getClassLoader()), name);
        }
    }

    @Test
    void noCrateOfItIsInTheClosure() {
        List<String> old = CrateClosure.of(List.of(DemoWorkspaceWidgetsCrate.INSTANCE)).stream()
                .map(Crate::name).filter(n -> n.equals("homing-workspace") || n.equals("homing-workspace-codecs")
                        || n.equals("homing-workspace-persistence") || n.equals("homing-studio-workspace")).toList();
        assertEquals(List.of(), old);
    }
}
