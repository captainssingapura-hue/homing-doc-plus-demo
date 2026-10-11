package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.docking.DeskModule;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;
import hue.captains.singapura.js.homing.ui.panes.TabSourceModule;

import java.util.List;

/** {@code DemoDocks}: a room of the tab controls' own - a desk, its docks in the two regions of a split grid, and a source of tabs - for the demos that open tabs to stand in. */
public record DemoDocksModule() implements DomModule<DemoDocksModule> {

    public static final DemoDocksModule INSTANCE = new DemoDocksModule();

    public record DemoDocks() implements Exportable._Class<DemoDocksModule> {}

    @Override
    public ImportsFor<DemoDocksModule> imports() {
        return ImportsFor.<DemoDocksModule>builder()
                .add(new ModuleImports<>(List.of(new DeskModule.Desk()), DeskModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridModule.SplitGrid()), SplitGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabSourceModule.TabSource()), TabSourceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<DemoDocksModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DemoDocks())); }
}
