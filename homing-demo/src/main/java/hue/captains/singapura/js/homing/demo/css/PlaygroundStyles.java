package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

/**
 * Styles for the Animal Platformer demo. Theme-token native — every colour
 * routes through the framework's standard {@code --color-*} tokens
 * (HomingDefault / HomingForest / HomingSunset / HomingBauhaus). No demo-local
 * theme registry, no per-theme {@code CssGroupImpl}. Vehicle silhouettes and
 * the in-page theme switcher used to live here; both were dropped when the
 * Site-in-a-Jar POC settled on the studio chrome's theme picker as the single
 * source of theming.
 */
public record PlaygroundStyles() implements CssGroup<PlaygroundStyles> {
    public static final PlaygroundStyles INSTANCE = new PlaygroundStyles();

    public record pg_title() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                font-size: 1.6rem;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 3px;
                color: var(--color-text-primary, #222);
                margin: 0 0 4px 0;
                """;
        }
    }
    public record pg_hint() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                color: var(--color-text-muted, #666);
                font-style: italic;
                font-size: 0.9rem;
                margin: 12px 0 16px 0;
                """;
        }
    }
    public record pg_controls() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: 16px;
                margin-bottom: 16px;
                flex-wrap: wrap;
                color: var(--color-text-primary, #222);
                """;
        }
    }
    public record pg_size_display() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                font-size: 0.85rem;
                color: var(--color-text-link, #0090b0);
                font-weight: 600;
                min-width: 80px;
                """;
        }
    }
    public record pg_playground() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: relative;
                overflow: hidden;
                background: var(--color-surface-inverted, #16213e);
                border: 1px solid var(--color-border, #ccc);
                border-radius: 8px;
                box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
                """;
        }
    }
    public record pg_sky() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                z-index: 1;
                background:
                    radial-gradient(ellipse 110px 55px at 38% 45%, rgba(255,255,255,0.18) 0%, transparent 100%),
                    radial-gradient(ellipse 85px 50px at 65% 50%, rgba(255,255,255,0.14) 0%, transparent 100%),
                    radial-gradient(ellipse 75px 42px at 88% 48%, rgba(255,255,255,0.16) 0%, transparent 100%);
                """;
        }
    }
    public record pg_world() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                """;
        }
    }
    public record pg_animal() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                transform-origin: center;
                will-change: transform, left, top;
                z-index: 5;
                """;
        }
    }
    /**
     * Platform — the visible standable rectangle. Theme accent colour as
     * background, slightly emphasised top edge so the landing line reads
     * clearly against the inverted-surface playground.
     */
    public record pg_platform() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                transition: filter 0.15s, background 0.15s;
                background: var(--color-accent, #4a7aa0);
                border-radius: 3px;
                border-top: 2px solid var(--color-accent-emphasis, #6a9ac0);
                """;
        }
    }
    /**
     * Active platform — the one the animal is currently standing on. Brightens
     * via a drop-shadow glow in the theme's accent-on contrast.
     */
    public record pg_platform_active() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                filter: drop-shadow(0 0 8px var(--color-accent-on, rgba(255, 230, 120, 0.85)));
                """;
        }
    }
    public record pg_lava() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
                background: linear-gradient(0deg, #6a1a1a 0%, #b04030 60%, rgba(176, 64, 48, 0.4) 100%);
                z-index: 2;
                """;
        }
    }
    public record pg_score() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                top: 8px;
                right: 12px;
                font-size: 1rem;
                font-weight: 700;
                color: var(--color-text-on-inverted, #fff);
                z-index: 3;
                """;
        }
    }
    public record pg_gameover() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                inset: 0;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                background: rgba(0, 0, 0, 0.7);
                z-index: 10;
                color: #fff;
                """;
        }
    }
    public record pg_final_score() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                font-size: 1.2rem;
                color: #fff;
                margin: 0 0 16px 0;
                """;
        }
    }

    @Override
    public List<CssClass<PlaygroundStyles>> cssClasses() {
        return List.of(
                new pg_title(), new pg_hint(), new pg_controls(), new pg_size_display(),
                new pg_playground(), new pg_sky(), new pg_world(), new pg_animal(),
                new pg_platform(), new pg_platform_active(),
                new pg_lava(), new pg_score(),
                new pg_gameover(), new pg_final_score()
        );
    }

    @Override
    public CssImportsFor<PlaygroundStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
