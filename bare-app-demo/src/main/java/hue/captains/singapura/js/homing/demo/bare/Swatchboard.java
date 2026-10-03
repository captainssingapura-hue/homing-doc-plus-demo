package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.server.PreferenceSteward;

import java.util.List;

/**
 * RFC 0066 — the bare app. One page, no studio: every colour word the board
 * reads, drawn as a swatch with the value the browser actually computed for
 * it, and a picker that switches the palette without a reload.
 *
 * <p>What it proves: {@code core + core-js + server + design-core}, a design
 * and its palettes, and a {@code DesignRegistry} are the whole of what a
 * served JS app needs to be designed. The classes wear design words and the
 * registry's renderer binds them; the switch goes through the preference
 * steward and the CSS manager follows it, exactly as the studio's picker
 * does — the studio just is not here.</p>
 */
public record Swatchboard() implements AppModule<AppModule._None, Swatchboard> {

    public static final Swatchboard INSTANCE = new Swatchboard();

    record appMain() implements AppModule._AppMain<AppModule._None, Swatchboard> {}

    @Override public String title() { return "Swatchboard"; }
    @Override public String simpleName() { return "swatchboard"; }

    @Override
    public ImportsFor<Swatchboard> imports() {
        return ImportsFor.<Swatchboard>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SwatchboardData.TOKENS(),
                        new SwatchboardData.THEMES()
                ), SwatchboardData.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new PreferenceSteward.PreferenceStewardInstance(),
                        new PreferenceSteward.PreferenceViewInstance()
                ), PreferenceSteward.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SwatchboardStyles.sb_root(),
                        new SwatchboardStyles.sb_head(),
                        new SwatchboardStyles.sb_title(),
                        new SwatchboardStyles.sb_sub(),
                        new SwatchboardStyles.sb_picker(),
                        new SwatchboardStyles.sb_select(),
                        new SwatchboardStyles.sb_grid(),
                        new SwatchboardStyles.sb_swatch(),
                        new SwatchboardStyles.sb_chip(),
                        new SwatchboardStyles.sb_name(),
                        new SwatchboardStyles.sb_value(),
                        new SwatchboardStyles.sb_foot(),
                        new SwatchboardStyles.sb_foot_muted(),
                        new SwatchboardStyles.sb_link()
                ), SwatchboardStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Swatchboard> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
