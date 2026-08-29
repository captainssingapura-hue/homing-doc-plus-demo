package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.docs.guides.HttpsTransportGuideDoc;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.DocTreeViewer;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/**
 * L1 category — operator-facing how-to guides, a sibling of {@link ReleasesCatalogue}
 * under the umbrella's {@link MultiStudioHome}.
 *
 * <p>Mirrors the releases arrangement: the catalogue node lives here, next to
 * {@code MultiStudioHome}, while the guide content lives in the public
 * {@code homing-docs} module, so the dependency points one way (demo → homing-docs)
 * with no cycle. As a {@link DocProvider} its docs flow into the studio's
 * {@code DocRegistry}, so DocReader / the doc-tree viewer serve them by UUID.</p>
 */
public record GuidesCatalogue()
        implements L1_Catalogue<MultiStudioHome, GuidesCatalogue>, DocProvider {

    public static final GuidesCatalogue INSTANCE = new GuidesCatalogue();

    @Override public MultiStudioHome parent() { return MultiStudioHome.INSTANCE; }
    @Override public String name()    { return "Guides"; }
    @Override public String summary() { return "How-to guides for running and configuring a Homing server."; }
    @Override public String badge()   { return "GUIDE"; }
    @Override public String icon()    { return "📘"; }

    @Override public List<Entry<GuidesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, DocTreeViewer.INSTANCE,
                        new DocTreeViewer.Params(HttpsTransportGuideDoc.INSTANCE.uuid().toString()),
                        HttpsTransportGuideDoc.INSTANCE)
        );
    }

    /** {@link DocProvider} contribution — guides feed the studio's DocRegistry so
     *  DocReader / the doc-tree viewer serve them by UUID. */
    @Override public List<Doc> docs() {
        return List.of(
                HttpsTransportGuideDoc.INSTANCE
        );
    }
}
