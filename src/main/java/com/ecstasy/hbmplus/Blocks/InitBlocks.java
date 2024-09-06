package com.ecstasy.hbmplus.Blocks;

import com.ecstasy.hbmplus.Machines.MachineShredderLarge.MachineShredderLarge;
import com.ecstasy.hbmplus.Machines.MegaCooler.MegaCooler;
import com.hbm.main.MainRegistry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class InitBlocks {
    public static Block cooler = new MegaCooler(Material.iron).setBlockName("machine_mega_cooler").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.machineTab).setBlockTextureName("hbm:waste_drum");
    public static Block machine_shredder_large = new MachineShredderLarge().setBlockName("machine_shredder_large").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.machineTab).setBlockTextureName("hbm:code");

    public static void Init() {
        GameRegistry.registerBlock(cooler, cooler.getUnlocalizedName());
        GameRegistry.registerBlock(machine_shredder_large, machine_shredder_large.getUnlocalizedName());
    }
}
