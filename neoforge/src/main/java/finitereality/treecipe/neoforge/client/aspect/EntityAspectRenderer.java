package finitereality.treecipe.neoforge.client.aspect;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import finitereality.treecipe.client.aspect.AspectRenderer;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class EntityAspectRenderer
    implements AspectRenderer<EntityType<?>>
{
    @Override
    public void render(
        final ResourceKey<EntityType<?>> resource,
        final BufferSource bufferSource,
        final PoseStack poseStack,
        final float partialTick)
    {
        final var type = BuiltInRegistries.ENTITY_TYPE
            .getOrThrow(resource);
        final var renderer = Minecraft.getInstance()
            .getEntityRenderDispatcher()
            .renderers.get(type);

        if (!(renderer instanceof RenderLayerParent<?,?> layerParent))
            return;

        poseStack.pushPose();

        poseStack.mulPose(new Matrix4f().scaling(1.0F, 1.0F, -1.0F));
        poseStack.scale(16, 16, 16);
        poseStack.pushPose();

        poseStack.mulPose(new Quaternionf()
            .rotateLocalY(Mth.PI / 4)
            .rotateLocalX(Mth.PI / 8));

        poseStack.translate(0.5, -type.getHeight() * 2f / 3, -0.5);

        try
        {
            Lighting.setupForEntityInInventory();

            final var model = layerParent.getModel();
            model.young = false;

            // TODO: this is LIKELY going to crash...
            final var buffer = bufferSource.getBuffer(model
                .renderType(renderer.getTextureLocation(null)));
            model.renderToBuffer(
                poseStack,
                buffer,
                15728880,
                OverlayTexture.NO_OVERLAY,
                1, 1, 1, 1);
            bufferSource.endBatch();

            Lighting.setupFor3DItems();
        }
        catch (Throwable e)
        {
            final var report = CrashReport.forThrowable(e, "Rendering entity");
            report.addCategory("Renderer details")
                .setDetail("Assigned renderer", renderer)
                .setDetail("Partial tick", partialTick);
            throw new ReportedException(report);
        }

        poseStack.popPose();
        poseStack.popPose();
    }
}
