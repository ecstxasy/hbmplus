package com.ecstasy.hbmplus.Recipes;

import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.ecstasy.hbmplus.Blocks.InitBlocks;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.recipes.AssemblerRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCircuit.EnumCircuitType;

import net.minecraft.item.Item;

public class Assembler {
    public static void Init() {

        AssemblerRecipes.makeRecipe(
            new ComparableStack(ModItems.tsar_core, 1), 
            new AStack[] {
                new OreDictStack(OreDictManager.U238.nugget(), 48),
                new OreDictStack(OreDictManager.IRON.plate(), 24),
                new OreDictStack(OreDictManager.STEEL.plate(), 32),
                new ComparableStack(ModItems.cell_deuterium, 15),
                new ComparableStack(ModItems.powder_lithium, 15)
            },

            30 * 20
        );

        AssemblerRecipes.makeRecipe(
            new ComparableStack(Item.getItemFromBlock(InitBlocks.machine_shredder_large), 1), 
            new AStack[] {
                new OreDictStack(OreDictManager.STEEL.plate(), 32),
                new OreDictStack(OreDictManager.ANY_RESISTANTALLOY.ingot(), 24),
                new ComparableStack(ModItems.motor_desh, 12),
                new ComparableStack(ModItems.circuit, 6, EnumCircuitType.ADVANCED.ordinal()),
                new ComparableStack(ModItems.blades_desh, 2)
            },

            30 * 20
        );

        
    }
}
