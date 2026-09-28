package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.designs.HomingDesigns;
import hue.captains.singapura.js.homing.site.Router;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.mpa.AppPage;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.StandardMpa;
import hue.captains.singapura.js.homing.workspace.shell.WorkspacePageModule;

import java.util.Optional;

/**
 * The demo workspace, standing up on its own: a site whose one page is the
 * workspace, under the framework's standard MPA - no studio, and so none of the
 * studio's old workspace. The server keeps its states, so the route says so.
 */
public record DemoWorkspaceSite() implements Site {

    public static final DemoWorkspaceSite INSTANCE = new DemoWorkspaceSite();

    /** The one declaration this site makes: the brand, the designs, the crate it serves. */
    public static final StandardMpa MPA = StandardMpa.of(Brand.of("Demo workspace"), HomingDesigns.REGISTRY, DemoWorkspaceCrate.INSTANCE);

    static final AppPage<?, ?> DEMO = MPA.page(DemoWorkspaceApp.INSTANCE, new WorkspacePageModule.Params(true));

    @Override public String name() { return "demo-workspace"; }

    @Override
    public Router router() { return path -> path.isRoot() ? Optional.of(DEMO) : Optional.empty(); }
}
