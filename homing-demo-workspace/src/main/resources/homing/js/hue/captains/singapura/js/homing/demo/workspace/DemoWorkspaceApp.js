// =============================================================================
// DemoWorkspaceApp — the demo's workspace on the new core, as a page: the
// shell's workspace page (WorkspacePage), handed the demo's manifest,
// DEMO_WORKSPACE, generated from its declaration in Java (DemoWorkspace) - the
// demo's ported widgets, and the monitors beside them - and its first state,
// DEMO_ARRANGEMENT (DemoArrangements): two videos side by side, laid out when
// the page writes a log that holds nothing yet.
// =============================================================================

function appMain(el, params) {
    WorkspacePage.main(el, params, DEMO_WORKSPACE, { fresh: function (p) { return nav.DemoWorkspaceApp(p); }, arrangement: DEMO_ARRANGEMENT });
}
