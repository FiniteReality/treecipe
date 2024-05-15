package finitereality.treecipe.neoforge.mixins;

import finitereality.treecipe.neoforge.util.IGetSmithingIngredientsAccessor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SmithingTransformRecipe.class, SmithingTrimRecipe.class})
public abstract class SmithingRecipeMixin implements IGetSmithingIngredientsAccessor
{
    @Accessor("template")
    public abstract Ingredient getTemplateIngredient();

    @Accessor("base")
    public abstract Ingredient getBaseIngredient();

    @Accessor("addition")
    public abstract Ingredient getAdditionIngredient();
}
