package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.demo.es.*;

import java.util.List;
import java.util.Set;

class DemoHrefConformanceTest extends HrefConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                AnimalCell.INSTANCE,
                DancingAnimals.INSTANCE,
                SpinningAnimals.INSTANCE,
                MovingAnimal.INSTANCE,
                ExtrudedTurtleDemo.INSTANCE,
                DecomposedSvgDemo.INSTANCE,
                ExtrudedSvgDemo.INSTANCE,
                PlatformerBgm.INSTANCE
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of();
    }
}
