package com.ecstasy.hbmplus.Recipes;

import com.hbm.inventory.recipes.SolderingRecipes;
import com.hbm.inventory.recipes.SolderingRecipes.SolderingRecipe;

public class Soldering {
    public static void Init() {

        // something like a mid-tier circuit (the red military ones) take way too long to craft for how often they are needed. they dont even make that op stuff im just not waiting this long bro
        for (SolderingRecipe recipe : SolderingRecipes.recipes) {
            recipe.duration = Math.min(recipe.duration / 2, 10);
        }

    }
}
