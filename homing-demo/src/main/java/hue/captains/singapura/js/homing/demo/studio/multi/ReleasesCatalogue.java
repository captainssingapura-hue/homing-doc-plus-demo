package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.docs.migrations.MigrateTo0_8_0Doc;
import hue.captains.singapura.js.homing.docs.releases.Release0_5_1Doc;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.DocTreeViewer;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/**
 * L1 category — public release notes for the Homing framework, a sibling of
 * {@link LearningStudioCategory} under the umbrella's {@link MultiStudioHome}.
 *
 * <p>Holds the release docs that live in the public {@code homing-docs} module
 * (from 0.5.1 onward — 0.5.0 and earlier remain inside {@code homing-self-studio}).
 * Each is a typed {@link Doc} ({@link Release0_5_1Doc} is a {@code RigidDoc}); the
 * catalogue node lives here, next to {@code MultiStudioHome}, while its content
 * lives in {@code homing-docs}, so the dependency points one way (demo →
 * homing-docs) with no cycle. As a {@link DocProvider} its docs flow into the
 * studio's {@code DocRegistry} automatically (collected from {@code home()}), so
 * DocReader / the doc-tree viewer serve them by UUID. Newest first; prepend new
 * releases as they ship.</p>
 */
public record ReleasesCatalogue()
        implements L1_Catalogue<MultiStudioHome, ReleasesCatalogue>, DocProvider {

    public static final ReleasesCatalogue INSTANCE = new ReleasesCatalogue();

    @Override public MultiStudioHome parent() { return MultiStudioHome.INSTANCE; }
    @Override public String name()    { return "Releases"; }
    @Override public String summary() { return "Release notes and migration guides for Homing — newest first. The 0.8.0 guide is the one to read before upgrading: that release removes 37 public types, and one of its breaks does not fail the build. From 0.5.1 on, authored as public RigidDocs in the homing-docs module."; }
    @Override public String badge()   { return "RELEASE"; }
    @Override public String icon()    { return "🏷️"; }

    @Override public List<Entry<ReleasesCatalogue>> leaves() {
        // Newest first. Prepend new releases here.
        return List.of(
                // The 0.8.0 migration guide sits above the release notes, because it is
                // what a downstream needs AT upgrade time and 0.8.0 removes 37 public
                // types. Scoped to the RELEASE rather than to RFC 0053 - the correction
                // the first downstream port forced, after planning a two-file job that
                // touched ten.
                Entry.of(this, DocTreeViewer.INSTANCE,
                        new DocTreeViewer.Params(MigrateTo0_8_0Doc.INSTANCE.uuid().toString()),
                        MigrateTo0_8_0Doc.INSTANCE),
                Entry.of(this, DocTreeViewer.INSTANCE,
                        new DocTreeViewer.Params(Release0_5_1Doc.INSTANCE.uuid().toString()),
                        Release0_5_1Doc.INSTANCE)
        );
    }

    /** {@link DocProvider} contribution — releases feed the studio's
     *  DocRegistry so DocReader / the doc-tree viewer serve them by UUID. */
    @Override public List<Doc> docs() {
        return List.of(
                MigrateTo0_8_0Doc.INSTANCE,
                Release0_5_1Doc.INSTANCE
        );
    }
}
