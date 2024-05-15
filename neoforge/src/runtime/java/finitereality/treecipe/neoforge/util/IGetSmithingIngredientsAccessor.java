package finitereality.treecipe.neoforge.util;

import net.minecraft.world.item.crafting.Ingredient;

public interface IGetSmithingIngredientsAccessor
{
    Ingredient getTemplateIngredient();
    Ingredient getBaseIngredient();
    Ingredient getAdditionIngredient();
}
