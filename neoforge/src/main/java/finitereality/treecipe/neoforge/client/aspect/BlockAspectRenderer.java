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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public final class BlockAspectRenderer
    implements AspectRenderer<Block>
{
    @Override
    public void render(
        final ResourceKey<Block> resource,
        final MultiBufferSource.BufferSource bufferSource,
        final PoseStack poseStack,
        final float partialTick)
    {
        final var block = BuiltInRegistries.BLOCK.getOrThrow(resource);
        final var blockState = block.defaultBlockState();

        if (blockState.getRenderShape() == RenderShape.INVISIBLE)
            return;

        poseStack.pushPose();

        poseStack.mulPose(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        poseStack.scale(16, 16, 16);
        poseStack.pushPose();

        final var model = Minecraft.getInstance().getBlockRenderer()
            .getBlockModel(blockState);

        model.applyTransform(ItemDisplayContext.GUI, poseStack, false);
        poseStack.translate(-0.5, -0.5, -0.5);

        Lighting.setupFor3DItems();

        // TODO: duplicate this codepath but call level.getShade(...)
        Minecraft.getInstance()
            .getBlockRenderer()
            .renderSingleBlock(
                blockState,
                poseStack,
                bufferSource,
                LightTexture.FULL_BLOCK,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                null);

        bufferSource.endBatch();
        // We shouldn't ACTUALLY need to do this, but BEWLRs might mess things
        // up. So just to be safe...
        Lighting.setupFor3DItems();

        poseStack.popPose();
        poseStack.popPose();
    }
}
