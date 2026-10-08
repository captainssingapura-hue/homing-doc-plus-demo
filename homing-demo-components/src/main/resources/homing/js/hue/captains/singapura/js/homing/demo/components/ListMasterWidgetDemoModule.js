// =============================================================================
// ListMasterWidgetDemo — the house's list master in action: a site's settings,
// listed by where they belong, depth said as indentation, so the user picks one
// to see beside the list. A press chooses a row; the keys the demo holds - the
// arrows, Home and End - move the row in force, and choosing is selecting.
// Each choice is said, with where it sits. It is controlled by nothing more
// than itself.
//
//   new ListMasterWidgetDemo(container, { leaf })   leaf: "list-master-widget"
//   (the rest is a ComponentDemo's)
// =============================================================================

var _SETTINGS = Object.freeze({ segment: "settings", children: [
    { segment: "appearance", children: [{ segment: "theme" }, { segment: "size" }] },
    { segment: "editor", children: [{ segment: "font" }, { segment: "wrap" }] },
    { segment: "language" }
] });

var _SETTING_LABELS = Object.freeze({
    "settings": "All settings", "settings/appearance": "Appearance", "settings/appearance/theme": "Theme",
    "settings/appearance/size": "Size", "settings/editor": "Editor", "settings/editor/font": "Font",
    "settings/editor/wrap": "Wrap lines", "settings/language": "Language"
});

class ListMasterWidgetDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "list-master-widget-demo", params);
        var self = this;
        this._list = new ListMasterWidget(this.branch.createBranch("list"), { tree: _SETTINGS, labels: _SETTING_LABELS });
        this._list.onSelect(function (path) { self.say((_SETTING_LABELS[path] || path) + " chosen - " + path); });
        this._list.setActive(true);
        this.stage.appendChild(this._list.root);
        this.intro = "press a setting, or press the list and move with the arrows, Home and End";
    }

    key(ev) { return this._list.key(ev); }

    disposed() { try { this._list.dispose(); } catch (e) {} }
}
