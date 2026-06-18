package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

/**
 * Typed CSS for the Animals Playground workspace's widgets — the generic
 * widget root wrapper, the doc-view layout, mount scaffolds, host slot,
 * loading messages, the "+" launcher. Replaces every inline cssText the
 * widgets would otherwise carry. Per RFC 0027.
 *
 * <p>Distinct from {@code demo.css.PlaygroundStyles} (the platformer's
 * game-specific styles). The two coexist; this one is workspace-side
 * scaffolding, the other is the moving-animal-platformer's painted
 * world. Both prefixes are {@code pg_} historically but the class
 * names are different so Java imports don't clash.</p>
 *
 * @since RFC 0025 Ext1b b.2a — workspace chrome typed CSS.
 */
public record AnimalsPlaygroundStyles() implements CssGroup<AnimalsPlaygroundStyles> {

    public static final AnimalsPlaygroundStyles INSTANCE = new AnimalsPlaygroundStyles();

    public record pg_host() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                position: relative;
                flex: 1;
                width: 100%;
                min-height: 0;
                """; }
    }

    public record pg_mount() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                height: 100%;
                display: flex;
                flex-direction: column;
                """; }
    }

    public record pg_loading() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                padding: 16px;
                font: 13px sans-serif;
                color: var(--color-text-muted, #888);
                """; }
    }

    public record pg_pinned_loading() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                padding: 12px;
                font: 13px sans-serif;
                color: var(--color-text-muted, #888);
                """; }
    }

    public record pg_doc_root() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                display: flex;
                flex-direction: column;
                height: 100%;
                background: var(--color-surface, #fff);
                color: var(--color-text-primary, #111);
                """; }
    }

    public record pg_doc_header() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                padding: 8px 12px;
                font: 600 14px sans-serif;
                border-bottom: 1px solid var(--color-border, #ddd);
                """; }
    }

    public record pg_doc_body() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                flex: 1;
                overflow: auto;
                padding: 12px 16px;
                font: 14px/1.55 sans-serif;
                """; }
    }

    /**
     * Generic widget-root wrapper used by SpinningAnimalsWidget,
     * DancingAnimalsWidget, MovingAnimalWidget and anything else that
     * needs a padded, scrollable body inside a pane.
     */
    public record pg_widget_root() implements CssClass<AnimalsPlaygroundStyles> {
        @Override public String body() { return """
                padding: 16px;
                height: 100%;
                box-sizing: border-box;
                overflow: auto;
                """; }
    }

    @Override
    public List<CssClass<AnimalsPlaygroundStyles>> cssClasses() {
        return List.of(
                new pg_host(),
                new pg_mount(),
                new pg_loading(),
                new pg_pinned_loading(),
                new pg_doc_root(),
                new pg_doc_header(),
                new pg_doc_body(),
                new pg_widget_root()
        );
    }

    @Override
    public CssImportsFor<AnimalsPlaygroundStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
