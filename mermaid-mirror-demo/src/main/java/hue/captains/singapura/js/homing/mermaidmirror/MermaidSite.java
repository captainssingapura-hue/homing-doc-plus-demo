package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.catalogue.site.AppListing;
import hue.captains.singapura.js.homing.designs.HomingDesigns;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Router;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.StandardMpa;

/**
 * Port 1's site: the catalogue router at the root, its one doc read in DocView, every page made
 * with the site's one MPA. The doc's diagram imports Mermaid through core's proxy, which the
 * server points at the local CDN before it serves anything.
 */
public record MermaidSite() implements Site {

    public static final MermaidSite INSTANCE = new MermaidSite();

    /** The site's one MPA: its brand, the designs it offers, the crate it serves. */
    public static final StandardMpa MPA = StandardMpa.of(Brand.of("Mermaid Mirror Demo"), HomingDesigns.REGISTRY, MermaidSiteCrate.INSTANCE);

    /** Read once: the tree is checked when the site is made, not when a request arrives. */
    public static final CatalogueRouter ROUTER = CatalogueRouter.at(Path.ROOT, MermaidDemoCatalogue.INSTANCE, MPA).listing(AppListing.INSTANCE);

    @Override public String name() { return "mermaid-mirror-demo"; }

    @Override public Router router() { return ROUTER; }
}
