package com.ecstasy.hbmplus.AE2.HBMFluidInterface;

import java.util.EnumSet;

import com.hbm.main.MainRegistry;

import appeng.block.AEBaseItemBlock;
import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHBMFluidInterface extends AEBaseTileBlock {
    public static String name = "hbm_fluid_interface";

    public BlockHBMFluidInterface(Material mat) {
        super(mat);

        this.setBlockName(name);
        this.setBlockTextureName("hbmplus:" + name);

        setFullBlock(true);
        setOpaque(true);
        setTileEntity(TileEntityHBMFluidInterface.class);
        setFeature(EnumSet.of(AEFeature.Core));
    }

    public void setOpaque(boolean opaque) {
        this.isOpaque = opaque;
    }

    public void setFullBlock(boolean full) {
        this.isFullSize = full;
    }

    @Override public TileEntity createNewTileEntity(World world, int a) {
        return new TileEntityHBMFluidInterface();
    }

    @Override
    public void setFeature( final EnumSet<AEFeature> f ) {
        super.setFeature(f);
    }

    @Override
    public void setTileEntity( final Class<? extends TileEntity> clazz ) {
        super.setTileEntity(clazz);
    }

    @Override
    public boolean onActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int facing, final float hitX, final float hitY, final float hitZ )
    {
        if(world.isRemote) {
			return true;
		} else if(!player.isSneaking()) {

			FMLNetworkHandler.openGui(player, MainRegistry.instance, 0, world, x, y, z);
			return true;
		} else {
			return false;
		}
    }

    public BlockHBMFluidInterface register() {
        GameRegistry.registerBlock(this, AEBaseItemBlock.class, name);
        GameRegistry.registerTileEntity(TileEntityHBMFluidInterface.class, name);
        setCreativeTab(CreativeTabs.tabCombat);
        return this;
    }

}
