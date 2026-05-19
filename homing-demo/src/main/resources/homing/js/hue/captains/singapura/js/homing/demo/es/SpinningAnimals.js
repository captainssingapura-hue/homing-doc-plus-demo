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
                    { text: "Spinning Animals" }
                ]
            }));
            var _main = document.createElement("div");
            css.addClass(_main, st_main);
            rootElement.appendChild(_main);
            buildSpin(_main);
        })
        .catch(function (err) {
            rootElement.replaceChildren();
            var e = document.createElement("div");
            e.style.cssText = "padding:24px;color:#c33;";
            e.textContent = "Failed to load chrome: " + err.message;
            rootElement.appendChild(e);
        });
}

function buildSpin(rootElement) {
    const h1 = document.createElement("h1");
    css.setClass(h1, spin_title);
    h1.textContent = "Spinning Animals";
    rootElement.appendChild(h1);

    const hint = document.createElement("p");
    css.setClass(hint, spin_hint);
    hint.textContent = "Click an animal to pause/resume it. Use the controls to adjust speed and direction.";
    rootElement.appendChild(hint);

    const controls = document.createElement("div");
    css.setClass(controls, spin_controls);

    const speedLabel = document.createElement("label");
    speedLabel.textContent = "Speed ";
    const speedSlider = document.createElement("input");
    speedSlider.type = "range";
    speedSlider.min = "1";
    speedSlider.max = "20";
    speedSlider.value = "5";
    speedLabel.appendChild(speedSlider);

    const reverseBtn = document.createElement("button");
    reverseBtn.textContent = "Reverse";

    var selector = createAnimalSelector();

    controls.appendChild(speedLabel);
    controls.appendChild(reverseBtn);
    controls.appendChild(selector);
    rootElement.appendChild(controls);

    const grid = document.createElement("div");
    css.setClass(grid, spin_grid);
    rootElement.appendChild(grid);

    let direction = 1;
    let speed = 5;
    const COUNT = 12;
    const cells = [];
    const angles = [];
    const pausedState = [];
    const offsets = [];

    for (let i = 0; i < COUNT; i++) {
        const cell = createAnimalCell(css.className(spin_cell));
        grid.appendChild(cell);
        cells.push(cell);
        angles.push(0);
        pausedState.push(false);
        offsets.push((Math.random() - 0.5) * 0.6);

        cell.addEventListener("click", () => {
            pausedState[i] = !pausedState[i];
            css.toggleClass(cell, paused, pausedState[i]);
        });
    }

    reverseBtn.addEventListener("click", () => {
        direction *= -1;
    });

    speedSlider.addEventListener("input", (e) => {
        speed = parseInt(e.target.value);
    });

    let last = 0;
    function frame(ts) {
        const dt = last ? (ts - last) / 1000 : 0;
        last = ts;
        for (let i = 0; i < COUNT; i++) {
            if (pausedState[i]) continue;
            const rate = speed * (1 + offsets[i]);
            angles[i] += direction * rate * 360 * dt;
            cells[i].style.transform = "rotate(" + angles[i] + "deg)";
        }
        requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
}
