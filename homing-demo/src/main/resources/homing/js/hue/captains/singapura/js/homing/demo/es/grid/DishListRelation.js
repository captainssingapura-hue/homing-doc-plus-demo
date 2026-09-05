// =============================================================================
// DishListRelation — the RFC 0050 Dish List (Appendix B) as an in-memory
// Relation: createDishListRelation() returns a RelationAdapterContract-shaped
// object over the canonical six dishes + popularity. push() is the DOMAIN
// feed (ticks, external changes); update() is the grid's commit target and
// echoes through push so every subscriber (the grid's direct path included)
// sees the write.
// =============================================================================

function createDishListRelation() {
    // The style enum's DECLARED order — the cell's options AND the sort order,
    // from one place. Sorting by style yields this sequence, not the alphabet.
    var STYLES = ['Chinese', 'French', 'English', 'German', 'USA', 'Italian'];
    var NUMERIC = { calories: true, price: true, popularity: true };
    var byStyle = compareByOrder(STYLES);
    var data = {
        mapo:   { ingredient: 'tofu',    style: 'Chinese', calories: 480, price: 9.5,  popularity: 71 },
        coq:    { ingredient: 'chicken', style: 'French',  calories: 610, price: 18,   popularity: 64 },
        fish:   { ingredient: 'cod',     style: 'English', calories: 560, price: 12,   popularity: 58 },
        sauer:  { ingredient: 'pork',    style: 'German',  calories: 650, price: 14,   popularity: 49 },
        burger: { ingredient: 'beef',    style: 'USA',     calories: 780, price: 11,   popularity: 88 },
        carbo:  { ingredient: 'pasta',   style: 'Italian', calories: 720, price: 13,   popularity: 77 }
    };
    var subs = [];
    return {
        pks:     function () { return Object.keys(data); },
        columns: function () { return ['ingredient', 'style', 'calories', 'price', 'popularity']; },
        styles:  STYLES,
        /** The Relation's meta (ext6): every column declares how it orders.
         *  A comparator is mandatory to sort — the grid never guesses. */
        columnMeta: function (col) {
            return { compare: col === 'style' ? byStyle
                            : NUMERIC[col]    ? compareNumbers
                            :                   compareText };
        },
        get:     function (pk, col) { return data[pk] ? data[pk][col] : undefined; },
        subscribe:   function (fn) { subs.push(fn); },
        unsubscribe: function (fn) { var i = subs.indexOf(fn); if (i >= 0) subs.splice(i, 1); },
        /** The domain feed: write + notify every subscriber. */
        push: function (pk, col, v) {
            if (!data[pk]) return;
            data[pk][col] = v;
            subs.slice().forEach(function (fn) { fn(pk, col, v); });
        },
        /** The grid's commit target — echoes through the feed. */
        update: function (pk, col, v) { this.push(pk, col, v); },
        deleteRows: function (pks) {
            var self = this;
            pks.forEach(function (pk) { delete data[pk]; });
        }
    };
}
