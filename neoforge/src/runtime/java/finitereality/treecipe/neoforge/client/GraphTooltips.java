package finitereality.treecipe.neoforge.client;

import finitereality.annotations.common.Static;
import finitereality.treecipe.registries.Registries;
import finitereality.treecipe.runtime.RecipeGraph;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Objects;

@Static
public final class GraphTooltips
{
    private GraphTooltips() { }

    public static void register(final IEventBus bus)
    {
        bus.addListener(ItemTooltipEvent.class, GraphTooltips::onItemTooltip);
    }

    private static void onItemTooltip(final ItemTooltipEvent event)
    {
        final var key = BuiltInRegistries.ITEM
            .getResourceKey(event.getItemStack().getItem())
            .orElseThrow();

        final var tooltip = event.getToolTip();
        tooltip.add(Component.literal("Crafted with:")
            .withStyle(it -> it.withUnderlined(true)));
        RecipeGraph.craftsIntoResource(key)
            .distinct()
            .map(subResource -> getComponent(subResource)
                .withStyle(it -> it.withColor(ChatFormatting.GRAY)))
            .forEach(tooltip::add);

        tooltip.add(Component.literal("Used in:")
            .withStyle(it -> it.withUnderlined(true)));
        RecipeGraph.craftableWithResource(key)
            .distinct()
            .map(subResource -> getComponent(subResource)
                .withStyle(it -> it.withColor(ChatFormatting.GRAY)))
            .forEach(tooltip::add);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> MutableComponent getComponent(final ResourceKey<T> resource)
    {
        final var level = Objects.requireNonNull(
            Minecraft.getInstance().level);
        final var registryAccess = level.registryAccess();
        final var aspect = registryAccess.registryOrThrow(Registries.ASPECT)
            .stream()
            .filter(it -> resource.isFor(it.registryKey()))
            .findFirst()
            .orElseThrow();

        return aspect.getResourceDescription(
                registryAccess,
                (ResourceKey)resource)
            .copy()
            .append(CommonComponents.SPACE)
            .append(aspect.getDescription()
                .copy()
                .withStyle(it -> it
                    .withColor(ChatFormatting.DARK_GRAY)
                    .withItalic(true)));
    }
}
