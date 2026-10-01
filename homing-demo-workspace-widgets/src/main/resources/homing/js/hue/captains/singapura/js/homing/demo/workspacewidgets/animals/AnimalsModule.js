// =============================================================================
// Animals — the six cute animals a demo widget can show: each an id, a name,
// and its picture, an SVG served as text (CuteAnimals). An animal travels by
// its id — what a party says, what a widget holds — and a widget that meets an
// id it does not know keeps the one it has. The picture goes on the page as a
// CSS image, a custom property a typed class draws (art); never as markup
// parsed into the page.
//
//   Animals.ALL          [{ id, name, svg }], in the order a chooser offers them
//   Animals.FIRST        the first one's id: where a widget starts, nothing chosen
//   Animals.byId(id)     → the animal, or null
//   Animals.art(id)      → url("data:image/svg+xml,…"): its picture, for a custom property;
//                          the first's, for an id it does not know
//
// Pure: no DOM.
// =============================================================================

class Animals {

    static ALL = Object.freeze([
        Object.freeze({ id: "turtle", name: "Turtle", svg: turtle }),
        Object.freeze({ id: "ghost", name: "Ghost", svg: ghost }),
        Object.freeze({ id: "broom", name: "Broom", svg: broom }),
        Object.freeze({ id: "penguin", name: "Penguin", svg: penguin }),
        Object.freeze({ id: "crocodile", name: "Crocodile", svg: crocodile }),
        Object.freeze({ id: "whale", name: "Whale", svg: whale })
    ]);

    static FIRST = "turtle";

    static byId(id) {
        return Animals.ALL.filter(function (a) { return a.id === id; })[0] || null;
    }

    static art(id) {
        var a = Animals.byId(id) || Animals.byId(Animals.FIRST);
        // The served text opens on a newline; the XML declaration must be the first thing in the image.
        return "url(\"data:image/svg+xml," + encodeURIComponent(a.svg.replace(/^\s+/, "")) + "\")";
    }
}
