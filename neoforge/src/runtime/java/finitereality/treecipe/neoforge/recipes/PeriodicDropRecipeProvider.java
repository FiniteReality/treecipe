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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PeriodicDropRecipeProvider
    implements RecipeProvider
{
    private final EntityComponent _entity;
    private final Set<ItemComponent> _periodicDrop;

    public PeriodicDropRecipeProvider(
        final EntityType<?> entityType,
        final Set<Item> periodicDrops)
    {
        _entity = new EntityComponent(entityType);
        _periodicDrop = periodicDrops.stream()
            .map(ItemComponent::new)
            .collect(Collectors.toSet());
    }

    @Override
    public Stream<Recipe> getRecipes(final Context context)
    {
        return _periodicDrop.stream()
            .map(it -> new PeriodicDropRecipe(_entity, it));
    }

    private record PeriodicDropRecipe(
        EntityComponent entity,
        ItemComponent drop)
        implements Recipe
    {
        @Override
        public Collection<Component> getInputs()
        {
            return Set.of(entity);
        }

        @Override
        public Collection<Component> getOutputs()
        {
            return Set.of(drop);
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
