package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.demo.es.JumpPhysics;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class AppHtmlGetActionTest {

    private final AppHtmlGetAction action = new AppHtmlGetAction(new QueryParamResolver());

    private static AppQuery byClass(String className) {
        return new AppQuery(null, className, null, null);
    }

    private static AppQuery byClass(String className, String theme, String locale) {
        return new AppQuery(null, className, theme, locale);
    }

    private static AppQuery byApp(String simpleName) {
        return new AppQuery(simpleName, null, null, null);
    }

    @Test
    void execute_generatesHtmlForAppModule() throws Exception {
        var query = byClass(MovingAnimal.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertEquals("text/html", result.contentType());
        assertTrue(result.body().contains("<title>Moving Animal · Homing</title>"));
        assertTrue(result.body().contains("/module?class=" + MovingAnimal.class.getCanonicalName()));
        assertTrue(result.body().contains("await import(moduleUrl)"));
        assertTrue(result.body().contains("appMain(document.getElementById(\"app\"))"));
        // RFC 0002: themes are explicit. With no ?theme= in the request, the
        // bootstrap forwards no theme (server resolves to its registered default)
        // and does NOT auto-detect from prefers-color-scheme.
        assertFalse(result.body().contains("matchMedia"), "Should not auto-detect system theme preference");
        assertFalse(result.body().contains("\"light\""), "Should not hard-code 'light' as a fallback theme");
    }

    @Test
    void execute_respectsThemeOverride() throws Exception {
        var query = byClass(MovingAnimal.class.getCanonicalName(), "dark", null);
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("\"dark\""), "Should include theme override in bootstrap");
    }

    @Test
    void execute_respectsLocaleOverride() throws Exception {
        var query = byClass(MovingAnimal.class.getCanonicalName(), null, "fr");
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("\"fr\""), "Should include locale override in bootstrap");
    }

    @Test
    void execute_failsForNonAppModule() {
        // JumpPhysics is an EsModule but not an AppModule
        var query = byClass(JumpPhysics.class.getCanonicalName());
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForNullClassName() {
        var query = byClass(null);
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForUnknownClass() {
        var query = byClass("com.nonexistent.FakeApp");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        assertThrows(ExecutionException.class, future::get);
    }

    // ---- RFC 0001 Step 07: ?app= dispatch ---------------------------------

    @Test
    void execute_resolvesByAppSimpleName() throws Exception {
        var resolver = new SimpleAppResolver(List.of(MovingAnimal.INSTANCE));
        var actionWithResolver = new AppHtmlGetAction(new QueryParamResolver(), resolver);
        var query = byApp(MovingAnimal.INSTANCE.simpleName());
        var result = actionWithResolver.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("<title>Moving Animal · Homing</title>"));
        assertTrue(result.body().contains("/module?class=" + MovingAnimal.class.getCanonicalName()));
    }

    @Test
    void execute_returnsNotFoundForUnknownAppName() {
        var resolver = new SimpleAppResolver(List.of(MovingAnimal.INSTANCE));
        var actionWithResolver = new AppHtmlGetAction(new QueryParamResolver(), resolver);
        var query = byApp("definitely-not-registered");
        var future = actionWithResolver.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsCleanlyWhenAppParamUsedWithoutResolver() {
        // Action constructed without a resolver — ?app= cannot work.
        var query = byApp("anything");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_appWinsOverClassWhenBothProvided() throws Exception {
        var resolver = new SimpleAppResolver(List.of(MovingAnimal.INSTANCE));
        var actionWithResolver = new AppHtmlGetAction(new QueryParamResolver(), resolver);
        // ?app=moving-animal &class=JumpPhysics — app wins, JumpPhysics is ignored.
        var query = new AppQuery(MovingAnimal.INSTANCE.simpleName(),
                                 JumpPhysics.class.getCanonicalName(), null, null);
        var result = actionWithResolver.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("<title>Moving Animal · Homing</title>"));
    }
}
