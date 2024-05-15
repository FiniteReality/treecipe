package finitereality.treecipe.runtime;

import finitereality.annotations.common.Static;
import finitereality.treecipe.runtime.util.AsReverseDirectedGraph;
import finitereality.treecipe.recipes.Recipe;
import finitereality.treecipe.recipes.RecipeProvider;
import finitereality.treecipe.registries.Registries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.LayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Static
public final class RecipeGraph
{
    private static final Logger Logger = LoggerFactory.getLogger(RecipeGraph.class);

    private static final AtomicReference<DirectedPseudograph<ResourceKey<?>, RecipeReference>> _currentGraph
        = new AtomicReference<>();

    private RecipeGraph() { }

    // resource -> ???
    public static Stream<ResourceKey<?>> craftableWithResource(
        final ResourceKey<?> resourceKey)
    {
        final var currentGraph = _currentGraph.get();
        if (currentGraph == null) return Stream.of();

        if (!currentGraph.containsVertex(resourceKey))
            return Stream.of();

        return currentGraph.outgoingEdgesOf(resourceKey)
            .stream()
            .map(currentGraph::getEdgeTarget);
    }

    // ??? -> resource
    public static Stream<ResourceKey<?>> craftsIntoResource(
        final ResourceKey<?> resourceKey)
    {
        final var currentGraph = _currentGraph.get();
        if (currentGraph == null) return Stream.of();

        if (!currentGraph.containsVertex(resourceKey))
            return Stream.of();

        return currentGraph.incomingEdgesOf(resourceKey)
            .stream()
            .map(currentGraph::getEdgeSource);
    }

    public static boolean canCraftInto(
        final ResourceKey<?> input,
        final ResourceKey<?> output)
    {
        final var currentGraph = _currentGraph.get();
        if (currentGraph == null) return false;

        return currentGraph.containsEdge(input, output);
    }

    public static void layoutTo(
        final ResourceKey<?> visibleTo,
        final Supplier<LayoutAlgorithm2D<ResourceKey<?>, ?>> algorithmSupplier,
        final LayoutModel2D<ResourceKey<?>> model)
    {
        final var currentGraph = _currentGraph.get();
        if (currentGraph == null) return;

        layout(
            visibleTo,
            algorithmSupplier.get(),
            model,
            new AsReverseDirectedGraph<>(currentGraph));
    }

    public static void layoutFrom(
        final ResourceKey<?> visibleFrom,
        final Supplier<LayoutAlgorithm2D<ResourceKey<?>, ?>> algorithmSupplier,
        final LayoutModel2D<ResourceKey<?>> model)
    {
        final var currentGraph = _currentGraph.get();
        if (currentGraph == null) return;

        layout(
            visibleFrom,
            algorithmSupplier.get(),
            model,
            currentGraph);
    }

    @SuppressWarnings("unchecked")
    private static void layout(
        final ResourceKey<?> origin,
        final LayoutAlgorithm2D<ResourceKey<?>, ?> algorithm,
        final LayoutModel2D<ResourceKey<?>> model,
        final Graph<ResourceKey<?>, RecipeReference> graph)
    {
        if (origin != null)
        {
            final var vertexSet = new HashSet<ResourceKey<?>>(Set.of(origin));
            new BreadthFirstIterator<>(graph, origin)
                .forEachRemaining(vertexSet::add);

            final var subgraph = new AsSubgraph<>(graph, vertexSet);
            ((LayoutAlgorithm2D<ResourceKey<?>, RecipeReference>)algorithm)
                .layout(subgraph, model);
        }
        else
        {
            ((LayoutAlgorithm2D<ResourceKey<?>, RecipeReference>)algorithm)
                .layout(graph, model);
        }
    }

    public static void buildRecipeGraph(
        final ProfilerFiller profiler,
        final RegistryAccess registryAccess,
        final RecipeManager recipeManager)
    {
        final var aspects = registryAccess.lookup(Registries.ASPECT)
            .orElseThrow(() -> new IllegalStateException(
                "Failed to get aspect registry"));
        final var recipeProviders = registryAccess.lookup(Registries.RECIPE_PROVIDER)
            .orElseThrow(() -> new IllegalStateException(
                "Failed to get recipe provider registry"));

        profiler.push("Building recipe graph");
        final var graphBuilder = DirectedPseudograph.<ResourceKey<?>, RecipeReference>createBuilder(RecipeReference.class);

        /*
        profiler.push("Adding vertices");
        // Register all aspects and their items as vertices in the graph by key
        aspects.listElements().forEach(aspectHolder -> {
            final var aspect = aspectHolder.value();
            final var aspectRegistry = registries.lookup(aspect.registryKey());

            if (aspectRegistry.isEmpty())
            {
                Logger.warn("Aspect {} refers to an unknown registry {}",
                    aspectHolder,
                    aspect.registryKey());

                return;
            }

            Logger.debug("Adding vertices for aspect {}", aspectHolder.key());

            final var verticesToAdd = aspectRegistry.get()
                .listElements()
                .map(Reference::key)
                .toArray(ResourceKey<?>[]::new);
            graphBuilder.addVertices(verticesToAdd);

            Logger.trace("Added {} vertices from aspect {}",
                verticesToAdd.length,
                aspectHolder.key());
        });
         */

        profiler.popPush("Adding edges");
        // Register all recipes by their components
        final var context = new ProviderContext(registryAccess, recipeManager);
        recipeProviders.listElements().forEach(recipeProviderHolder -> {
            final var recipeProvider = recipeProviderHolder.value();

            Logger.debug(
                "Adding edges for recipe provider {}",
                recipeProviderHolder.key());

            // We don't ACTUALLY need an atomic integer here, but we have one
            // anyway...
            // Though I suppose if we want Parallel:tm: in the future we're
            // ready for it
            final var count = new AtomicInteger();
            recipeProvider.getRecipes(context)
                .forEach(recipe -> {
                    var inputs = recipe.getInputs();
                    var outputs = recipe.getOutputs();
                    inputs.forEach(input ->
                        outputs.forEach(output -> {
                            graphBuilder.addEdge(
                                input.getResource(),
                                output.getResource(),
                                new RecipeReference(recipe));

                            count.incrementAndGet();
                        })
                    );
                });

            Logger.trace("Added {} edges from recipe provider {}",
                count.get(),
                recipeProviderHolder.key());
        });

        profiler.popPush("Finalizing graph");
        final var newGraph = graphBuilder.build();
        _currentGraph.set(newGraph);
        profiler.pop();
        Logger.info("Built recipe graph containing {} vertices and {} edges",
            newGraph.vertexSet().size(),
            newGraph.edgeSet().size());
        profiler.pop();
    }

    private record ProviderContext(
        RegistryAccess registryAccess,
        RecipeManager recipeManager)
        implements RecipeProvider.Context { }

    // This is necessary because a single recipe has multiple inputs and
    // outputs, and to link them properly we need a unique edge.
    private static class RecipeReference
    {
        private final Recipe _recipe;

        public RecipeReference(final Recipe recipe)
        {
            _recipe = recipe;
        }

        public Recipe recipe() { return _recipe; }
    }
}
