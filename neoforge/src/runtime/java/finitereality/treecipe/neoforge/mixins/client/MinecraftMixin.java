package finitereality.treecipe.neoforge.mixins.client;

import finitereality.treecipe.neoforge.client.event.RegisterAspectRenderersEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class MinecraftMixin
{
    @Inject(
        method = "<init>",
        at = @At(value = "INVOKE", target = "initClientHooks"),
        require = 1)
    private void treecipe$callRegisterAspectRenderers(
        final CallbackInfo callbackInfo)
    {
        ModLoader.postEvent(new RegisterAspectRenderersEvent());
    }
}
