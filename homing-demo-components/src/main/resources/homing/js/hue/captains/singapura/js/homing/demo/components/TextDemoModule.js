// =============================================================================
// TextDemo — a house text in action: one of the fifteen, as its leaf says -
// a heading of each level, kicker, caption, lede, label, readout, a table cell's
// words, badge, pill, wordmark -
// made as the leaf's own class on the element its companion makes, holding
// the words such a text holds, in the look the style gives it. A text does
// nothing; what there is to see is its face, and its options: its size.
//
//   new TextDemo(container, { leaf })   leaf: "heading1", …
//   (the rest is a ComponentDemo's)
// =============================================================================

// each leaf, the class that is it, and the words such a text holds - and what it is for, said
var _TEXT_KINDS = Object.freeze({
    "heading1": Object.freeze({ Leaf: Heading1, Factory: Heading1Factory, words: "Quarterly report",
                                says: "a heading of level 1: a page's own name: the first heading of its outline" }),
    "heading2": Object.freeze({ Leaf: Heading2, Factory: Heading2Factory, words: "Revenue",
                                says: "a heading of level 2: a part of the page: the heading of one of its great divisions" }),
    "heading3": Object.freeze({ Leaf: Heading3, Factory: Heading3Factory, words: "By region",
                                says: "a heading of level 3: a section within a part" }),
    "heading4": Object.freeze({ Leaf: Heading4, Factory: Heading4Factory, words: "Northern stores",
                                says: "a heading of level 4: a smaller section, or what a container holds - a card's, a group's, a setting's title" }),
    "heading5": Object.freeze({ Leaf: Heading5, Factory: Heading5Factory, words: "Weekend trading",
                                says: "a heading of level 5: a division within a smaller section" }),
    "heading6": Object.freeze({ Leaf: Heading6, Factory: Heading6Factory, words: "Notes",
                                says: "a heading of level 6: the least heading: a division within that" }),
    "kicker":   Object.freeze({ Leaf: Kicker,   Factory: KickerFactory,   words: "Appearance",
                                says: "a kicker: the small word set above or before, naming the kind of thing that follows" }),
    "caption":  Object.freeze({ Leaf: Caption,  Factory: CaptionFactory,  words: "Updated two minutes ago",
                                says: "a caption: a quiet note beside what it is about" }),
    "lede":     Object.freeze({ Leaf: Lede,     Factory: LedeFactory,     words: "The figures for the quarter, by region, against the plan - and what moved them.",
                                says: "a lede: the sentence that opens a section and says what it is about" }),
    "label":    Object.freeze({ Leaf: Label,    Factory: LabelFactory,    words: "Volume",
                                says: "a label: the name of the control it stands by" }),
    "readout":  Object.freeze({ Leaf: Readout,  Factory: ReadoutFactory,  words: "42 %",
                                says: "a readout: a value read out exactly, as it stands now" }),
    "text-cell": Object.freeze({ Leaf: TextCell, Factory: TextCellFactory, words: "1,204",
                                says: "a text cell: a table cell's words - a fact where its row and its column meet, or the name heading its column" }),
    "badge":    Object.freeze({ Leaf: Badge,    Factory: BadgeFactory,    words: "Beta",
                                says: "a badge: a short tag set apart, saying what kind a thing is" }),
    "pill":     Object.freeze({ Leaf: Pill,     Factory: PillFactory,     words: "+3",
                                says: "a pill: a count, set apart - how many more there are" }),
    "wordmark": Object.freeze({ Leaf: Wordmark, Factory: WordmarkFactory, words: "Homing",
                                says: "a wordmark: the site's name, set as its own" })
});

class TextDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "text-demo", params);
        var kind = _TEXT_KINDS[this.leaf];
        if (!kind) throw new Error("[TextDemo] no text for the leaf '" + this.leaf + "'; one of " + Object.keys(_TEXT_KINDS).join(", "));
        var row = this.el("row", "div", dm_row, this.stage);
        var props = Object.assign({ text: kind.words }, kind.props || {});
        this._text = new kind.Leaf(kind.Factory.create(this.branch, "text"), props);
        row.appendChild(this._text.el);
        this.intro = kind.says;
    }

    size(v) { this._text.size = v; }
}
