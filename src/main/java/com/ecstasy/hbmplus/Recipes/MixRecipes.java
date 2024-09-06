package com.ecstasy.hbmplus.Recipes;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.MixerRecipes;
import com.hbm.items.ModItems;

public class MixRecipes {

    public static class CustomMixerRecipe extends MixerRecipes.MixerRecipe {
        public CustomMixerRecipe(int output, int processTime) {
            super(output, processTime);
        }

        public CustomMixerRecipe setStack1(FluidStack stack) { input1 = stack; return this; }
        public CustomMixerRecipe setStack2(FluidStack stack) { input2 = stack; return this; }
        public CustomMixerRecipe setSolid(AStack stack) { solidInput = stack; return this; }
    }

    public static void Init() {
        MixerRecipes.register(
            Fluids.WASTEFLUID,
            new CustomMixerRecipe(1000, 60)
            .setSolid(new ComparableStack(ModItems.nuclear_waste))
            .setStack1(new FluidStack(Fluids.NITRIC_ACID, 1000))
        );
    }
}
