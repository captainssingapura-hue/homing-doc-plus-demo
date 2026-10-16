package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;

import java.util.List;


/** The taxonomy workbench's widgets: a column of what each shows, the tree's port, the table's frame, facts, panels, links to nodes, what is lit. */
public record TaxonomyStyles() implements CssGroup<TaxonomyStyles> {

    public static final TaxonomyStyles INSTANCE = new TaxonomyStyles();

    /** A widget's column: what it shows, top to bottom, with air. */
    public record tx_root() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            height: 100%;
            width: 100%;
            min-height: 0;
            padding: 10px 12px;
            box-sizing: border-box;
            gap: 8px;
            """;
        }
    }

    /** What a widget says of itself, small and quiet. */
    public record tx_hint() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "margin: 0;"; }
    }

    /** A heading inside a widget: the picked node's name, a panel's title. */
    public record tx_title() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "margin: 6px 0 0;"; }
    }

    /** The tree's scrollport. */
    public record tx_port() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "flex: 1;\nmin-height: 0;\noverflow: auto;\n"; }
    }

    /** The frame round the parts table's scrollport: the grid's light goes round the scrollbar, not inside it. */
    public record tx_frame() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "position: relative;\nflex: 1;\ndisplay: flex;\nflex-direction: column;\nmin-height: 0;\n"; }
    }

    /** What scrolls in the details. */
    public record tx_scroll() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "flex: 1;\nmin-height: 0;\noverflow: auto;\ndisplay: flex;\nflex-direction: column;\ngap: 6px;\n"; }
    }

    /** One panel of the details, framed. */
    public record tx_panel() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "display: flex;\nflex-direction: column;\ngap: 6px;\npadding: 8px 12px 10px;\n"; }
    }

    /** Facts: a key, then its value, a row each. */
    public record tx_facts() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "display: grid;\ngrid-template-columns: max-content 1fr;\ngap: 3px 14px;\nmargin: 0;\n"; }
    }

    public record tx_key() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return ""; }
    }

    /** A value as code: a token, a class. */
    public record tx_code() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "overflow-wrap: anywhere;"; }
    }

    /** One line of a list that reads across. */
    public record tx_line() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "display: flex;\nflex-wrap: wrap;\nalign-items: baseline;\ngap: 4px 10px;\n"; }
    }

    /** A node named so it can be picked: a button, drawn as the design draws a link. */
    public record tx_link() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "background: none;\nborder: 0;\npadding: 0;\nmargin: 0;\ntext-align: start;\n"; }
    }

    /** A meaning's words, to be read: a paragraph each. */
    public record tx_prose() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "margin: 0;\nline-height: 1.5;\nmax-width: 72ch;\n"; }
    }

    /** A word beside a thing - what it is, where it is - small and quiet. */
    public record tx_tag() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return ""; }
    }

    /** A file's text as it is: its lines kept, scrolled across rather than wrapped. */
    public record tx_source() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "margin: 0;\nwhite-space: pre;\nfont-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;\nfont-size: 12px;\nline-height: 1.45;\ntab-size: 4;\n"; }
    }

    /** The picked node's chain, lit. */
    public record tx_on() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "padding: 0 4px;"; }
    }

    /** Where a widget stands one it holds - a demo: all the room left, framed; what it holds fills it. */
    public record tx_slot() implements CssClass<TaxonomyStyles> {
        @Override public String body() { return "position: relative;\nflex: 1;\nmin-height: 0;\noverflow: hidden;\n"; }
    }

    @Override
    public List<CssClass<TaxonomyStyles>> cssClasses() {
        return List.of(new tx_root(), new tx_hint(), new tx_title(), new tx_port(), new tx_frame(), new tx_scroll(), new tx_panel(), new tx_facts(),
                       new tx_key(), new tx_code(), new tx_line(), new tx_link(), new tx_tag(), new tx_on(), new tx_prose(), new tx_slot(), new tx_source());
    }
}
