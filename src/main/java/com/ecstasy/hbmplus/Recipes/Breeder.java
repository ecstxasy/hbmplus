package com.ecstasy.hbmplus.Recipes;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.BreederRecipes;
import com.hbm.items.ModItems;

import java.util.HashMap;

import com.ecstasy.hbmplus.Items.ItemCustomBreedingRod;
import com.ecstasy.hbmplus.Items.ItemsMod;

public class Breeder {
    private Object recipes;

    public Breeder() {
        BreederRecipes breederRecipes = new BreederRecipes();
        this.recipes = breederRecipes.getRecipeObject();
    }

    public void addRecipe(ComparableStack input, ComparableStack output, int flux) {
        @SuppressWarnings("unchecked")
        HashMap<ComparableStack, BreederRecipes.BreederRecipe> recipesMap = (HashMap<ComparableStack, BreederRecipes.BreederRecipe>) recipes;
        recipesMap.put(input, new BreederRecipes.BreederRecipe(output.toStack(), flux));
    }

    public void addRecipe(ItemCustomBreedingRod inputRod, ItemCustomBreedingRod outputRod, int flux) {
        addRecipe(new ComparableStack(inputRod.rod, 1), new ComparableStack(outputRod.rod, 1), flux);
        addRecipe(new ComparableStack(inputRod.dualRod, 1), new ComparableStack(outputRod.dualRod, 1), flux * 2);
        addRecipe(new ComparableStack(inputRod.quadRod, 1), new ComparableStack(outputRod.quadRod, 1), flux * 4);
    }

    public void addRecipe(int meta, ItemCustomBreedingRod outputRod, int flux) {
        addRecipe(new ComparableStack(ModItems.rod, 1, meta), new ComparableStack(outputRod.rod, 1), flux);
        addRecipe(new ComparableStack(ModItems.rod_dual, 1, meta), new ComparableStack(outputRod.dualRod, 1), flux * 2);
        addRecipe(new ComparableStack(ModItems.rod_quad, 1, meta), new ComparableStack(outputRod.quadRod, 1), flux * 4);
    }

    public static void Init() {
        Breeder breeder = new Breeder();

        breeder.addRecipe(
            ItemsMod.BreedingPu240,
            ItemsMod.BreedingPu241,
            300
        );

        breeder.addRecipe(
            ItemsMod.BreedingPu241,
            ItemsMod.BreedingAmMix,
            200
        );
    }
}
