package finitereality.treecipe.client.aspect;

import com.mojang.blaze3d.vertex.PoseStack;
import finitereality.treecipe.aspect.Aspect;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.Set;

/**
 * Defines an interface that can be used to render a single resource.
 *
 * @param <T> The type of object this renderer can render.
 */
public interface AspectRenderer<T>
{
    /**
     * Renders the resource.
     * <p>
     * The transformation necessary to translate the renderer to the correct
     * location has already been applied; you should therefore assume any model
     * is being rendered around the origin.
     *
     * @param resource The resource to be rendered.
     * @param bufferSource The buffer source for getting the correct buffer to
     * render to.
     * @param poseStack The pose stack for applying transformations.
     * @param partialTick The current partial tick, which may be necessary for
     * animated renderers.
     */
    void render(
        final ResourceKey<T> resource,
        final MultiBufferSource.BufferSource bufferSource,
        final PoseStack poseStack,
        final float partialTick);
}
