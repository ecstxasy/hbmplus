package com.ecstasy.hbmplus.Recipes;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.ArcWelderRecipes;
import com.hbm.inventory.recipes.ArcWelderRecipes.ArcWelderRecipe;
import com.hbm.items.ModItems;

import net.minecraft.item.ItemStack;

public class ArcWelder {
    public static void Init() {

        // yeah im not waiting 5 mins per plate. for the normal length recipes that got caught in the crossfire, idgaf im too lazy lol
        for (ArcWelderRecipe recipe : ArcWelderRecipes.recipes) {
            recipe.duration = Math.max(recipe.duration / 5, 1); // i put this here because im scared its gonna round down to 0 which would (probably) break the recipe
        }

        ArcWelderRecipes.recipes.add(
            new ArcWelderRecipe(
                new ItemStack(ModItems.plate_dineutronium, 4),
                3 * 20,
                5000000,
                new FluidStack(Fluids.REFORMGAS, 1000),
                new AStack[] {
                    new OreDictStack(OreDictManager.DNT.ingot(), 4),
                    new ComparableStack(ModItems.powder_spark_mix, 2),
                    new OreDictStack(OreDictManager.DESH.ingot(), 1)
                }
            )
            
        );

        ArcWelderRecipes.recipes.add(
            new ArcWelderRecipe(
                new ItemStack(ModItems.plate_desh, 4),
                2 * 20,
                250000,
                new FluidStack(Fluids.OXYGEN, 1000),
                new AStack[] {
                    new OreDictStack(OreDictManager.DESH.ingot(), 4),
                    new OreDictStack(OreDictManager.ANY_PLASTIC.dust(), 2),
                    new OreDictStack(OreDictManager.DURA.ingot(), 1)
                }
            )
            
        );
    }
}
