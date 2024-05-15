package finitereality.treecipe.recipes;

import finitereality.treecipe.aspect.Aspect;
import finitereality.treecipe.attribute.Attribute;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.Map;

/**
 * Defines an interface that represents a "recipe", a named sequence of steps
 * which transform one {@link Aspect} to another.
 * <p>
 * This is not necessarily equivalent to a {@link net.minecraft.world.item.crafting.Recipe}
 * as some transformations are not necessarily encoded this way.
 */
public interface Recipe
{
    /**
     * Gets the inputs to this recipe.
     *
     * @return A collection of {@link Component}s which encode an input to the
     * recipe.
     */
    Collection<Component> getInputs();

    /**
     * Gets the outputs of this recipe.
     *
     * @return A collection of {@link Component}s which encode an output of the
     * recipe.
     */
    Collection<Component> getOutputs();

    /**
     * Gets any attributes applied to this recipe.
     *
     * @return A map of {@link Attribute}s and their values which are applied
     * to this recipe.
     */
    Map<Attribute<?>, ?> getAttributes();

    interface Component
    {
        ResourceKey<?> getResource();
        Map<Attribute<?>, ?> getAttributes();
    }
}
