package finitereality.treecipe.neoforge.recipes;

import finitereality.treecipe.attribute.Attribute;
import finitereality.treecipe.attribute.Attributes;
import finitereality.treecipe.recipes.Recipe;
import finitereality.treecipe.recipes.RecipeProvider;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VanillaRecipeProvider<TRecipe extends net.minecraft.world.item.crafting.Recipe<?>>
    implements RecipeProvider
{
    private final Class<TRecipe> _recipeClass;
    private final RecipeType<? super TRecipe> _type;

    public VanillaRecipeProvider(
        final Class<TRecipe> recipeClass,
        final RecipeType<? super TRecipe> type)
    {
        _recipeClass = recipeClass;
        _type = type;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Stream<Recipe> getRecipes(final Context context)
    {
        return context.recipeManager()
            .getAllRecipesFor((RecipeType)_type)
            .stream()
            .filter(x -> ((RecipeHolder<TRecipe>)x).value().getClass().equals(_recipeClass))
            .map(holder -> createRecipe((RecipeHolder<TRecipe>)holder, context));
    }

    protected VanillaRecipe<TRecipe> createRecipe(
        final RecipeHolder<TRecipe> holder,
        final Context context)
    {
        return new VanillaRecipe<>(holder, context.registryAccess());
    }

    protected static class VanillaRecipe<TRecipe extends net.minecraft.world.item.crafting.Recipe<?>>
        implements Recipe
    {
        protected final RecipeHolder<TRecipe> _holder;
        protected final RegistryAccess _registryAccess;

        public VanillaRecipe(
            final RecipeHolder<TRecipe> holder,
            final RegistryAccess registryAccess)
        {
            _holder = holder;
            _registryAccess = registryAccess;
        }

        @Override
        public Collection<Component> getInputs()
        {
            final var seen = new HashSet<Item>();
            return _holder.value()
                .getIngredients()
                .stream()
                .<Component>mapMulti((ingredient, consumer) -> {
                    for (final var stack : ingredient.getItems())
                    {
                        if (seen.add(stack.getItem()))
                            consumer.accept(new ItemComponent(stack));
                    }
                })
                .collect(Collectors.toSet());
        }

        @Override
        public Collection<Component> getOutputs()
        {
            final var result = _holder.value().getResultItem(_registryAccess);
            return ObjectSet.of(new ItemComponent(result));
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.of();
        }

        private static class ItemComponent implements Component
        {
            private final Holder<Item> _item;
            private final int _count;

            public ItemComponent(final ItemStack stack)
            {
                _item = stack.getItemHolder();
                _count = stack.getCount();
            }

            @Override
            public ResourceKey<?> getResource()
            {
                return _item.unwrapKey().orElseThrow();
            }

            @Override
            public Map<Attribute<?>, ?> getAttributes()
            {
                return Map.ofEntries(
                    Map.entry(Attributes.COUNT, _count));
            }
        }
    }
}
