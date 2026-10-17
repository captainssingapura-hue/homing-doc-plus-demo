package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.CaptionModule;
import hue.captains.singapura.js.homing.ui.elements.LabelModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SelectModule;
import hue.captains.singapura.js.homing.ui.look.StatesAndEventsModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/**
 * {@code StyleFamilyFile}: a house style's file of one family, as the site serves it from {@code /style-families} (RFC
 * 0067, appendix The Style Jars) - made afresh from the casting files on the file system in dev mode, deployed the
 * file the house's jar was built with. A view of the pick: the family the picked node is in; the style picked here.
 */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 9,
        reason = "It places its controls and writes the file's text into an element of its own: the house has no code block component yet (HouseRegions.CodeBlock is realized by nothing), and it waits for the designed containers to draw only components.")
public record StyleFamilyFileModule() implements DomModule<StyleFamilyFileModule> {

    public static final StyleFamilyFileModule INSTANCE = new StyleFamilyFileModule();

    public record StyleFamilyFile() implements SelfContainedWidget<StyleFamilyFileModule>, NeedKeyboard {
        @Override public String summary() {
            return "A house style's file of one family, as the site serves it: each casting of the family as the style draws it, its own or Base's,"
                 + " a delegating casting one line naming the rule it takes. The family the picked node is in; the style picked here; Reload reads it again.";
        }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back")); }
    }

    @Override
    public ImportsFor<StyleFamilyFileModule> imports() {
        return ImportsFor.<StyleFamilyFileModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LabelModule.Label()), LabelModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SelectModule.Select()), SelectModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CaptionModule.Caption()), CaptionModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_line(), new TaxonomyStyles.tx_scroll(), new TaxonomyStyles.tx_source()),
                        TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<StyleFamilyFileModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new StyleFamilyFile())); }
}
