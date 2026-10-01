// =============================================================================
// DemoWorkspaceApp — the demo's workspaces on the new core, as a page: the
// grouped workspace page (GroupedWorkspacePage), handed the demo's manifests,
// DEMO_WORKSPACES - the video room, the animal platformer, each generated from
// its declaration in Java - the groups they are filed in, DEMO_GROUPS, and
// their first states, DEMO_ARRANGEMENTS (DemoArrangements), laid out when the
// page writes a log that holds nothing yet. The route names the group, the
// anchor the workspace.
// =============================================================================

function appMain(el, params) {
    GroupedWorkspacePage.main(el, params, DEMO_WORKSPACES, DEMO_GROUPS, DEMO_ARRANGEMENTS);
}
