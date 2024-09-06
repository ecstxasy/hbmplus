package com.ecstasy.hbmplus.Items.BedrockOres;

import net.minecraft.item.ItemStack;
import com.hbm.world.feature.BedrockOre;
import com.hbm.world.feature.BedrockOre.BedrockOreDefinition;
import com.hbm.config.WorldConfig;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;

public class BedrockOres {
    public static void Init() {

        // overworld ores

        BedrockOre.registerBedrockOre(BedrockOre.weightedOres,
            new BedrockOreDefinition(
                new ItemStack(ModItems.crystal_rare, 1),
                2,
                0x6E7F80,
                new FluidStack(Fluids.PEROXIDE, 500)
            ),

            WorldConfig.bedrockRareEarthSpawn
        );

        //nether ores

        BedrockOre.registerBedrockOre(BedrockOre.weightedOresNether,
            new BedrockOreDefinition(
                new ItemStack(ModItems.crystal_phosphorus, 1),
                1,
                0xD7341F
            ),

            WorldConfig.bedrockGlowstoneSpawn
        );

    }
}
