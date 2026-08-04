package de.ambertation.wover.datagen.impl;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverAutoProvider;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.entrypoint.LibWoverDatagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-logic JUnit tests for the auto-provider <b>redirector</b> bookkeeping in
 * {@link PackBuilderImpl#instantiateAutoProvider(WoverDataProvider)} /
 * {@link PackBuilderImpl#addProviderWithRedirect(WoverDataProvider)}. They only touch the value-type
 * {@link net.minecraft.resources.ResourceLocation}-free path (no {@code DataProvider} is ever run), so
 * they need no Minecraft bootstrap and run via {@code ./gradlew :wover-datagen-api:test}.
 * <p>
 * This mechanism is the gate that decides <em>which</em> registered providers actually reach a Datapack.
 * A {@link WoverAutoProvider.WithRedirect} registered as an auto-provider is applied, in registration
 * order, to every subsequently added provider and may wrap it, replace it, or suppress it entirely by
 * returning {@code null}. The whole feature (one mod filtering/replacing another mod's providers) rests on
 * three invariants that a version port could silently break while still compiling:
 * <ul>
 *     <li>a {@code WithRedirect} is itself added to the provider list <b>and</b> registered as a redirector;</li>
 *     <li>a later provider is redirected by every already-registered redirector, composed in order,
 *     and a {@code null} result removes it from the pack;</li>
 *     <li>the ordering assumption documented on {@code instantiateAutoProvider} holds: a provider added
 *     <em>before</em> a redirector is not retroactively redirected.</li>
 * </ul>
 * Each test below fails loudly if one of those invariants regresses.
 */
class PackBuilderRedirectTest {
    // A minimal concrete PackBuilderImpl that exposes the two protected accumulator lists so the tests can
    // assert on the exact bookkeeping outcome. modCore() only feeds the debug logger inside the impl.
    private static final class TestPack extends PackBuilderImpl {
        @Override
        protected ModCore modCore() {
            return LibWoverDatagen.C;
        }

        List<WoverDataProvider<?>> providers() {
            return providerFactories;
        }

        List<WoverAutoProvider.WithRedirect> registeredRedirectors() {
            return redirectors;
        }
    }

    // A plain provider that never produces a real DataProvider (the tests only exercise registration).
    private static final class NamedProvider implements WoverDataProvider<DataProvider> {
        final String name;

        NamedProvider(String name) {
            this.name = name;
        }

        @Override
        public DataProvider getProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            return null;
        }

        @Override
        public String toString() {
            return "NamedProvider[" + name + "]";
        }
    }

    // A redirector that replaces every incoming provider with a fixed replacement instance.
    private static final class ReplacingRedirector implements WoverDataProvider<DataProvider>, WoverAutoProvider.WithRedirect {
        final WoverDataProvider<?> replacement;

        ReplacingRedirector(WoverDataProvider<?> replacement) {
            this.replacement = replacement;
        }

        @Override
        public DataProvider getProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends DataProvider> WoverDataProvider<T> redirect(WoverDataProvider<T> provider) {
            return (WoverDataProvider<T>) replacement;
        }
    }

    // A redirector that suppresses every incoming provider by returning null.
    private static final class SuppressingRedirector implements WoverDataProvider<DataProvider>, WoverAutoProvider.WithRedirect {
        @Override
        public DataProvider getProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            return null;
        }

        @Override
        public <T extends DataProvider> WoverDataProvider<T> redirect(WoverDataProvider<T> provider) {
            return null;
        }
    }

    // A redirector that wraps the incoming provider, recording its tag so composition order is observable.
    private static final class WrappingRedirector implements WoverDataProvider<DataProvider>, WoverAutoProvider.WithRedirect {
        final String tag;

        WrappingRedirector(String tag) {
            this.tag = tag;
        }

        @Override
        public DataProvider getProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            return null;
        }

        @Override
        public <T extends DataProvider> WoverDataProvider<T> redirect(WoverDataProvider<T> provider) {
            return new Wrapped<>(tag, provider);
        }
    }

    private static final class Wrapped<T extends DataProvider> implements WoverDataProvider<T> {
        final String tag;
        final WoverDataProvider<T> inner;

        Wrapped(String tag, WoverDataProvider<T> inner) {
            this.tag = tag;
            this.inner = inner;
        }

        @Override
        public T getProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            return inner.getProvider(output, registries);
        }
    }

    @Test
    void withRedirectIsBothRegisteredAsRedirectorAndAddedAsProvider() {
        final TestPack pack = new TestPack();
        final ReplacingRedirector redirector = new ReplacingRedirector(new NamedProvider("replacement"));

        pack.instantiateAutoProvider(redirector);

        // A WithRedirect must land in *both* lists: it participates in serialization and it intercepts later ones.
        assertEquals(1, pack.registeredRedirectors().size(), "the WithRedirect must be registered as a redirector");
        assertSame(redirector, pack.registeredRedirectors().get(0));
        assertEquals(List.of(redirector), pack.providers(), "the WithRedirect must also be added as a provider");
    }

    @Test
    void aLaterProviderIsReplacedByAnAlreadyRegisteredRedirector() {
        final TestPack pack = new TestPack();
        final NamedProvider replacement = new NamedProvider("replacement");
        final ReplacingRedirector redirector = new ReplacingRedirector(replacement);
        final NamedProvider original = new NamedProvider("original");

        // Redirector first (as the impl assumes), then the normal provider it should intercept.
        pack.instantiateAutoProvider(redirector);
        pack.instantiateAutoProvider(original);

        // providerFactories = [redirector, replacement]. The original must have been swapped out entirely.
        assertEquals(2, pack.providers().size());
        assertTrue(pack.providers().contains(replacement), "the redirector's replacement must be added");
        assertFalse(pack.providers().contains(original), "the original provider must not survive the redirect");
    }

    @Test
    void aRedirectorReturningNullSuppressesTheProvider() {
        final TestPack pack = new TestPack();
        final SuppressingRedirector redirector = new SuppressingRedirector();
        final NamedProvider original = new NamedProvider("original");

        pack.instantiateAutoProvider(redirector);
        pack.instantiateAutoProvider(original);

        // Only the redirector itself remains; the suppressed provider is dropped from the pack.
        assertEquals(List.of(redirector), pack.providers(), "a null redirect result must drop the provider");
    }

    @Test
    void multipleRedirectorsComposeInRegistrationOrder() {
        final TestPack pack = new TestPack();
        final WrappingRedirector first = new WrappingRedirector("A");
        final WrappingRedirector second = new WrappingRedirector("B");
        final NamedProvider original = new NamedProvider("original");

        pack.instantiateAutoProvider(first);
        pack.instantiateAutoProvider(second);
        pack.instantiateAutoProvider(original);

        // reduce starts from `original` and applies A then B: result = Wrapped("B", Wrapped("A", original)).
        final WoverDataProvider<?> added = pack.providers().get(pack.providers().size() - 1);
        assertInstanceOf(Wrapped.class, added);
        final Wrapped<?> outer = (Wrapped<?>) added;
        assertEquals("B", outer.tag, "the last-registered redirector must be the outermost wrapper");
        assertInstanceOf(Wrapped.class, outer.inner);
        final Wrapped<?> inner = (Wrapped<?>) outer.inner;
        assertEquals("A", inner.tag, "the first-registered redirector must be the innermost wrapper");
        assertSame(original, inner.inner, "the original provider must sit at the center of the wrapper chain");
    }

    @Test
    void aProviderAddedBeforeItsRedirectorIsNotRetroactivelyRedirected() {
        final TestPack pack = new TestPack();
        final NamedProvider original = new NamedProvider("original");
        final ReplacingRedirector redirector = new ReplacingRedirector(new NamedProvider("replacement"));

        // Violate the documented "redirectors first" ordering on purpose: provider before redirector.
        pack.instantiateAutoProvider(original);
        pack.instantiateAutoProvider(redirector);

        // The pre-existing provider must be left untouched; only later providers are redirected.
        assertTrue(pack.providers().contains(original), "an already-added provider must not be redirected after the fact");
        assertEquals(List.of(original, redirector), pack.providers());
    }

    @Test
    void nullAutoProviderIsIgnored() {
        final TestPack pack = new TestPack();

        pack.instantiateAutoProvider(null);

        assertTrue(pack.providers().isEmpty(), "a null auto-provider must be a no-op");
        assertTrue(pack.registeredRedirectors().isEmpty());
    }
}
