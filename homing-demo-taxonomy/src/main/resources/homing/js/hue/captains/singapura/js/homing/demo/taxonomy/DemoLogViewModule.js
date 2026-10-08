// =============================================================================
// ComponentDemoLog — what the demos did, newest first: the demo log's view.
// The demo panel clears it as it mounts a demo; its own Clear empties this
// view alone.
//
//   new ComponentDemoLog(container, params)   params: none
//   (the rest is a ComponentLogView's)
// =============================================================================

class ComponentDemoLog extends ComponentLogView {
    constructor(container, params) { super(container, "component-demo-log", "Demo log", COMPONENT_DEMO_LOG); }
}
