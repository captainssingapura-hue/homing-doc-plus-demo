package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;

/**
 * The demo's conformance studio: the framework's conformance workbench over {@link
 * DemoConformance#TOP_LEVEL}, saying of their rules what the build exported. The downstream half
 * of the studio story: the workbench is the studio repo's, the crates and the report are the
 * demo's - and the report carries the demo's own rule set, so the conformance pane shows the
 * platformer's play loop under the game-loop type.
 *
 * <p>{@code mvn -o -pl homing-demo-conformance exec:java}, on 8091 unless {@code
 * -Dconformance.port} says otherwise.</p>
 */
public final class DemoConformanceStudioServer {

    private DemoConformanceStudioServer() {}

    public static void main(String[] args) {
        int port = Integer.getInteger("conformance.port", 8091);
        ConformanceStudio.of("Homing · demo conformance", DemoConformance.TOP_LEVEL,
                ConformanceStudio.exportedReport(DemoConformanceStudioServer.class)).serve(port);
    }
}
