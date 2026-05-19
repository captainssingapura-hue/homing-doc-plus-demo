function appMain(rootElement) {
    // Studio chrome shell — see MovingAnimal.js for the same pattern.
    css.addClass(rootElement, st_root);
    var _loading = document.createElement("div");
    css.addClass(_loading, st_loading);
    _loading.textContent = "Loading…";
    _loading.style.cssText = "padding:24px;";
    rootElement.appendChild(_loading);
    fetch("/brand")
        .then(function (r) { if (!r.ok) throw new Error("/brand HTTP " + r.status); return r.json(); })
        .then(function (brand) {
            rootElement.replaceChildren();
            rootElement.appendChild(Header({
                brand:  { href: brand.homeUrl, label: brand.label, logo: brand.logo },
                crumbs: [
                    { text: brand.label, href: brand.homeUrl },
                    { text: "Dancing Animals" }
                ]
            }));
            var _main = document.createElement("div");
            css.addClass(_main, st_main);
            rootElement.appendChild(_main);
            buildDance(_main);
        })
        .catch(function (err) {
            rootElement.replaceChildren();
            var e = document.createElement("div");
            e.style.cssText = "padding:24px;color:#c33;";
            e.textContent = "Failed to load chrome: " + err.message;
            rootElement.appendChild(e);
        });
}

function buildDance(rootElement) {
    const h1 = document.createElement("h1");
    css.setClass(h1, subway_title);
    h1.textContent = "Dancing Animals";
    rootElement.appendChild(h1);

    const hint = document.createElement("p");
    css.setClass(hint, subway_hint);
    hint.textContent = "Press \u2190 or \u2192 arrow keys to make them dance! Space to jump.";
    rootElement.appendChild(hint);

    const selector = createAnimalSelector();
    rootElement.appendChild(selector);

    const grid = document.createElement("div");
    css.setClass(grid, subway_grid);
    rootElement.appendChild(grid);

    const GRAVITY = 1.8;
    const JUMP_STRENGTH = 8;

    const cells = [];
    const reversed = [];
    const physicsArr = [];
    const offsets = [];

    for (let i = 0; i < 25; i++) {
        const cell = createAnimalCell(css.className(subway_cell));
        grid.appendChild(cell);
        cells.push(cell);
        reversed.push(Math.random() < 0.5);
        physicsArr.push(createJumpPhysics(GRAVITY, JUMP_STRENGTH));
        offsets.push(0);
    }

    document.addEventListener("keydown", (e) => {
        if (e.key === "ArrowLeft") {
            cells.forEach((cell, i) => cell.style.transform = reversed[i] ? "scaleX(1)" : "scaleX(-1)");
        } else if (e.key === "ArrowRight") {
            cells.forEach((cell, i) => cell.style.transform = reversed[i] ? "scaleX(-1)" : "scaleX(1)");
        } else if (e.key === " ") {
            e.preventDefault();
            physicsArr.forEach((p) => p.jump());
        }
    });

    function frame() {
        for (let i = 0; i < cells.length; i++) {
            offsets[i] = physicsArr[i].update(offsets[i], 0);
            cells[i].style.marginTop = offsets[i] + "px";
        }
        requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
}
