package finitereality.treecipe.neoforge.recipes;

import finitereality.treecipe.attribute.Attribute;
import finitereality.treecipe.recipes.Recipe;
import finitereality.treecipe.recipes.Recipe.Component;
import finitereality.treecipe.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class MilkingRecipeProvider
    implements RecipeProvider
{
    private final EntityComponent _cow;
    private final ItemComponent _emptyBucket;
    private final ItemComponent _filledBucket;

    public MilkingRecipeProvider(
        final EntityType<?> entityType,
        final Item emptyBucket,
        final Item filledBucket)
    {
        _cow = new EntityComponent(entityType);
        _emptyBucket = new ItemComponent(emptyBucket);
        _filledBucket = new ItemComponent(filledBucket);
    }

    @Override
    public Stream<Recipe> getRecipes(final Context context)
    {
        return Stream.of(new MilkingRecipe(_cow,  _emptyBucket, _filledBucket));
    }

    private record MilkingRecipe(
        EntityComponent cow,
        ItemComponent emptyBucket,
        ItemComponent filledBucket)
        implements Recipe
    {
        @Override
        public Collection<Component> getInputs()
        {
            return Set.of(cow, emptyBucket);
        }

        @Override
        public Collection<Component> getOutputs()
        {
            return Set.of(filledBucket);
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries();
        }
    }

    private record ItemComponent(Item item)
        implements Component
    {
        @Override
        public ResourceKey<?> getResource()
        {
            // TODO: betterify this (I'm lazy)
            return item.builtInRegistryHolder().key();
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries();
        }
    }

    private record EntityComponent(EntityType<?> entity)
        implements Component
    {
        @Override
        public ResourceKey<?> getResource()
        {
            // TODO: betterify this (I'm lazy)
            return entity.builtInRegistryHolder().key();
        }

        @Override
        public Map<Attribute<?>, ?> getAttributes()
        {
            return Map.ofEntries();
        }
    }
}
