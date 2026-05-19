// =============================================================================
// AnimalCell — shared widget that renders one animal SVG inside a labelled
// cell. Used by Dancing/Spinning/Moving demos.
//
// Doctrine compliance:
//   - Pure-Component Views: no HTML tag literals; no innerHTML writes.
//     SVG strings from the typed SvgGroup are parsed via DOMParser and
//     installed as Nodes.
//   - Owned References: the wrapper inside each cell is held in liveCells
//     alongside the cell — no querySelector traversal.
//   - Managed DOM Ops: AnimalCell is a helper used by imperative game-loop
//     demos, so this doctrine is out of scope (per the SPA-only scoping).
// =============================================================================

const allAnimals = [
    { name: "Turtle", svg: turtle },
    { name: "Ghost", svg: ghost },
    { name: "Broom", svg: broom },
    { name: "Penguin", svg: penguin },
    { name: "Crocodile", svg: crocodile },
    { name: "Whale", svg: whale }
];

var selectedIndex = 0;
// Each entry is { cell, wrapper } so refreshCells doesn't need a selector lookup.
var liveCells = [];

// Parse an SVG string into a typed Node. Done at point-of-use; the framework
// emits SVGs as strings (typed asset, not HTML authored by consumer code), and
// DOMParser turns the typed string into a Node without going through any
// element's innerHTML setter.
//
// The framework's SvgGroupContentProvider emits the SVG inside a JS template
// literal opened with a backtick immediately followed by a newline — so the
// runtime string starts with "\n". Strict XML parsers (Firefox) then reject
// the <?xml ...?> declaration with "XML declaration allowed only at the start
// of the document" because the declaration is on line 2, not line 1. Trim
// leading whitespace before parsing so the declaration lands at column 0
// line 1 regardless of how the bundler framed the literal.
function parseSvgToNode(svgString) {
    var doc = new DOMParser().parseFromString(svgString.replace(/^\s+/, ""), "image/svg+xml");
    var el = doc.documentElement;
    // Force the SVG to fill its container regardless of the source artwork's
    // intrinsic dimensions. The original SVGs declare large explicit sizes
    // (typically 800×800); without overriding, the rendered element ignores
    // any sizing CSS the consumer expects. The standalone CSS files used to
    // do this via a `.subway-cell svg { width: 100% }` descendant rule, but
    // that style is theme-globals territory and isn't reliably available in
    // the studio chrome context. Setting the attribute directly is robust.
    el.setAttribute("width", "100%");
    el.setAttribute("height", "100%");
    return el;
}

function currentAnimalSvg() {
    if (selectedIndex < 0) {
        return allAnimals[Math.floor(Math.random() * allAnimals.length)].svg;
    }
    return allAnimals[selectedIndex].svg;
}

function getAnimalSvg() {
    // Kept as a backwards-compatible helper for consumers that still want the
    // raw string (e.g. a future Component impl that templates it). Prefer
    // calling parseSvgToNode(currentAnimalSvg()) directly when building DOM.
    return currentAnimalSvg();
}

function refreshCells() {
    for (var i = 0; i < liveCells.length; i++) {
        var w = liveCells[i].wrapper;
        w.replaceChildren(parseSvgToNode(currentAnimalSvg()));
    }
}

function createAnimalCell(className) {
    var cell = document.createElement("div");
    cell.className = className;
    var wrapper = document.createElement("div");
    wrapper.appendChild(parseSvgToNode(currentAnimalSvg()));
    cell.appendChild(wrapper);
    liveCells.push({ cell: cell, wrapper: wrapper });
    return cell;
}

function createAnimalSelector() {
    var container = document.createElement("label");
    container.textContent = "Animal ";
    var select = document.createElement("select");

    var randomOpt = document.createElement("option");
    randomOpt.value = "-1";
    randomOpt.textContent = "Random";
    select.appendChild(randomOpt);

    for (var i = 0; i < allAnimals.length; i++) {
        var opt = document.createElement("option");
        opt.value = String(i);
        opt.textContent = allAnimals[i].name;
        if (i === selectedIndex) opt.selected = true;
        select.appendChild(opt);
    }

    select.addEventListener("change", function () {
        selectedIndex = parseInt(select.value);
        refreshCells();
    });

    container.appendChild(select);
    return container;
}
