package finitereality.treecipe.neoforge.platform;

import finitereality.annotations.common.Static;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.neoforge.init.RegistryRegistration;
import finitereality.treecipe.recipes.RecipeProvider;
import net.minecraft.core.Registry;

@Static
public final class RegistryProvider
    implements finitereality.treecipe.platform.RegistryProvider
{
    @Override
    public Registry<Aspect<?>> getAspectRegistry()
    {
        return RegistryRegistration.Aspect;
    }

    @Override
    public Registry<RecipeProvider> getRecipeProviderRegistry()
    {
        return RegistryRegistration.RecipeProvider;
    }
}
