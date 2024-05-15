package finitereality.treecipe.platform.client;

import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.client.aspect.AspectRenderer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Defines an internal interface used for providing aspect renderers via
 * platform-specific means.
 */
@ApiStatus.Internal
public interface AspectRendererProvider
{
    /**
     * Gets a supplier for the aspect renderers, in render order.
     */
    Supplier<Map<Aspect<?>, AspectRenderer<?>>> getAspectRenderers();

    /**
     * Gets a render-order comparator for the aspect renderers.
     */
    Supplier<Comparator<AspectRenderer<?>>> getRenderOrderComparator();
}
