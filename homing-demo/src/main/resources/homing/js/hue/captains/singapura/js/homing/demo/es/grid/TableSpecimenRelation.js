// =============================================================================
// TableSpecimenRelation — the Table Workbench's fixtures: one depot inventory,
// grown to whatever shape a specimen asks for.
//
//   createSpecimenRelation({ rows, cols })
//
// Two deliberate choices, both because this is a SCROLL test bed:
//
//   1. The first column is the row ordinal. A cursor parked on row 137 of an
//      unlabelled table tells you nothing; "where am I?" has to be answerable
//      at a glance, or you cannot tell a follow that worked from one that
//      landed somewhere else.
//   2. The data is deterministic — a tiny LCG seeded per row, never
//      Math.random. Reload the workbench and row 137 reads the same, so a
//      before/after comparison is worth something.
//
// Columns beyond the four named ones are monthly periods (2024-01 onwards),
// which is what makes the WIDE specimens plausible rather than filler.
// =============================================================================

var _TW_ITEMS = [
    'Anchor bolt', 'Ball bearing', 'Cable gland', 'Drive belt', 'End cap',
    'Flange nut', 'Gasket ring', 'Hex screw', 'Idler pulley', 'Journal pin',
    'Keyway shim', 'Lock washer', 'Mount bracket', 'Needle valve', 'O-ring',
    'Pivot arm', 'Quill sleeve', 'Roller chain', 'Spring clip', 'Thrust collar',
    'U-bolt', 'V-seal', 'Wear plate', 'Xenon lamp', 'Yoke coupler', 'Zinc anode'
];
var _TW_REGIONS = ['North', 'South', 'East', 'West', 'Central', 'Coastal'];

/** Deterministic per-row noise — same seed, same table, every reload. */
function _twNoise(seed) {
    var x = (seed * 1103515245 + 12345) % 2147483648;
    return x < 0 ? x + 2147483648 : x;
}

function _twPeriod(k) {
    var y = 2024 + Math.floor(k / 12), m = (k % 12) + 1;
    return y + '-' + (m < 10 ? '0' : '') + m;
}

function createSpecimenRelation(shape) {
    shape = shape || {};
    var rows = Math.max(1, shape.rows || 40);
    var cols = Math.max(1, shape.cols || 6);

    var NAMED = ['#', 'sku', 'item', 'region'];
    var columns = NAMED.slice(0, Math.min(NAMED.length, cols));
    for (var c = NAMED.length; c < cols; c++) columns.push(_twPeriod(c - NAMED.length));

    var pks = [], data = {};
    for (var r = 0; r < rows; r++) {
        var pk = 'r' + r, n = _twNoise(r + 1);
        var row = {
            '#':      r,
            sku:      'SKU-' + (10000 + r),
            item:     _TW_ITEMS[r % _TW_ITEMS.length],
            region:   _TW_REGIONS[(n >> 7) % _TW_REGIONS.length]
        };
        for (var k = 0; k + NAMED.length < cols; k++)
            row[_twPeriod(k)] = _twNoise(r * 97 + k + 1) % 900 + 20;
        pks.push(pk);
        data[pk] = row;
    }

    var subs = [];
    var isNumeric = function (col) { return col === '#' || /^\d{4}-\d{2}$/.test(col); };
    return {
        pks:     function () { return pks; },
        columns: function () { return columns; },
        /** The Relation's meta (ext6): the ordinal and the period columns
         *  order by value — 2 before 10, not '10' before '2'; the named text
         *  columns in natural order. Mandatory to sort; the grid never guesses. */
        columnMeta: function (col) {
            return { compare: isNumeric(col) ? compareNumbers : compareText };
        },
        get:     function (pk, col) { return data[pk] ? data[pk][col] : undefined; },
        subscribe:   function (fn) { subs.push(fn); },
        unsubscribe: function (fn) { var i = subs.indexOf(fn); if (i >= 0) subs.splice(i, 1); },
        push: function (pk, col, v) {
            if (!data[pk]) return;
            data[pk][col] = v;
            subs.slice().forEach(function (fn) { fn(pk, col, v); });
        },
        update: function (pk, col, v) { this.push(pk, col, v); },
        deleteRows: function (killed) {
            killed.forEach(function (pk) {
                delete data[pk];
                var i = pks.indexOf(pk);
                if (i >= 0) pks.splice(i, 1);
            });
        }
    };
}
