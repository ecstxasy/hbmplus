package com.ecstasy.hbmplus.Machines.MegaCooler;

import com.hbm.blocks.machine.WasteDrum;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MegaCooler extends WasteDrum {
    public MegaCooler(Material mat) {
        super(mat);
    }

    @Override public TileEntity createNewTileEntity(World world, int hi) {
		return new TileEntityMegaCooler();
	}
}
