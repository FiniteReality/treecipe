package finitereality.treecipe.neoforge.client.event;

import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.client.aspect.AspectRenderer;
import finitereality.treecipe.neoforge.TreecipeMod;
import finitereality.treecipe.registries.TreecipeRegistries;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Defines a class which can be used to register one or more
 * {@link AspectRenderer}s to be built.
 *
 * This event is fired on the mod-specific bus, on the logical client.
 */
public final class RegisterAspectRenderersEvent
    extends Event
    implements IModBusEvent
{
    @ApiStatus.Internal
    public RegisterAspectRenderersEvent() { }

    // TODO: is synchronised bad here?
    /**
     * Registers an aspect renderer for the given aspect and sort ordering.
     *
     * @param aspect The aspect this renderer renders. May not be null.
     * @param provider The provider used to get an instance of the renderer.
     * May not be null.
     * @param before A set of aspects that must be rendered befor this aspect.
     * May be null.
     * @param after A set of aspects that must render after this aspect. May be
     * null.
     */
    public synchronized void register(final Aspect<?> aspect,
        Supplier<AspectRenderer<?>> provider,
        Set<Aspect<?>> before,
        Set<Aspect<?>> after)
    {
        Objects.requireNonNull(aspect);
        Objects.requireNonNull(provider);

        final var aspectKey = TreecipeRegistries.ASPECT.getKey(aspect);
        if (aspectKey == null) throw new IllegalStateException("Unregistered aspect: " + aspect);

        if (TreecipeMod.RegisteredAspectRenderers.putIfAbsent(aspectKey, provider) != null)
            throw new IllegalStateException(
                "A provider with that type is already registered");

        TreecipeMod.AspectRendererGraphBuilder.addVertex(aspectKey);
        if (before != null)
            before.forEach(source -> {
                final var sourceKey = TreecipeRegistries.ASPECT.getKey(source);
                if (sourceKey == null) throw new IllegalStateException("Unregistered aspect: " + source);
                TreecipeMod.AspectRendererGraphBuilder.addEdge(sourceKey, aspectKey);
            });
        if (after != null)
            after.forEach(target -> {
                final var targetKey = TreecipeRegistries.ASPECT.getKey(target);
                if (targetKey == null) throw new IllegalStateException("Unregistered aspect: " + target);
                TreecipeMod.AspectRendererGraphBuilder.addEdge(aspectKey, targetKey);
            });

        TreecipeMod.AspectRendererGraphBuilder.addEdge(TreecipeMod.AspectRendererGraphRoot, aspectKey);
    }
}
