package finitereality.treecipe.neoforge.client.aspect;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import finitereality.treecipe.client.aspect.AspectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;

public final class ItemAspectRenderer
    implements AspectRenderer<Item>
{
    @Override
    public void render(
        final ResourceKey<Item> resource,
        final MultiBufferSource.BufferSource bufferSource,
        final PoseStack poseStack,
        final float partialTick)
    {
        final var item = BuiltInRegistries.ITEM.getOrThrow(resource);

        poseStack.pushPose();

        poseStack.mulPose(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        poseStack.scale(16, 16, 16);

        // ItemRenderer.getModel does some very silly logic so I'm skipping it.
        var model = Minecraft.getInstance()
            .getItemRenderer()
            .getItemModelShaper()
            .getItemModel(item);
        model = model == null
            ? null
            : model.getOverrides()
                .resolve(model,
                    item.getDefaultInstance(),
                    Minecraft.getInstance().level,
                    null, 0);
        model = model != null
            ? model
            : Minecraft.getInstance()
                .getItemRenderer()
                .getItemModelShaper()
                .getModelManager()
                .getMissingModel();

        if (model.usesBlockLight())
            Lighting.setupFor3DItems();
        else
            Lighting.setupForFlatItems();

        // But ItemRenderer.render still does some very silly logic... :sigh:
        Minecraft.getInstance()
            .getItemRenderer()
            .render(
                item.getDefaultInstance(),
                ItemDisplayContext.GUI,
                /* left-handed */ false,
                poseStack,
                bufferSource,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model);

        bufferSource.endBatch();
        // We ALSO shouldn't need to do this here, but this also calls BEWLRs
        // so to be safe...
        Lighting.setupFor3DItems();
        poseStack.popPose();
    }
}
