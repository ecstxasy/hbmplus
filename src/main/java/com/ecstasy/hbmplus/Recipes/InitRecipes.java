package com.ecstasy.hbmplus.Recipes;

import java.lang.reflect.Field;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCustomMissilePart;
import com.hbm.tileentity.machine.TileEntityFurnaceCombination;

public class InitRecipes {
    public static void Main() throws IllegalArgumentException, IllegalAccessException {
        Assembler.Init();
        Soldering.Init();
        ChemPlant.Init();
        ArcWelder.Init();
        Breeder.Init();
        MixRecipes.Init();
        Electrolysis.init();

        TileEntityFurnaceCombination.maxHeat *= 10;

        ItemCustomMissilePart bf15 = ((ItemCustomMissilePart) ModItems.mp_warhead_15_balefire);

        for (Field field : bf15.getClass().getFields()) { // it too heavy in the jameh2 fork
            if (field.getName() == "mass") {
                field.set(bf15, 5000);
            }
        }
    }
}