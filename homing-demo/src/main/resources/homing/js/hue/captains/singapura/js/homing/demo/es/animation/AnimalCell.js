// =============================================================================
// AnimalCell — shared widget that renders one animal SVG inside a labelled
// cell. Used by Dancing/Spinning/Moving demos.
//
// Doctrine compliance:
//   - Pure-Component Views: no HTML tag literals; no innerHTML writes.
//     SVG strings from the typed SvgGroup are parsed via DOMParser and
//     installed as Nodes.
//   - Owned References: the wrapper inside each cell is held in the scope's
//     cells array alongside the cell — no querySelector traversal.
//   - Managed DOM Ops: AnimalCell is a helper used by imperative game-loop
//     demos, so this doctrine is out of scope (per the SPA-only scoping).
//
// Scoping (RFC 0025 Ext1b b.2b fix):
//   ES modules cache per URL — three workspace widgets all importing AnimalCell
//   share the same module instance. The original implementation kept
//   `selectedIndex` and `liveCells` at module level, which meant the selector
//   in widget A refreshed cells in widget B as well. The fix:
//
//     var scope = createAnimalScope();
//     var cell  = createAnimalCell(className, scope);
//     var sel   = createAnimalSelector(scope);
//
//   Each widget instance mints its own scope; cells + selectedIndex are
//   per-scope. Legacy callers that don't pass a scope fall back to a shared
//   default scope (so the original single-instance AppModule pages still work).
// =============================================================================

const allAnimals = [
    { name: "Turtle", svg: turtle },
    { name: "Ghost", svg: ghost },
    { name: "Broom", svg: broom },
    { name: "Penguin", svg: penguin },
    { name: "Crocodile", svg: crocodile },
    { name: "Whale", svg: whale }
];

function createAnimalScope() {
    return { selectedIndex: 0, cells: [] };
}

// Shared default scope — used by callers that don't pass an explicit scope
// (legacy AppModules; one-shot rendering contexts where isolation doesn't
// matter). New widget code should always mint its own via createAnimalScope().
var _defaultScope = createAnimalScope();

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
    // any sizing CSS the consumer expects.
    el.setAttribute("width", "100%");
    el.setAttribute("height", "100%");
    return el;
}

function _currentSvgFor(scope) {
    if (scope.selectedIndex < 0) {
        return allAnimals[Math.floor(Math.random() * allAnimals.length)].svg;
    }
    return allAnimals[scope.selectedIndex].svg;
}

function _refreshCellsIn(scope) {
    for (var i = 0; i < scope.cells.length; i++) {
        scope.cells[i].wrapper.replaceChildren(parseSvgToNode(_currentSvgFor(scope)));
    }
}

// RFC 0028 cycle 4 — exported entry point for AnimalsParty reactors:
// set the scope's selected index then refresh cells in one call. Used by
// animal widgets when they receive an AnimalChanged broadcast from the
// global Ribbon-driven AnimalsParty.
function setScopeAnimal(scope, index) {
    scope.selectedIndex = parseInt(index, 10);
    _refreshCellsIn(scope);
}

function createAnimalCell(className, scope) {
    var s = scope || _defaultScope;
    var cell = document.createElement("div");
    cell.className = className;
    var wrapper = document.createElement("div");
    wrapper.appendChild(parseSvgToNode(_currentSvgFor(s)));
    cell.appendChild(wrapper);
    s.cells.push({ cell: cell, wrapper: wrapper });
    return cell;
}

function createAnimalSelector(scope) {
    var s = scope || _defaultScope;
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
        if (i === s.selectedIndex) opt.selected = true;
        select.appendChild(opt);
    }

    select.addEventListener("change", function () {
        s.selectedIndex = parseInt(select.value);
        _refreshCellsIn(s);
    });

    container.appendChild(select);
    return container;
}
