package finitereality.treecipe.neoforge;

import finitereality.treecipe.Treecipe;
import finitereality.treecipe.client.aspect.AspectRenderer;
import finitereality.treecipe.neoforge.client.AspectRenderers;
import finitereality.treecipe.neoforge.client.GraphTooltips;
import finitereality.treecipe.neoforge.client.Keybindings;
import finitereality.treecipe.neoforge.client.RecipeGraphEvents;
import finitereality.treecipe.neoforge.init.AspectRegistration;
import finitereality.treecipe.neoforge.init.RecipeProviderRegistration;
import finitereality.treecipe.neoforge.init.RegistryRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod(Treecipe.MOD_ID)
public final class TreecipeMod
{
    private static final Logger Logger = LoggerFactory.getLogger(TreecipeMod.class);

    public static final ResourceLocation AspectRendererGraphRoot
        = new ResourceLocation(Treecipe.MOD_ID, "aspect_root");
    public static final Map<ResourceLocation, Supplier<AspectRenderer<?>>>
        RegisteredAspectRenderers = new HashMap<>();
    public static final org.jgrapht.graph.builder.GraphBuilder<ResourceLocation, ?, ? extends DirectedAcyclicGraph<ResourceLocation, ?>>
        AspectRendererGraphBuilder = DirectedAcyclicGraph.createBuilder(Object::new);

    public TreecipeMod(final IEventBus modEventBus, final Dist distribution)
    {
        AspectRegistration.register(modEventBus);
        RecipeProviderRegistration.register(modEventBus);
        RegistryRegistration.register(modEventBus);

        if (distribution.isClient())
        {
            AspectRenderers.register(modEventBus);
            GraphTooltips.register(NeoForge.EVENT_BUS);
            Keybindings.register(modEventBus);
            RecipeGraphEvents.register(NeoForge.EVENT_BUS);
        }
    }
}
