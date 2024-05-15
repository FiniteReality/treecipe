package finitereality.treecipe.neoforge.client;

import com.mojang.blaze3d.platform.InputConstants;
import finitereality.annotations.common.Static;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import java.util.List;

@Static
public final class Keybindings
{
    private Keybindings() { }

    public static void register(final IEventBus eventBus)
    {
        eventBus.addListener(
            RegisterKeyMappingsEvent.class,
            Keybindings::registerKeyMappings);
    }

    private static void registerKeyMappings(
        final RegisterKeyMappingsEvent event)
    {
        KeysToRegister.forEach(event::register);
    }

    // Call this cursed or whatever you want, but it's clean :)
    private static final List<KeyMapping> KeysToRegister = List.of(
        ViewRecipes = new KeyMapping(
            "key.treecipe.show_recipe",
            KeyConflictContext.GUI,
            InputConstants.getKey("key.keyboard.r"),
            "key.categories.treecipe.ui"),
        ViewUsages = new KeyMapping(
            "key.treecipe.show_usage",
            KeyConflictContext.GUI,
            InputConstants.getKey("key.keyboard.u"),
            "key.categories.treecipe.ui")
    );

    public static final KeyMapping ViewRecipes;
    public static final KeyMapping ViewUsages;
}
