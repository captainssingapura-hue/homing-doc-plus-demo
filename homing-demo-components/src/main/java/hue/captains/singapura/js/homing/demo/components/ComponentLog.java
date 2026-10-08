package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * What a component-log party carries: lines of words, the newest last, and the commands the
 * widget that writes them gives - {@link ClearLog}. A member does - {@link Note}, {@link ClearLog},
 * {@link HistoryRequested}; the party says - {@link Noted} and {@link Cleared} to every member,
 * {@link History} to one that asks. Two types of one vocabulary: {@link #DEMO}, what the demos
 * did, written by the demo panel and the demos it holds; {@link #CONTROL}, what was set, written
 * by the control panel.
 */
public sealed interface ComponentLog {

    /** A line, written. */
    record Note(String words) implements ComponentLog {}

    /** The command of the widget that writes the log: the lines gone. */
    record ClearLog() implements ComponentLog {}

    /** A member asks for the lines kept - one that joins late - and is answered alone. */
    record HistoryRequested() implements ComponentLog {}

    /** The party says: a line was written, this one in order. */
    record Noted(int seq, String words) implements ComponentLog {}

    /** The party says: the lines are gone. */
    record Cleared() implements ComponentLog {}

    /** The party says: the lines kept, the oldest first. */
    record History(List<Line> lines) implements ComponentLog {}

    /** A line, as {@link History} holds it. */
    record Line(int seq, String words) {}

    /** What the demos did: {@code component-demo-log}, served as {@code COMPONENT_DEMO_LOG}. */
    PartyType<ComponentLog> DEMO = new PartyType<>("component-demo-log", ComponentLog.class)
            .servedFrom(new ModuleImports<>(List.of(new ComponentDemoLogModule.COMPONENT_DEMO_LOG()), ComponentDemoLogModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new ComponentLogSecretaryModule.ComponentLogSecretary()), ComponentLogSecretaryModule.INSTANCE));

    /** What was set: {@code component-control-log}, served as {@code COMPONENT_CONTROL_LOG}. */
    PartyType<ComponentLog> CONTROL = new PartyType<>("component-control-log", ComponentLog.class)
            .servedFrom(new ModuleImports<>(List.of(new ComponentControlLogModule.COMPONENT_CONTROL_LOG()), ComponentControlLogModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new ComponentLogSecretaryModule.ComponentLogSecretary()), ComponentLogSecretaryModule.INSTANCE));
}
