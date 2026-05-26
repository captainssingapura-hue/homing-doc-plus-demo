package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The Secretary for the {@code AnimalsParty} — the {@code AnimalsPlayground}
 * workspace's first downstream ActorParty. Extracted as a standalone JS
 * module per the Diligent Secretaries doctrine's third pillar: pure
 * behaviour functions must be importable in isolation so a GraalVM-backed
 * JUnit harness can exercise them without a DOM.
 *
 * <p>Exports a single {@code AnimalsSecretary} JS object with two members:
 * {@code initial} (starting state record) and {@code behavior} (the pure
 * {@code (state, envelope) → Step} routing function). The
 * {@link AnimalsPlaygroundChrome} imports both and passes them to the
 * {@code Party} constructor.</p>
 *
 * <h2>State shape (Diligent Secretaries enrichment)</h2>
 *
 * <pre>{@code
 * {
 *     selectedAnimal: String,             // "-1" (Random) | concrete animal id
 *     lastChangedBy : AgentId | null,     // provenance of most recent change
 *     recentUnknown : [{ kind, from }]    // last N unrecognised messages (bounded at 10)
 * }
 * }</pre>
 *
 * <h2>Message kinds</h2>
 *
 * <ul>
 *   <li>{@code AnimalSelectionRequested(animal)} — record selection, broadcast
 *       {@code AnimalChanged(animal)} to all members.</li>
 *   <li>{@code CurrentAnimalRequested} — late-join sync. If
 *       {@code selectedAnimal} is non-default, reply to the asking member via
 *       {@code SendToMember(AnimalChanged)}; if still {@code '-1'}, no-op
 *       (asking widget keeps its own per-instance default).</li>
 *   <li>Anything else — append to {@code recentUnknown} (bounded at 10);
 *       no action emitted.</li>
 * </ul>
 *
 * @since RFC 0028 cycle 6 phase 1 — Secretary-as-module refactor for JS test harness
 */
public record AnimalsSecretaryModule() implements DomModule<AnimalsSecretaryModule> {

    /** The single export — a JS object with {@code initial} and {@code behavior} members. */
    public record AnimalsSecretary() implements Exportable._Constant<AnimalsSecretaryModule> {}

    public static final AnimalsSecretaryModule INSTANCE = new AnimalsSecretaryModule();

    @Override
    public ImportsFor<AnimalsSecretaryModule> imports() {
        return ImportsFor.<AnimalsSecretaryModule>builder().build();
    }

    @Override
    public ExportsOf<AnimalsSecretaryModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new AnimalsSecretary()));
    }
}
