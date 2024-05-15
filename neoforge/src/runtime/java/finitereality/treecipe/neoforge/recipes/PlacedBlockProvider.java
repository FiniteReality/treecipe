package finitereality.treecipe.neoforge.recipes;

import finitereality.treecipe.attribute.Attribute;
import finitereality.treecipe.recipes.Recipe;
import finitereality.treecipe.recipes.Recipe.Component;
import finitereality.treecipe.recipes.RecipeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class PlacedBlockProvider implements RecipeProvider
{
    final Set<Block> _blocks;

    public PlacedBlockProvider(final Set<Block> blocks)
    {
        _blocks = blocks;
    }

    @Override
    public Stream<Recipe> getRecipes(final Context context)
    {
        final var blockRegistry = context.registryAccess()
            .registryOrThrow(Registries.BLOCK);
        final var itemRegistry = context.registryAccess()
            .registryOrThrow(Registries.ITEM);

        return _blocks.stream()
            .map(blockRegistry::getResourceKey)
            .flatMap(Optional::stream)
            .map(blockRegistry::getHolderOrThrow)
            .map(it -> Map.entry(it, getItemFromBlock(itemRegistry, it)))
            .map(it -> new PlacedBlockRecipe(it.getKey(), it.getValue()));
    }

    private static Holder<Item> getItemFromBlock(
        final Registry<Item> registry,
        final Holder<Block> block)
    {
        final var key = registry.getResourceKey(block.value().asItem())
            .orElseThrow();

        return registry.getHolderOrThrow(key);
    }

    private record PlacedBlockRecipe(
        Holder<Block> block,
        Holder<Item> item)
        implements Recipe
    {

        @Override
        public Collection<Component> getInputs()
        {
            return Set.of(new HolderComponent(item));
        }

        @Override
        public Collection<Component> getOutputs()
        {
            return Set.of(new HolderComponent(block));
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries();
        }
    }

    private record HolderComponent(Holder<?> resource)
        implements Component
    {
        @Override
        public ResourceKey<?> getResource()
        {
            return resource.unwrapKey().orElseThrow();
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries();
        }
    }
}
