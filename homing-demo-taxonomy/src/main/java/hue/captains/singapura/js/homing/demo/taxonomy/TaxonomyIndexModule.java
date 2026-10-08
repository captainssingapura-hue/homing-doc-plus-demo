package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/** {@code TaxonomyIndex}: the taxonomy as the widgets ask about it, read off {@code TAXONOMY}; pure. */
public record TaxonomyIndexModule() implements EsModule<TaxonomyIndexModule> {

    public static final TaxonomyIndexModule INSTANCE = new TaxonomyIndexModule();

    public record TaxonomyIndex() implements Exportable._Class<TaxonomyIndexModule> {}

    @Override
    public ImportsFor<TaxonomyIndexModule> imports() {
        return ImportsFor.<TaxonomyIndexModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyDataModule.TAXONOMY()), TaxonomyDataModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TaxonomyIndexModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new TaxonomyIndex())); }
}
