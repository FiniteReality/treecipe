package finitereality.treecipe.neoforge.platform;

import com.google.common.collect.Streams;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.client.aspect.AspectRenderer;
import finitereality.treecipe.neoforge.TreecipeMod;
import finitereality.treecipe.registries.TreecipeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.ApiStatus;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class AspectRendererProvider
    implements finitereality.treecipe.platform.client.AspectRendererProvider
{
    private static final Supplier<DirectedAcyclicGraph<ResourceLocation, ?>>
        Graph = Lazy.of(TreecipeMod.AspectRendererGraphBuilder::build);
    private static final Supplier<Map<Aspect<?>, AspectRenderer<?>>>
        AspectRenderers = Lazy.of(
            () -> TreecipeMod.RegisteredAspectRenderers
                .entrySet().stream()
                .map(it -> {
                    final var aspect = TreecipeRegistries.ASPECT.get(it.getKey());
                    if (aspect == null) throw new IllegalStateException(); // Should be unreachable, buut...
                    return Map.entry(aspect, it.getValue().get());
                })
                .collect(Collectors.toUnmodifiableMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue)));

    @Override
    public Supplier<Map<Aspect<?>, AspectRenderer<?>>> getAspectRenderers()
    {
        return AspectRenderers;
    }

    @Override
    public Supplier<Comparator<AspectRenderer<?>>> getRenderOrderComparator()
    {
        return Lazy.of(AspectRendererProvider::buildRenderOrderComparator);
    }

    private static Comparator<AspectRenderer<?>> buildRenderOrderComparator()
    {
        final var order = Streams.stream(Graph.get().iterator()).toList();
        final var map = AspectRenderers.get().entrySet().stream()
            .map(it -> Map.entry(it.getValue(), Objects.requireNonNull(TreecipeRegistries.ASPECT.getKey(it.getKey()))))
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                Map.Entry::getValue));

        return Comparator.comparingInt(it -> {
            if (!map.containsKey(it))
                throw new IllegalStateException("Unknown aspect renderer");

            return order.indexOf(map.get(it));
        });
    }
}
