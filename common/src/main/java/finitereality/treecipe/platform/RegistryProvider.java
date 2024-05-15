package finitereality.treecipe.platform;

import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.recipes.RecipeProvider;
import finitereality.treecipe.registries.Registries;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

/**
 * Defines an internal interface used for providing registries via
 * platform-specific means.
 */
@ApiStatus.Internal
public interface RegistryProvider
{
    /**
     * Gets the aspect registry.
     *
     * @return A {@link Registry} for the {@link Registries#ASPECT} type.
     */
    Registry<Aspect<?>> getAspectRegistry();

    /**
     * Gets the recipe provider registry.
     *
     * @return A {@link Registry} for the {@link Registries#RECIPE_PROVIDER}
     * type.
     */
    Registry<RecipeProvider> getRecipeProviderRegistry();
}
