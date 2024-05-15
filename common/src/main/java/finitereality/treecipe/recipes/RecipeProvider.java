package finitereality.treecipe.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.stream.Stream;

/**
 * Defines an interface representing a provider of {@link Recipe} instances.
 */
public interface RecipeProvider
{
    /**
     * Gets the recipes provided by this recipe provider.
     *
     * @param context A context object containing any contextual information
     * which may be necessary for this recipe provider to function.
     *
     * @return A {@link Stream} of all possible recipes this provider knows of.
     */
    Stream<Recipe> getRecipes(final Context context);

    /**
     * Defines an interface representing the contextual parameters passed to
     * {@link #getRecipes(Context)}
     */
    interface Context
    {
        /**
         * Gets the registry access which can be used to look up registered
         * objects.
         *
         * @return A {@link RegistryAccess} containing the registries visible.
         */
        RegistryAccess registryAccess();

        /**
         * Gets the recipe manager used to store registered recipes.
         *
         * @return A {@link RecipeManager} containing all recipes.
         */
        RecipeManager recipeManager();
    }
}
