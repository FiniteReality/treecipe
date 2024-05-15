package finitereality.treecipe.neoforge.init;

import finitereality.annotations.common.Static;
import finitereality.treecipe.Treecipe;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Static
public final class AspectRegistration
{
    private AspectRegistration() { }

    private static final DeferredRegister<Aspect<?>> _aspects
        = DeferredRegister.create(Registries.ASPECT, Treecipe.MOD_ID);

    // TODO: consider Aspect.ofRegistry(... (ra, r) -> ...getName())
    public static final DeferredHolder<Aspect<?>, Aspect<Block>> Block
        = _aspects.register("block", () -> new Aspect<>()
        {
            @Override
            public ResourceKey<Registry<Block>> registryKey()
            {
                return net.minecraft.core.registries.Registries.BLOCK;
            }

            @Override
            public Component getResourceDescription(
                final RegistryAccess registryAccess,
                final ResourceKey<Block> resource)
            {
                return registryAccess.registryOrThrow(registryKey())
                    .getOrThrow(resource)
                    .getName();
            }

            @Override
            public Class<Block> getAspectType()
            {
                return Block.class;
            }
        });

    public static final DeferredHolder<Aspect<?>, Aspect<Enchantment>> Enchantment
        = _aspects.register("enchantment", () -> new Aspect<>()
        {
            @Override
            public ResourceKey<Registry<net.minecraft.world.item.enchantment.Enchantment>> registryKey()
            {
                return net.minecraft.core.registries.Registries.ENCHANTMENT;
            }

            @Override
            public Component getResourceDescription(
                final RegistryAccess registryAccess,
                final ResourceKey<Enchantment> resource)
            {
                final var enchant = registryAccess.registryOrThrow(registryKey())
                    .getOrThrow(resource);

                return enchant.getFullname(enchant.getMinLevel());
            }

            @Override
            public Class<Enchantment> getAspectType()
            {
                return Enchantment.class;
            }
        });

    @SuppressWarnings("unchecked")
    public static final DeferredHolder<Aspect<?>, Aspect<EntityType<?>>> Entity
        = _aspects.register("entity", () -> new Aspect<>()
        {
            @Override
            public ResourceKey<Registry<EntityType<?>>> registryKey()
            {
                return net.minecraft.core.registries.Registries.ENTITY_TYPE;
            }

            @Override
            public Component getResourceDescription(
                final RegistryAccess registryAccess,
                final ResourceKey<EntityType<?>> resource)
            {
                return registryAccess.registryOrThrow(registryKey())
                    .getOrThrow(resource)
                    .getDescription();
            }

            @Override
            public Class<EntityType<?>> getAspectType()
            {
                return (Class<EntityType<?>>)(Class<?>)EntityType.class;
            }
        });

    public static final DeferredHolder<Aspect<?>, Aspect<Fluid>> Fluid
        = _aspects.register("fluid", () -> new Aspect<>()
        {
            @Override
            public ResourceKey<Registry<Fluid>> registryKey()
            {
                return net.minecraft.core.registries.Registries.FLUID;
            }

            @Override
            public Component getResourceDescription(
                final RegistryAccess registryAccess,
                final ResourceKey<Fluid> resource)
            {
                return registryAccess.registryOrThrow(registryKey())
                    .getOrThrow(resource)
                    .getFluidType()
                    .getDescription();
            }

            @Override
            public Class<Fluid> getAspectType()
            {
                return Fluid.class;
            }
        });

    public static final DeferredHolder<Aspect<?>, Aspect<Item>> Item
        = _aspects.register("item", () -> new Aspect<>()
        {
            @Override
            public ResourceKey<Registry<Item>> registryKey()
            {
                return net.minecraft.core.registries.Registries.ITEM;
            }

            @Override
            public Component getResourceDescription(
                final RegistryAccess registryAccess,
                final ResourceKey<Item> resource)
            {
                return registryAccess.registryOrThrow(registryKey())
                    .getOrThrow(resource)
                    .getDescription();
            }

            @Override
            public Class<Item> getAspectType()
            {
                return Item.class;
            }
        });

    public static void register(final IEventBus bus)
    {
        _aspects.register(bus);
    }
}
