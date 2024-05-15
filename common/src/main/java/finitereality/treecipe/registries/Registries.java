package finitereality.treecipe.registries;

import finitereality.annotations.common.Static;
import finitereality.treecipe.Treecipe;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.recipes.RecipeProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Defines a type containing registries defined by Treecipe.
 */
@Static
public final class Registries
{
    private Registries() { }

    /**
     * The aspect registry, containing definitions of all known types of object
     * which may be included in a recipe.
     */
    public static final ResourceKey<Registry<Aspect<?>>> ASPECT
        = key("aspect");

    /**
     * The recipe registry, containing definitions of all known recipe
     * providers.
     */
    public static final ResourceKey<Registry<RecipeProvider>> RECIPE_PROVIDER
        = key("recipe_provider");

    private static <T> ResourceKey<Registry<T>> key(final String name)
    {
        return ResourceKey.createRegistryKey(
            new ResourceLocation(Treecipe.MOD_ID, name));
    }
}