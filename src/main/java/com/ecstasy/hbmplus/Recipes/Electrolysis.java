package com.ecstasy.hbmplus.Recipes;

import com.ecstasy.hbmplus.Fluids.CustomFluids;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes.ElectrolysisRecipe;

public class Electrolysis {
    public static void init() {
        final ElectrolysisRecipe chlorAlkali = new ElectrolysisRecipe(
            1000,
            new FluidStack(Fluids.HYDROGEN, 500),
            new FluidStack(Fluids.CHLORINE, 500)
        );

        ElectrolyserFluidRecipes.recipes.put(CustomFluids.SALTWATER, chlorAlkali);
    }
}