package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.designs.HomingDesigns;
import hue.captains.singapura.js.homing.site.Router;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.StandardMpa;

/**
 * The demo's workspaces, standing up on their own: a site of grouped workspaces -
 * the group a page, {@code /demo}, the root sending to it, each workspace where
 * the group files it, {@code #ws/<section>/<kind>} - under the framework's
 * standard MPA: no studio, and so none of the studio's old workspace. The server
 * keeps their states, so every route says so.
 */
public record DemoWorkspaceSite() implements Site {

    public static final DemoWorkspaceSite INSTANCE = new DemoWorkspaceSite();

    /** The one declaration this site makes: the brand, the designs, the crate it serves. */
    public static final StandardMpa MPA = StandardMpa.of(Brand.of("Demo workspace"), HomingDesigns.REGISTRY, DemoWorkspaceCrate.INSTANCE);

    private static final Router ROUTER = DemoGroups.SITE.router(MPA, DemoWorkspaceApp.INSTANCE, true);

    @Override public String name() { return "demo-workspace"; }

    @Override public Router router() { return ROUTER; }
}
