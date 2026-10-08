// =============================================================================
// ComponentControlLog — what was set, done and asked, newest first: the control
// log's view. The control panel clears it as the control type changes; its own
// Clear empties this view alone.
//
//   new ComponentControlLog(container, params)   params: none
//   (the rest is a ComponentLogView's)
// =============================================================================

class ComponentControlLog extends ComponentLogView {
    constructor(container, params) { super(container, "component-control-log", "Control log", COMPONENT_CONTROL_LOG); }
}
