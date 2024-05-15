package finitereality.treecipe.neoforge.client;

import finitereality.annotations.common.Static;
import finitereality.treecipe.neoforge.client.aspect.BlockAspectRenderer;
import finitereality.treecipe.neoforge.client.aspect.EntityAspectRenderer;
import finitereality.treecipe.neoforge.client.aspect.ItemAspectRenderer;
import finitereality.treecipe.neoforge.client.event.RegisterAspectRenderersEvent;
import finitereality.treecipe.neoforge.init.AspectRegistration;
import net.neoforged.bus.api.IEventBus;

import java.util.Set;

@Static
public final class AspectRenderers
{
    private AspectRenderers() { }

    public static void register(final IEventBus modEventBus)
    {
        modEventBus.addListener(
            RegisterAspectRenderersEvent.class,
            AspectRenderers::registerRenderers);
    }

    private static void registerRenderers(
        final RegisterAspectRenderersEvent event)
    {
        event.register(
            AspectRegistration.Block.get(),
            BlockAspectRenderer::new,
            Set.of(),
            Set.of(AspectRegistration.Fluid.get()));
        // TODO: fluids go here
        event.register(
            AspectRegistration.Item.get(),
            ItemAspectRenderer::new,
            Set.of(AspectRegistration.Fluid.get()),
            Set.of(AspectRegistration.Entity.get()));
        event.register(
            AspectRegistration.Entity.get(),
            EntityAspectRenderer::new,
            Set.of(AspectRegistration.Item.get()),
            Set.of());
    }
}
