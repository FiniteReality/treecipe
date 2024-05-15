package finitereality.treecipe.attribute;

import finitereality.annotations.common.Static;
import finitereality.treecipe.Treecipe;
import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Defines a type containing definitions of commonly-used attributes.
 */
@Static
public final class Attributes
{
    private Attributes() { }

    /**
     * The burn time attribute, indicating that this aspect requires, or
     * provides some number of ticks to burn.
     * <p>
     * This attribute is useful for machines like a furnace, which use a fuel
     * to provide some amount of burn time.
     */
    public static final Attribute<Integer> BURN_TIME
        = key(Integer.class, "burn_time");

    /**
     * The count attribute, indicating that this aspect requires, or provides a
     * certain count of values.
     * <p>
     * This attribute is useful for "recipes" like mob drops, which may drop
     * one or more items.
     */
    public static final Attribute<Integer> COUNT
        = key(Integer.class, "count");

    /**
     * The craft time attribute, indicating that this recipe takes some number
     * of ticks to craft.
     * <p>
     * This attribute is useful for machines like a furnace, which do not craft
     * instantly.
     */
    public static final Attribute<Integer> CRAFT_TIME
        = key(Integer.class, "craft_time");

    /**
     * The craft time attribute, indicating that this recipe takes some amount
     * of power to craft.
     * <p>
     * This attribute is useful for machines which use a unit of power to
     * craft, such as a powered furnace
     */
    public static final Attribute<Integer> POWER_COST
        = key(Integer.class, "power_cost");

    private static <T> Attribute<T> key(final Class<T> clazz, final String name)
    {
        return Attribute.named(
            new ResourceLocation(Treecipe.MOD_ID, name),
            clazz);
    }
}
