package finitereality.treecipe.neoforge.init;

import com.mojang.serialization.Lifecycle;
import finitereality.annotations.common.Static;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.recipes.RecipeProvider;
import finitereality.treecipe.registries.Registries;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.RegistryManager;

@Static
public final class RegistryRegistration
{
    private RegistryRegistration() { }

    public static final Registry<Aspect<?>> Aspect
        = new MappedRegistry<>(Registries.ASPECT, Lifecycle.stable(), true);
    public static final Registry<RecipeProvider> RecipeProvider
        = new RegistryBuilder<>(Registries.RECIPE_PROVIDER)
            .create();

    public static void register(final IEventBus bus)
    {
        bus.addListener(NewRegistryEvent.class, event -> {
            event.register(Aspect);
            event.register(RecipeProvider);
        });
    }
}
