package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.catalogue.site.AppListing;
import hue.captains.singapura.js.homing.designs.HomingDesigns;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Router;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.StandardMpa;

/**
 * The demo as a site: its router IS the catalogue router, at the root, its tree read with the
 * site's one MPA - every doc read in DocView, every catalogue's listing - so {@code /} is the
 * demo's listing and every other address a walk down its tree.
 */
public record DemoSite() implements Site {

    public static final DemoSite INSTANCE = new DemoSite();

    /** The site's one MPA: its brand, the designs it offers, the crate it serves. */
    public static final StandardMpa MPA = StandardMpa.of(Brand.of("Homing · demo"), HomingDesigns.REGISTRY, DemoSiteCrate.INSTANCE);

    /** Read once: the tree is checked when the site is made, not when a request arrives. */
    public static final CatalogueRouter ROUTER = CatalogueRouter.at(Path.ROOT, DemoCatalogue.INSTANCE, MPA).listing(AppListing.INSTANCE);

    @Override public String name() { return "homing-demo-site"; }

    @Override public Router router() { return ROUTER; }
}
