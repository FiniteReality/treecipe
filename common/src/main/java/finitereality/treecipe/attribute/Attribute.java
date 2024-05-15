package finitereality.treecipe.attribute;

import net.minecraft.resources.ResourceLocation;

/**
 * Defines an interface that represents an "attribute", a non-critical piece of
 * information attached to a unique recipe.
 *
 * @param <T> The value type of this attribute.
 */
public interface Attribute<T>
{
    /**
     * Creates an attribute for the given type.
     * @param name A name which can be used for debugging.
     * @param clazz The type of the attribute.
     * @param <T> The type of attribute.
     * @return An {@link Attribute} with the given type.
     */
    static <T> Attribute<T> named(
        final ResourceLocation name,
        final Class<T> clazz)
    {
        return new Attribute<T>() {
            @Override
            public Class<T> getAttributeType()
            {
                return clazz;
            }

            @Override
            public String toString()
            {
                return "Attribute[" + name.toString() + "]";
            }
        };
    }

    /**
     * Gets the {@link Class} representing the type of value this attribute
     * represents.
     *
     * @return A {@link Class} of type {@link T}.
     */
    Class<T> getAttributeType();
}
