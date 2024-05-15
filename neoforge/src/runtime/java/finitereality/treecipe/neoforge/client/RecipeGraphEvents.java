package finitereality.treecipe.neoforge.client;

import com.mojang.blaze3d.platform.InputConstants;
import finitereality.annotations.common.Static;
import finitereality.treecipe.runtime.RecipeGraph;
import finitereality.treecipe.neoforge.client.gui.screens.RecipeGraphScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Static
public final class RecipeGraphEvents
{
    private static final Logger Logger
        = LoggerFactory.getLogger(RecipeGraphEvents.class);

    private static final Set<KeyMapping> ConsumeScreenEvents
        = new HashSet<>();

    static
    {
        ConsumeScreenEvents.add(Keybindings.ViewRecipes);
        ConsumeScreenEvents.add(Keybindings.ViewUsages);
    }

    private RecipeGraphEvents() { }

    private static boolean onRecipeKey(final Screen screen)
    {
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen))
            return false;

        final var slot = containerScreen.getSlotUnderMouse();
        if (slot == null || !slot.hasItem())
            return false;

        // TODO: better-ify this.
        final var item = slot.getItem().getItem().builtInRegistryHolder().key();

        RecipeGraphScreen newScreen = null;
        try
        {
            if (Keybindings.ViewUsages.isDown())
                newScreen = RecipeGraphScreen.craftedFrom(item);
            else if (Keybindings.ViewRecipes.isDown())
                newScreen = RecipeGraphScreen.craftedInto(item);
        }
        catch (Exception e)
        {
            Logger.warn("Failed to create recipe graph screen!", e);
        }

        if (newScreen == null)
            return false;

        Minecraft.getInstance().setScreen(newScreen);
        return true;
    }

    public static void register(final IEventBus eventBus)
    {
        eventBus.addListener(
            RecipesUpdatedEvent.class,
            RecipeGraphEvents::onRecipesUpdated);
        eventBus.addListener(
            ScreenEvent.KeyPressed.Pre.class,
            RecipeGraphEvents::onKeyDown);
        eventBus.addListener(
            ScreenEvent.KeyReleased.Pre.class,
            RecipeGraphEvents::onKeyUp);
    }

    private static void onRecipesUpdated(final RecipesUpdatedEvent event)
    {
        final var profiler = Minecraft.getInstance().getProfiler();
        final var level = Minecraft.getInstance().level;

        if (level == null) return;

        RecipeGraph.buildRecipeGraph(
            profiler,
            level.registryAccess(),
            event.getRecipeManager());
    }

    private static void onKeyDown(final ScreenEvent.KeyPressed.Pre event)
    {
        final var key = InputConstants.getKey(
            event.getKeyCode(),
            event.getScanCode());

        final var handled = ConsumeScreenEvents.stream()
            .anyMatch(it -> {
                if (it.isActiveAndMatches(key))
                {
                    it.setDown(true);
                    return true;
                }

                return false;
            });

        if (handled)
            event.setCanceled(onRecipeKey(event.getScreen()));
    }

    private static void onKeyUp(final ScreenEvent.KeyReleased.Pre event)
    {
        final var handled = ConsumeScreenEvents.stream()
            .anyMatch(it -> {
                if (it.isDown())
                {
                    it.setDown(false);
                    return true;
                }

                return false;
            });

        if (handled)
            event.setCanceled(true);
    }
}
