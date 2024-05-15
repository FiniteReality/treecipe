package finitereality.treecipe.neoforge.recipes;

import finitereality.treecipe.attribute.Attribute;
import finitereality.treecipe.attribute.Attributes;
import finitereality.treecipe.recipes.Recipe;
import finitereality.treecipe.recipes.Recipe.Component;
import finitereality.treecipe.recipes.RecipeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class FuelRecipeProvider implements RecipeProvider
{
    private final FurnaceComponent _machine;

    public FuelRecipeProvider(final ResourceKey<Block> machine)
    {
        _machine = new FurnaceComponent(machine);
    }

    @Override
    public Stream<Recipe> getRecipes(final Context context)
    {
        final var itemRegistry = context.registryAccess()
            .registryOrThrow(Registries.ITEM);

        return itemRegistry
            .getDataMap(NeoForgeDataMaps.FURNACE_FUELS)
            .entrySet()
            .stream()
            .map(pair -> new FuelRecipe(
                itemRegistry.getHolderOrThrow(pair.getKey()),
                _machine,
                pair.getValue()));
    }

    private record FurnaceComponent(
        ResourceKey<Block> resource)
        implements Component
    {
        @Override
        public ResourceKey<?> getResource()
        {
            return resource;
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries();
        }
    }

    private record FuelRecipe(
        Holder<Item> resource,
        FurnaceComponent machine,
        FurnaceFuel fuelValue)
        implements Recipe
    {

        @Override
        public Collection<Component> getInputs()
        {
            return Set.of(new ItemComponent(resource));
        }

        @Override
        public Collection<Component> getOutputs()
        {
            return Set.of(machine);
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries(
                Map.entry(Attributes.BURN_TIME, fuelValue.burnTime()));
        }
    }

    private record ItemComponent(Holder<Item> resource)
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
