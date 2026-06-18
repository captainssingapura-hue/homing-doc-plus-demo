package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Generic WorkspaceWidget that renders a Markdown body inside a scrollable
 * panel with a small header. Reusable — any workspace can register one or
 * more instances of this widget with different {@link Params} defaults to
 * surface static doc content (intro notes, cheat-sheets, release notes,
 * help panels…).
 *
 * <p>In the Animals Playground workspace ({@link AnimalPlaygroundSpec}) this
 * widget is pinned with the playground's introduction as its defaults — the
 * workspace's auto-spawn instantiates one tab at boot, close disabled,
 * so the welcome content is always visible.</p>
 *
 * <p>Markdown rendering uses {@link MarkedJs} from {@code homing-libs}.
 * No fetch — body is passed via params, so the widget remains usable
 * across origins and offline.</p>
 *
 * @since RFC 0025 Ext1b — Mechanism 2 demo case (b.2a)
 */
public final class DocViewWidget extends WorkspaceWidget<DocViewWidget.Params, DocViewWidget> {

    public static final DocViewWidget INSTANCE = new DocViewWidget();

    private DocViewWidget() {}

    /**
     * Two-field record — title shows in the widget header strip; body is
     * the Markdown source rendered into the scrollable content area.
     */
    public record Params(String title, String body) implements WorkspaceWidget._Param {}

    private record construct() implements WorkspaceWidget._Construct<Params, DocViewWidget> {}

    @Override protected _Construct<Params, DocViewWidget> construct() { return new construct(); }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Document"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE),
                new ModuleImports<>(List.of(
                        new AnimalsPlaygroundStyles.pg_doc_root(),
                        new AnimalsPlaygroundStyles.pg_doc_header(),
                        new AnimalsPlaygroundStyles.pg_doc_body()),
                        AnimalsPlaygroundStyles.INSTANCE));
    }

    @Override
    protected List<String> constructBodyJs() {
        // Lines join with '\n' at emit time — each string here is one JS line.
        return List.of(
                "    var root = branch.createElement('root', 'div');",
                "    css.setClass(root, pg_doc_root);",
                "    var header = branch.createElement('header', 'div');",
                "    css.setClass(header, pg_doc_header);",
                "    header.textContent = (params && params.title) ? params.title : 'Document';",
                "    var body = branch.createElement('body', 'div');",
                "    css.setClass(body, pg_doc_body);",
                "    var md = (params && params.body) ? params.body : '';",
                "    try {",
                "        body.innerHTML = marked.parse(md);",
                "    } catch (e) {",
                "        body.textContent = md;",
                "    }",
                "    root.appendChild(header);",
                "    root.appendChild(body);",
                "    // Doc view has nothing to gate — overlay handles mouse, no document",
                "    // listeners, no audio. setActive is a no-op (still required by contract).",
                "    return { root: root, setActive: function (active) {} };"
        );
    }
}
