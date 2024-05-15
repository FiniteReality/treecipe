package finitereality.treecipe.client.aspect;

import com.mojang.blaze3d.vertex.PoseStack;
import finitereality.annotations.common.Static;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.platform.client.AspectRendererProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceKey;

import java.util.Comparator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * Defines a class which contains the built map of aspect renderers.
 */
@Static
public final class AspectRenderers
{
    static
    {
        final var provider = ServiceLoader.load(AspectRendererProvider.class)
            .findFirst().orElseThrow();

        AspectRenderers = provider.getAspectRenderers();
        RenderOrderComparator = provider.getRenderOrderComparator();
    }

    private static final Supplier<Map<Aspect<?>, AspectRenderer<?>>> AspectRenderers;
    private static final Supplier<Comparator<AspectRenderer<?>>> RenderOrderComparator;

    private AspectRenderers() { }

    /**
     * Gets the aspect renderer for the given aspect type.
     *
     * @param aspect The aspect type to get
     * @return A {@link AspectRenderer} to render.
     */
    public static AspectRenderer<?> getRenderer(
        final Aspect<?> aspect)
    {
        return AspectRenderers.get()
            .getOrDefault(aspect, DefaultRenderer.Instance);
    }

    /**
     * Returns a comparator which can be used to sort one or more aspect
     * renderers by the preferred render order.
     *
     * @return A {@link Comparator} which can be used to compare aspect
     * renderers based on render order.
     */
    public static Comparator<AspectRenderer<?>> renderOrder()
    {
        return RenderOrderComparator.get();
    }

    private static final class DefaultRenderer<T> implements AspectRenderer<T>
    {
        public static final DefaultRenderer<?> Instance = new DefaultRenderer<>();

        private DefaultRenderer() { }

        @Override
        public void render(
            final ResourceKey<T> resource,
            final BufferSource bufferSource,
            final PoseStack poseStack,
            final float partialTick)
        {
            Minecraft.getInstance().font.drawInBatch(
                "?",
                0, 0,
                0xFF_FF_FF_FF,
                true,
                poseStack.last().pose(),
                bufferSource,
                DisplayMode.NORMAL,
                0,
                15728880);
            bufferSource.endBatch();
        }
    }
}
