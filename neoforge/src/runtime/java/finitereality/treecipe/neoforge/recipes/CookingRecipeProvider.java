package finitereality.treecipe.neoforge.recipes;

import finitereality.treecipe.attribute.Attribute;
import finitereality.treecipe.attribute.Attributes;
import finitereality.treecipe.recipes.Recipe;
import finitereality.treecipe.recipes.Recipe.Component;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CookingRecipeProvider<TRecipe extends AbstractCookingRecipe>
    extends VanillaRecipeProvider<TRecipe>
{
    final Set<FurnaceComponent> _furnaces;

    public CookingRecipeProvider(
        final Class<TRecipe> recipeClass,
        final RecipeType<? super TRecipe> type,
        final Set<ResourceKey<Block>> furnaces)
    {
        super(recipeClass, type);
        _furnaces = furnaces.stream()
            .map(FurnaceComponent::new)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Stream<Recipe> getRecipes(final Context context)
    {
        return super.getRecipes(context);
    }

    @Override
    protected VanillaRecipe<TRecipe> createRecipe(
        final RecipeHolder<TRecipe> holder,
        final Context context)
    {
        return new CookingRecipe<>(holder, context.registryAccess(), _furnaces);
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

    private static class CookingRecipe<TRecipe extends AbstractCookingRecipe>
        extends VanillaRecipe<TRecipe>
    {
        private final Set<FurnaceComponent> _furnaces;

        public CookingRecipe(
            final RecipeHolder<TRecipe> holder,
            final RegistryAccess registryAccess,
            final Set<FurnaceComponent> furnaces)
        {
            super(holder, registryAccess);
            _furnaces = furnaces;
        }

        @Override
        public Collection<Component> getInputs()
        {
            return Stream.concat(super.getInputs().stream(), _furnaces.stream())
                .collect(Collectors.toSet());
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries(
                Map.entry(Attributes.CRAFT_TIME, _holder.value().getCookingTime()));
        }
    }
}
