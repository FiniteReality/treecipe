package finitereality.treecipe.registries;

import finitereality.annotations.common.Static;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.platform.RegistryProvider;
import finitereality.treecipe.recipes.RecipeProvider;
import net.minecraft.core.Registry;

import java.util.ServiceLoader;

@Static
public final class TreecipeRegistries
{
    static
    {
        final var provider = ServiceLoader.load(RegistryProvider.class)
            .findFirst().orElseThrow();

        ASPECT = provider.getAspectRegistry();
        RECIPE_PROVIDER = provider.getRecipeProviderRegistry();
    }

    private TreecipeRegistries() { }

    public static final Registry<Aspect<?>> ASPECT;
    public static final Registry<RecipeProvider> RECIPE_PROVIDER;
}
