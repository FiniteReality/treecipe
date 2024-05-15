package finitereality.treecipe.neoforge.recipes;

import finitereality.treecipe.attribute.Attribute;
import finitereality.treecipe.attribute.Attributes;
import finitereality.treecipe.neoforge.util.IGetSmithingIngredientsAccessor;
import finitereality.treecipe.recipes.Recipe;
import finitereality.treecipe.recipes.RecipeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SmithingRecipeProvider<TRecipe extends SmithingRecipe>
    extends VanillaRecipeProvider<TRecipe>
{
    public SmithingRecipeProvider(
        final Class<TRecipe> tRecipeClass,
        final RecipeType<? super TRecipe> type)
    {
        super(tRecipeClass, type);
    }

    @Override
    protected VanillaRecipe<TRecipe> createRecipe(
        final RecipeHolder<TRecipe> holder,
        final Context context)
    {
        return new SmithingRecipe<>(holder, context.registryAccess());
    }

    protected static class SmithingRecipe<TRecipe extends net.minecraft.world.item.crafting.SmithingRecipe>
        extends VanillaRecipe<TRecipe>
    {
        public SmithingRecipe(
            final RecipeHolder<TRecipe> holder,
            final RegistryAccess registryAccess)
        {
            super(holder, registryAccess);
        }

        @Override
        public Collection<Component> getInputs()
        {
            final var seen = new HashSet<Item>();
            final var ingredientsAccessor
                = (IGetSmithingIngredientsAccessor)_holder.value();
            final var ingredients = Stream.of(
                ingredientsAccessor.getTemplateIngredient(),
                ingredientsAccessor.getBaseIngredient(),
                ingredientsAccessor.getAdditionIngredient());
            return ingredients
                .<Component>mapMulti((ingredient, consumer) -> {
                    for (final var stack : ingredient.getItems())
                    {
                        if (seen.add(stack.getItem()))
                            consumer.accept(new ItemComponent(stack));
                    }
                })
                .collect(Collectors.toSet());
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
