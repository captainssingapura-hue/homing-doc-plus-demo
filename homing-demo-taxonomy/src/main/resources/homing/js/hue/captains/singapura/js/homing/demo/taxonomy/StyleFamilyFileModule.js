// =============================================================================
// StyleFamilyFile — a house style's file of one family, read as the site
// serves it (/style-families): each casting of the family as the style draws
// it, its own or Base's, a delegating casting one line naming the rule it
// takes. In dev mode the site makes the file afresh from the casting files on
// the file system on every read - an edit shows on Reload; deployed, it is
// the file the house's jar was built with.
//
// A VIEW OF THE PICK: the family is the one the picked node is in - a
// component's, a part's component's, a family itself - or, for a node above
// the families, the first family under it. The style is picked here.
//
//   new StyleFamilyFile(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

/** A style picked: the file read again. */
class _StyleFamilyStylePicked {
    constructor(widget) { this._widget = widget; }
    canHandle(type) { return type === EVENT.COMMIT; }
    handle() { this._widget._read(); }
}

/** Reload pressed: the file read again, as it is now. */
class _StyleFamilyReload {
    constructor(widget) { this._widget = widget; }
    canHandle(type) { return type === EVENT.CLICK; }
    handle() { this._widget._read(); }
}

class StyleFamilyFile extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "styleFamilyFile", "Family CSS");
        var self = this, b = this.branch;
        var bar = this.el("bar", "div", tx_line, this.body);
        this._label = new Label(b.elementMaker("style-label"), { text: "Style" });
        this._style = new Select(b.elementMaker("style"), { options: [] });
        this._reload = new PlainButton(b.elementMaker("reload"), { label: "Reload", size: -1 });
        this._said = new Caption(b.elementMaker("said"), { text: "Asking the site what it has…" });
        bar.appendChild(this._label.el);
        bar.appendChild(this._style.el);
        bar.appendChild(this._reload.el);
        this.body.appendChild(this._said.el);
        var scroll = this.el("scroll", "div", tx_scroll, this.body);
        this._file = this.el("file", "pre", tx_source, scroll);
        this._style.setOnCommit(new _StyleFamilyStylePicked(this));
        this._reload.setOnClick(new _StyleFamilyReload(this));
        this._list = null;     // what the site has: { dev, styles, families }
        this._family = null;   // the family shown
        this._asked = 0;       // the last read asked for: an older answer is dropped
        fetch("/style-families").then(function (r) { if (!r.ok) throw new Error("HTTP " + r.status); return r.json(); })
            .then(function (list) {
                if (!self.alive) return;
                self._list = list;
                self._style.options = list.styles;
                self._style.value = list.styles.length > 1 ? list.styles[1] : list.styles[0];   // the default style, past Base
                self.selected(self.picked());
            })
            .catch(function (e) { if (self.alive) self._said.text = "The site's family files could not be listed: " + e.message; });
    }

    /** The family a node is in: the family itself, one above it, else the first family under it. */
    _familyOf(id) {
        var t = this.taxonomy, families = this._list.families, n = id ? t.node(id) : null;
        if (n && n.is === "part") n = t.node(n.base);
        for (var at = n; at; at = at.parent ? t.node(at.parent) : null)
            if (families.indexOf(at.id) >= 0) return at.id;
        var under = n ? n.id : t.root().id;
        for (var i = 0; i < families.length; i++)
            for (var up = t.node(families[i]); up; up = up.parent ? t.node(up.parent) : null)
                if (up.id === under) return families[i];
        return families[0];
    }

    selected(id) {
        if (!this._list) return;
        var family = this._familyOf(id);
        if (family === this._family) return;
        this._family = family;
        this._read();
    }

    /** The picked style's file of the family, as the site serves it now. */
    _read() {
        if (!this._list || !this._family) return;
        var self = this, style = this._style.value, family = this._family, asked = ++this._asked;
        var whence = this._list.dev ? "made afresh from the casting files on the file system (dev mode)" : "as built into the house's jar";
        this._said.text = "The " + family + " family in " + style + ": reading…";
        fetch("/style-families?style=" + encodeURIComponent(style) + "&family=" + encodeURIComponent(family))
            .then(function (r) { return r.text().then(function (text) { if (!r.ok) throw new Error(text || "HTTP " + r.status); return text; }); })
            .then(function (text) {
                if (!self.alive || asked !== self._asked) return;
                self._file.textContent = text;
                self._said.text = "The " + family + " family in " + style + ", " + whence + ".";
            })
            .catch(function (e) {
                if (!self.alive || asked !== self._asked) return;
                self._file.textContent = "Not read: " + e.message;
                self._said.text = "The " + family + " family in " + style + " could not be read: " + e.message;
            });
    }
}
