package com.ecstasy.hbmplus.Recipes;

import com.hbm.inventory.recipes.ChemplantRecipes;
import com.hbm.inventory.recipes.ChemplantRecipes.ChemRecipe;
import com.hbm.items.ModItems;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;

public class ChemPlant {

    public static void Init () {
        final int BaseLevel = 1500; // ok

        final ChemRecipe ENCHANTMENT = new ChemRecipe(BaseLevel + 1, "ENCHANTMENT", 5 * 20);
        ChemplantRecipes.recipes.add(ENCHANTMENT);

        final ChemRecipe EXPERIENCE = new ChemRecipe(BaseLevel + 2, "EXPERIENCE", 3 * 20);
        ChemplantRecipes.recipes.add(EXPERIENCE);

        ENCHANTMENT.inputItems(new AStack[] {
            new ComparableStack(ModItems.powder_quartz, 4)
        });

        ENCHANTMENT.inputFluids(new FluidStack(
            Fluids.XENON, 1000
        ));

        ENCHANTMENT.outputItems(new ItemStack[] {
            new ItemStack(ModItems.powder_magic, 1)
        });


        EXPERIENCE.inputItems(new AStack[] {
           new ComparableStack(ModItems.powder_magic),
           new ComparableStack(ModItems.powder_lapis, 4) 
        });

        EXPERIENCE.outputFluids(
            new FluidStack(Fluids.XPJUICE, 1000)
        );

        
    }
}
