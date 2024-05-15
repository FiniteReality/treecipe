package finitereality.treecipe.neoforge.init;

import finitereality.annotations.common.Static;
import finitereality.treecipe.Treecipe;
import finitereality.treecipe.neoforge.recipes.CookingRecipeProvider;
import finitereality.treecipe.neoforge.recipes.FuelRecipeProvider;
import finitereality.treecipe.neoforge.recipes.MilkingRecipeProvider;
import finitereality.treecipe.neoforge.recipes.PeriodicDropRecipeProvider;
import finitereality.treecipe.neoforge.recipes.PlacedBlockProvider;
import finitereality.treecipe.neoforge.recipes.SmithingRecipeProvider;
import finitereality.treecipe.neoforge.recipes.VanillaRecipeProvider;
import finitereality.treecipe.recipes.RecipeProvider;
import finitereality.treecipe.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Static
public final class RecipeProviderRegistration
{
    private RecipeProviderRegistration() { }

    private static final DeferredRegister<RecipeProvider> _providers
        = DeferredRegister.create(Registries.RECIPE_PROVIDER, Treecipe.MOD_ID);

    public static final DeferredHolder<RecipeProvider, PlacedBlockProvider> PlacingFurnaces
        = _providers.register("placing_furnaces",
            () -> new PlacedBlockProvider(Set.of(
                Blocks.FURNACE,
                Blocks.BLAST_FURNACE,
                Blocks.SMOKER,
                Blocks.CAMPFIRE,
                Blocks.SOUL_CAMPFIRE)));

    public static final DeferredHolder<RecipeProvider, MilkingRecipeProvider> CowMilking
        = _providers.register("cow_milking",
            () -> new MilkingRecipeProvider(
                EntityType.COW,
                Items.BUCKET,
                Items.MILK_BUCKET));

    public static final DeferredHolder<RecipeProvider, PeriodicDropRecipeProvider> ChickenEggs
        = _providers.register("chicken_egg",
            () -> new PeriodicDropRecipeProvider(
                EntityType.CHICKEN,
                Set.of(Items.EGG)));

    public static final DeferredHolder<RecipeProvider, FuelRecipeProvider> FurnaceFuels
        = _providers.register("furnace_fuels",
            furnaceFuelsType(Blocks.FURNACE));
    public static final DeferredHolder<RecipeProvider, FuelRecipeProvider> BlastFurnaceFuels
        = _providers.register("blast_furnace_fuels",
            furnaceFuelsType(Blocks.BLAST_FURNACE));
    public static final DeferredHolder<RecipeProvider, FuelRecipeProvider> SmokerFuels
        = _providers.register("smoker_fuels",
            furnaceFuelsType(Blocks.SMOKER));

    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> Blasting
        = _providers.register("blasting", furnaceRecipeType(
            BlastingRecipe.class,
            RecipeType.BLASTING,
            Set.of(Blocks.BLAST_FURNACE)));
    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> CampfireCooking
        = _providers.register("campfire_cooking", furnaceRecipeType(
            CampfireCookingRecipe.class,
            RecipeType.CAMPFIRE_COOKING,
            Set.of(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE)));
    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> Smelting
        = _providers.register("smelting", furnaceRecipeType(
            SmeltingRecipe.class,
            RecipeType.SMELTING,
            Set.of(Blocks.FURNACE)));
    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> Smoking
        = _providers.register("smoking", furnaceRecipeType(
            SmokingRecipe.class,
            RecipeType.SMOKING,
            Set.of(Blocks.SMOKER)));

    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> ShapelessCrafting
        = _providers.register("shapeless_crafting", vanillaRecipeType(
            ShapelessRecipe.class,
        RecipeType.CRAFTING));
    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> ShapedCrafting
        = _providers.register("shaped_crafting", vanillaRecipeType(
            ShapedRecipe.class,
        RecipeType.CRAFTING));

    // TODO: make these use the smithing table and stonecutter
    // TODO: Trim recipes are... special bunnies.
    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> SmithingTransform
        = _providers.register("smithing_transform",
            () -> new SmithingRecipeProvider<>(
                SmithingTransformRecipe.class,
                RecipeType.SMITHING));
    public static final DeferredHolder<RecipeProvider, VanillaRecipeProvider<?>> Stonecutting
        = _providers.register("stonecutting", vanillaRecipeType(
            StonecutterRecipe.class,
        RecipeType.STONECUTTING));

    private static <T extends Recipe<?>> Supplier<VanillaRecipeProvider<T>> vanillaRecipeType(
        final Class<T> clazz,
        final RecipeType<? super T> recipeType)
    {
        return () -> new VanillaRecipeProvider<>(clazz, recipeType);
    }

    private static Supplier<FuelRecipeProvider> furnaceFuelsType(final Block block)
    {
        return () -> new FuelRecipeProvider(
            BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
    }

    private static <T extends AbstractCookingRecipe> Supplier<VanillaRecipeProvider<T>> furnaceRecipeType(
        final Class<T> clazz,
        final RecipeType<? super T> recipeType,
        final Set<Block> blocks)
    {
        return () -> new CookingRecipeProvider<>(
            clazz,
            recipeType,
            blocks.stream()
                .map(BuiltInRegistries.BLOCK::getResourceKey)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet()));
    }

    public static void register(final IEventBus bus)
    {
        _providers.register(bus);
    }
}
