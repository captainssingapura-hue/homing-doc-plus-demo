// =============================================================================
// TaxonomyWorkbenchApp — the taxonomy workbench's page: the grouped workspace
// page over the workbench, its manifest, where it is filed and how it is
// arranged the first time.
// =============================================================================

function appMain(el, params) {
    GroupedWorkspacePage.main(el, params, TAXONOMY_WORKSPACES, TAXONOMY_GROUPS, TAXONOMY_ARRANGEMENTS);
}
