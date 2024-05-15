package finitereality.treecipe.aspect;

import finitereality.treecipe.registries.TreecipeRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

/**
 * Defines an interface that represents an "aspect", an object that may have
 * some resource value attached to it.
 *
 * @param <T> The type of object this aspect represents.
 */
public abstract class Aspect<T>
{
    private final Holder.Reference<Aspect<?>> _registryHolder
        = TreecipeRegistries.ASPECT.createIntrusiveHolder(this);
    private String _descriptionId;

    /**
     * Gets the registry key containing all possible definitions of this aspect.
     *
     * @return A {@link ResourceKey} referring to a registry of objects of this
     * type.
     */
    public abstract ResourceKey<Registry<T>> registryKey();

    /**
     * Gets a component which can be used to describe the given resource.
     *
     * @param registryAccess A {@link RegistryAccess} which can be used to
     * perform registry lookups in order to compute the description.
     * @param resource A {@link ResourceKey} naming the resource to look up.
     *
     * @return A {@link Component} which describes the resource.
     */
    public abstract Component getResourceDescription(
        final RegistryAccess registryAccess,
        final ResourceKey<T> resource);

    /**
     * Gets a component which can be used to describe this aspect.
     *
     * @return A {@link Component} which describes this aspect.
     */
    public Component getDescription()
    {
        return Component.translatable(getDescriptionId());
    }

    /**
     * Gets a translatable string which can be used to describe this aspect.
     *
     * @return A string which describes this aspect.
     */
    public String getDescriptionId()
    {
        if (_descriptionId == null)
            return _descriptionId = Util.makeDescriptionId("aspect",
                TreecipeRegistries.ASPECT.getKey(this));

        return _descriptionId;
    }

    /**
     * Gets the {@link Class} representing the type of object this aspect
     * represents.
     *
     * @return A {@link Class} of type {@link T}.
     */
    public abstract Class<T> getAspectType();

    /**
     * Gets a registry holder for this object.
     *
     * @return A {@link Holder} for this object.
     */
    public Holder<Aspect<?>> builtInRegistryHolder()
    {
        return _registryHolder;
    }
}
