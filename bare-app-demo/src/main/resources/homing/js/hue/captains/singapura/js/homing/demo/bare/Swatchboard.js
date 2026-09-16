// Swatchboard — the bare app (RFC 0066).
//
// appMain(root) draws every token the global palette declares as a swatch,
// the chip painted with the token itself and the caption showing what the
// browser computed for it, plus a picker over the registry's themes. Picking
// writes the preference; the CSS manager follows the store and swaps the
// palette's sheet; onThemeApplied is when the captions are re-read.
//
// Nothing here names a token or a theme — TOKENS and THEMES are generated
// from the Java that owns them (SwatchboardData). The framework's
// EsModuleWriter appends the import/export prologue — do not add import or
// export lines here.

var _owner = Object.freeze({ toString: function () { return "swatchboard"; } });

function computed(name) {
    return getComputedStyle(document.documentElement).getPropertyValue(name).trim();
}

function appMain(root) {
    var branch = domOpsParty.createBranch("swatchboard");
    branch.activate(_owner);

    css.addClass(root, sb_root);

    // ── Head: title, subtitle, picker ────────────────────────────────────────
    var head = branch.createElement("head", "header");
    css.addClass(head, sb_head);
    root.appendChild(head);

    var titles = branch.createElement("titles", "div");
    head.appendChild(titles);
    var title = branch.createElement("title", "h1");
    css.addClass(title, sb_title);
    title.textContent = "Swatchboard";
    titles.appendChild(title);
    var sub = branch.createElement("sub", "p");
    css.addClass(sub, sb_sub);
    titles.appendChild(sub);

    var picker = branch.createElement("picker", "label");
    css.addClass(picker, sb_picker);
    picker.textContent = "Theme ";
    var select = branch.createElement("select", "select");
    css.addClass(select, sb_select);
    for (var i = 0; i < THEMES.length; i++) {
        var opt = branch.createElement("opt-" + THEMES[i].slug, "option");
        opt.value = THEMES[i].slug;
        opt.textContent = THEMES[i].label;
        select.appendChild(opt);
    }
    picker.appendChild(select);
    head.appendChild(picker);

    // ── Grid: one swatch per token ───────────────────────────────────────────
    var grid = branch.createElement("grid", "div");
    css.addClass(grid, sb_grid);
    root.appendChild(grid);

    var captions = [];
    for (var t = 0; t < TOKENS.length; t++) {
        var token = TOKENS[t];
        var swatch = branch.createElement("swatch-" + token.name, "div");
        css.addClass(swatch, sb_swatch);

        var chip = branch.createElement("chip-" + token.name, "div");
        css.addClass(chip, sb_chip);
        if (token.scale) css.addClass(chip, sb_chip_scale);
        // The one custom property this page sets: the chip wears the token
        // it names. var(--sb-chip) resolves to var(--<token>) through the
        // palette, so the chip re-paints on a switch with no help from here.
        chip.style.setProperty("--sb-chip", "var(" + token.name + ")");
        swatch.appendChild(chip);

        var text = branch.createElement("text-" + token.name, "div");
        var name = branch.createElement("name-" + token.name, "div");
        css.addClass(name, sb_name);
        name.textContent = token.name;
        text.appendChild(name);
        var value = branch.createElement("value-" + token.name, "div");
        css.addClass(value, sb_value);
        text.appendChild(value);
        swatch.appendChild(text);

        grid.appendChild(swatch);
        captions.push({ name: token.name, el: value });
    }

    // ── Foot: what this page is made of ──────────────────────────────────────
    var foot = branch.createElement("foot", "footer");
    css.addClass(foot, sb_foot);
    var footText = branch.createElement("foot-text", "span");
    footText.textContent = "No studio on this classpath. ";
    foot.appendChild(footText);
    var footMuted = branch.createElement("foot-muted", "span");
    css.addClass(footMuted, sb_foot_muted);
    footMuted.textContent = "core + core-js + server + theme-color, two themes, one provision each. "
        + "The palette is the prior of this page's one CSS group; the picker writes the "
        + "preference and the CSS manager follows it.";
    foot.appendChild(footMuted);
    root.appendChild(foot);

    // ── Wiring ───────────────────────────────────────────────────────────────
    function refresh() {
        var worn = css.theme();
        var theme = null;
        for (var k = 0; k < THEMES.length; k++) if (THEMES[k].slug === worn) theme = THEMES[k];
        sub.textContent = theme ? theme.label + " — " + theme.inspiration : "";
        if (worn) select.value = worn;
        for (var c = 0; c < captions.length; c++) captions[c].el.textContent = computed(captions[c].name);
    }
    select.addEventListener("change", function () {
        PreferenceStewardInstance.remember("theme", select.value);
    });
    css.onThemeApplied(function () { refresh(); });
    refresh();
}
