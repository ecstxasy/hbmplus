package com.ecstasy.hbmplus.AE2;

import com.ecstasy.hbmplus.AE2.FluidExport.ItemFluidExport;
import com.ecstasy.hbmplus.AE2.FluidImport.ItemFluidImport;
import com.ecstasy.hbmplus.AE2.HBMFluidInterface.BlockHBMFluidInterface;

import net.minecraft.block.material.Material;

@SuppressWarnings("unused")
public class InitAE2 {
    public static ItemFluidExport fluidExport = new ItemFluidExport();
    public static ItemFluidImport fluidImport = new ItemFluidImport();
//    public static BlockHBMFluidInterface hbmFluidInterface = new BlockHBMFluidInterface(Material.iron);

    public static void Init() {
        fluidExport.register();
        fluidImport.register();
//        hbmFluidInterface.register();
    }
}
