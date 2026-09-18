// TableDemoModule.js
//
// Two plain HTML tables, and two routes to a lifted row or cell:
//
//   mode "state"  the component flips an ATTRIBUTE — aria-selected on click,
//                 aria-current under the keyboard cursor, data-highlighted on
//                 every cell that shares the hovered cell's value — and the
//                 design's word at that slot is the look. One class per
//                 element; the design decides what selected is.
//   mode "class"  the component decides the look itself: it applies its own
//                 lifted class beside the base (tdm_row_lifted beside st_tr,
//                 tdm_cell_lifted beside tdm_cell), wearing the Selected pairs.
//                 The sheet's precedence — a state semantic after a layer's —
//                 is what makes "beside" win, every start, every design.
//
// Under Brutalism a lifted cell sits up on its offset shadow; a lifted row
// casts one by filter, since a browser paints no shadow on a table row.
//
// The framework's EsModuleWriter appends the import/export prologue from the
// matching Java TableDemoModule declaration — do not add import/export lines.

var ROWS = [
    ["CssClassManager", "core",      "0", "done"],
    ["ThemePicker",     "studio",    "2", "in progress"],
    ["WorkspaceLayout", "workspace", "0", "done"],
    ["PartyMonitor",    "shell",     "5", "blocked"],
    ["TreeRenderer",    "core",      "1", "in progress"],
    ["RelationGrid",    "grid",      "3", "blocked"]
];
var HEAD = ["Module", "Owner", "Findings", "Status"];

/** Build the demo into `host`; `mode` is "state" or "class". */
function mountTableDemo(branch, host, mode) {
    var byClass = (mode === "class");

    var note = branch.createElement("note", "div");
    css.setClass(note, tdm_note);
    note.textContent = byClass
        ? "Class route: the component applies its own lifted class beside the base. Click a row or a cell to toggle it."
        : "State route: the component flips aria-selected (click), aria-current (arrow keys) and data-highlighted (hover a cell) — the design says what each looks like.";
    host.appendChild(note);

    host.appendChild(heading(branch, "h1", "Rows"));
    host.appendChild(rowsTable(branch, byClass));
    host.appendChild(heading(branch, "h2", "Cells"));
    host.appendChild(cellsTable(branch, byClass));
}

function heading(branch, id, text) {
    var h = branch.createElement(id, "div");
    css.setClass(h, tdm_heading);
    h.textContent = text;
    return h;
}

function table(branch, id) {
    var t = branch.createElement(id, "table");
    css.setClass(t, st_table);
    var thead = branch.createElement(id + "h", "thead");
    css.setClass(thead, st_thead);
    var hr = branch.createElement(id + "hr", "tr");
    HEAD.forEach(function (name, i) {
        var th = branch.createElement(id + "th" + i, "th");
        css.setClass(th, st_th);
        th.textContent = name;
        hr.appendChild(th);
    });
    thead.appendChild(hr);
    t.appendChild(thead);
    return t;
}

/** Rows are the interactive thing: st_tr on every row, st_td on every cell. */
function rowsTable(branch, byClass) {
    var t = table(branch, "rows");
    var tbody = branch.createElement("rowsb", "tbody");
    var rows = [];
    ROWS.forEach(function (r, i) {
        var tr = branch.createElement("row" + i, "tr");
        css.setClass(tr, st_tr);
        tr.tabIndex = 0;
        r.forEach(function (v, j) {
            var td = branch.createElement("row" + i + "c" + j, "td");
            css.setClass(td, st_td);
            td.textContent = v;
            tr.appendChild(td);
        });
        tr.addEventListener("click", function () { selectRow(rows, tr, byClass); });
        tbody.appendChild(tr);
        rows.push(tr);
    });
    // The keyboard cursor: aria-current follows focus; Enter selects.
    tbody.addEventListener("keydown", function (ev) {
        var at = rows.indexOf(document.activeElement);
        if (at < 0) return;
        if (ev.key === "ArrowDown" || ev.key === "ArrowUp") {
            var next = rows[Math.min(rows.length - 1, Math.max(0, at + (ev.key === "ArrowDown" ? 1 : -1)))];
            rows.forEach(function (r) { r.removeAttribute("aria-current"); });
            next.setAttribute("aria-current", "true");
            next.focus();
            ev.preventDefault();
        } else if (ev.key === "Enter" || ev.key === " ") {
            selectRow(rows, rows[at], byClass);
            ev.preventDefault();
        }
    });
    t.appendChild(tbody);
    return t;
}

function selectRow(rows, tr, byClass) {
    if (byClass) {
        var on = css.hasClass(tr, tdm_row_lifted);
        rows.forEach(function (r) { css.removeClass(r, tdm_row_lifted); });
        if (!on) css.addClass(tr, tdm_row_lifted);
        return;
    }
    var was = tr.getAttribute("aria-selected") === "true";
    rows.forEach(function (r) { r.removeAttribute("aria-selected"); });
    if (!was) tr.setAttribute("aria-selected", "true");
}

/** Cells are the interactive thing: plain rows, tdm_cell on every cell. */
function cellsTable(branch, byClass) {
    var t = table(branch, "cells");
    var tbody = branch.createElement("cellsb", "tbody");
    var cells = [];
    ROWS.forEach(function (r, i) {
        var tr = branch.createElement("crow" + i, "tr");
        r.forEach(function (v, j) {
            var td = branch.createElement("crow" + i + "c" + j, "td");
            css.setClass(td, tdm_cell);
            td.textContent = v;
            td.tabIndex = 0;
            td.addEventListener("click", function () { selectCell(cells, td, byClass); });
            tr.appendChild(td);
            cells.push(td);
        });
        tbody.appendChild(tr);
    });
    if (!byClass) {
        // Highlight: every cell sharing the hovered cell's value is lit — a
        // state no aria word names, so the slot reads data-highlighted.
        tbody.addEventListener("mouseover", function (ev) {
            var td = ev.target.closest("td");
            if (!td) return;
            cells.forEach(function (c) {
                if (c.textContent === td.textContent && c !== td) c.setAttribute("data-highlighted", "");
                else c.removeAttribute("data-highlighted");
            });
        });
        tbody.addEventListener("mouseleave", function () {
            cells.forEach(function (c) { c.removeAttribute("data-highlighted"); });
        });
    }
    t.appendChild(tbody);
    return t;
}

function selectCell(cells, td, byClass) {
    if (byClass) {
        var on = css.hasClass(td, tdm_cell_lifted);
        cells.forEach(function (c) { css.removeClass(c, tdm_cell_lifted); });
        if (!on) css.addClass(td, tdm_cell_lifted);
        return;
    }
    var was = td.getAttribute("aria-selected") === "true";
    cells.forEach(function (c) { c.removeAttribute("aria-selected"); });
    if (!was) td.setAttribute("aria-selected", "true");
}
