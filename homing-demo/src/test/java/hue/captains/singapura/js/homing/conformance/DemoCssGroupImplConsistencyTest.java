package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.demo.es.AnimalCell;
import hue.captains.singapura.js.homing.demo.es.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.PlatformerBgm;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimals;
import hue.captains.singapura.js.homing.demo.theme.DemoCssGroupImplRegistry;

import java.util.List;

class DemoCssGroupImplConsistencyTest extends CssGroupImplConsistencyTest {

    @Override
    protected List<CssGroupImpl<?, ?>> impls() {
        return DemoCssGroupImplRegistry.ALL;
    }

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                AnimalCell.INSTANCE,
                DancingAnimals.INSTANCE,
                SpinningAnimals.INSTANCE,
                MovingAnimal.INSTANCE,
                PlatformerBgm.INSTANCE
        );
    }

    @Override
    protected String defaultThemeSlug() {
        // Studio uses the framework's HomingDefault theme. Demo no longer
        // ships its own theme registry (military themes removed when the
        // platformer moved into the studio chrome with standard --color-*
        // tokens).
        return "default";
    }
}
