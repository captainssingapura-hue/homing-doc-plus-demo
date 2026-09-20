// =============================================================================
// WelcomeApp — the gallery's front page. appMain(el) draws it into the slot
// the chrome handed over; the theme line is re-read whenever the theme
// changes, which is how a page sees a pick made on the bar. The cards are
// the shared Card builder; this page owns the grid they sit in and nothing
// of what a card looks like.
// =============================================================================

const _owner = Object.freeze({ toString: () => "welcome" });

var PAGES = [
    { title: "Counter", badge: "JS", text: "A JS app with typed params. The site's router binds the start off the path and tells the page where it is.", link: "/counter/7" },
    { title: "Counter, from zero", badge: "JS", text: "The same app, bound with nothing: the address /counter and a start of 0.", link: "/counter" },
    { title: "A plain page", badge: "HTML", text: "Not under the chrome at all: the router serves a string, and the MPA is nowhere in it.", link: "/plain" },
    { title: "The flat address", badge: "PERMALINK", text: "The framework's permalink for a JS app, /app?app=welcome, served by the MPA's own route.", link: "/app?app=welcome" },
    { title: "Grid", badge: "REL-GRID", text: "The relation grid from its own repo, over twelve books, as a page under the chrome. Titles and ratings edit.", link: "/grid" },
    { title: "Tree", badge: "REL-TREE", text: "The same books as shelf \u2192 book in the relation tree. An unfold is a question on the ask channel.", link: "/tree" },
    { title: "Dialog", badge: "UI-DIALOG", text: "A frame that owns the screen: inert behind, keys captured, Escape, Enter, focus given back. And one that does not.", link: "/dialog" },
    { title: "Preferences", badge: "PREFERENCES", text: "A rigid tree of preferences on the left, the chosen one's widget on the right, each loaded when first chosen. Theme, locale, editor.", link: "/preferences" },
    { title: "Panes", badge: "UI-PANES", text: "One pane of tabs holding widgets by the base's contract: add from a picker, switch, drag a chip to reorder, close. Every mutation is on the log below it.", link: "/panes" },
    { title: "Buttons", badge: "UI-ELEMENTS", text: "The button through its builder: six colour words under one extent slider, and a danger button whose extent follows the data.", link: "/buttons" },
    { title: "Cards", badge: "UI-ELEMENTS", text: "The card through its builder: a hard frame the design measures and proportions, grown by its size; one that scrolls, one with its own body, one with an action.", link: "/cards" },
    { title: "Floating panes", badge: "UI-FLOATING", text: "A desk and the panes that float on it: opened with a widget, dragged by the head, sized by the corner, raised by a press, closed by the cross or Escape.", link: "/floating" }
];

class WelcomeWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-site-mpa";
        el.appendChild(kicker);

        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Gallery";
        el.appendChild(title);

        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        el.appendChild(lede);
        function wearing() {
            lede.textContent = "Two JS apps as pages under the standard chrome, wearing "
                + (css.theme() || "the default") + ". The bar, the trail and the theme menu are the MPA's; "
                + "this page is an AppModule mounted in the slot it was given. Pick another theme on the bar and "
                + "every sheet on the page follows.";
        }
        wearing();
        css.onThemeApplied(wearing);

        var cards = branch.createElement("cards", "div");
        css.addClass(cards, ga_cards);
        for (var i = 0; i < PAGES.length; i++) {
            cards.appendChild(new CardBuilder().title(PAGES[i].title).text(PAGES[i].text).badge(PAGES[i].badge).link(PAGES[i].link).aspect(0.6)
                .build(branch.createBranch("card-" + i)).root);
        }
        el.appendChild(cards);
        this.root = el;
    }

    dispose() {}
}

function appMain(el, params) {
    el.appendChild(new WelcomeWidget(domOpsParty.createBranch("welcome"), params).root);
}
