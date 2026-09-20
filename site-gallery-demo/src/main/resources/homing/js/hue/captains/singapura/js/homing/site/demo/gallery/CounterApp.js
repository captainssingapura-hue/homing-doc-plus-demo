// =============================================================================
// CounterApp — a counter that starts where its params say. appMain(el, params)
// receives the stamped params the server decoded through the app's codec:
// { start: "7" } — strings, as they travel on the wire. Its buttons are the
// shared Button builder; the page owns the row they sit in.
// =============================================================================

const _owner = Object.freeze({ toString: () => "counter" });

class CounterWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var start = params && params.start ? parseInt(params.start, 10) : 0;
        if (isNaN(start)) start = 0;
        var value = start;

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "typed params";
        el.appendChild(kicker);

        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Counter";
        el.appendChild(title);

        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "Started at " + start + ", which the server stamped into the page from the binding "
            + "and the address through the app's own codec.";
        el.appendChild(lede);

        var count = branch.createElement("count", "div");
        css.addClass(count, ga_count);
        count.setAttribute("aria-live", "polite");
        el.appendChild(count);

        function draw() { count.textContent = String(value); }

        // The three buttons are the shared Button builder: primary for the two
        // that count, plain for the way back.
        var buttons = branch.createElement("buttons", "div");
        css.addClass(buttons, ga_buttons);
        buttons.appendChild(new Button(branch.createElement("minus", Button.TAG), { label: "−",     onClick: function () { value -= 1; draw(); } }).el);
        buttons.appendChild(new Button(branch.createElement("plus", Button.TAG),  { label: "+",     onClick: function () { value += 1; draw(); } }).el);
        buttons.appendChild(new Button(branch.createElement("reset", Button.TAG), { label: "reset", kind: "plain", onClick: function () { value = start; draw(); } }).el);
        el.appendChild(buttons);
        draw();
        this.root = el;
    }

    dispose() {}
}

function appMain(el, params) {
    el.appendChild(new CounterWidget(domOpsParty.createBranch("counter"), params).root);
}
