package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * The viewers' case board: {@code ViewerCase}, a cell with its title, what to
 * do, its scene and its checks, each with a lamp; and {@code CaseBoard}, which
 * judges the checks by where the keys go after every Escape - the member it
 * came from, read off the steward's trace, and who holds once it is routed.
 */
public record ViewerCasesModule() implements DomModule<ViewerCasesModule> {

    /** {@code new ViewerCase(branch, host, { n, title, how })}: a case of the board; {@code check(what)} adds a line with a lamp. */
    public record ViewerCase() implements BranchComponent<ViewerCasesModule> {
        @Override public String summary() { return "A case of the viewers' board: its title, what to do, the scene it holds, and its checks, each with a lamp."; }
    }

    /** {@code new CaseBoard(steward)}: judges the cases' checks by the route of every Escape and who holds after it. */
    public record CaseBoard() implements Exportable._Class<ViewerCasesModule> {}

    public static final ViewerCasesModule INSTANCE = new ViewerCasesModule();

    @Override
    public ImportsFor<ViewerCasesModule> imports() {
        return ImportsFor.<ViewerCasesModule>builder()
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_case(),
                        new GalleryStyles.ga_case_title(),
                        new GalleryStyles.ga_case_body(),
                        new GalleryStyles.ga_case_check(),
                        new GalleryStyles.ga_cell_caption(),
                        new GalleryStyles.ga_lamp(),
                        new GalleryStyles.ga_lamp_ok(),
                        new GalleryStyles.ga_lamp_off()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ViewerCasesModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new ViewerCase(), new CaseBoard()));
    }
}
