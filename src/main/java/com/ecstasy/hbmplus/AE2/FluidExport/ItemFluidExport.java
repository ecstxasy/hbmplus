package com.ecstasy.hbmplus.AE2.FluidExport;

import javax.annotation.Nullable;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFluidExport extends Item implements IPartItem {
    public ItemFluidExport() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("fluid_export");
        AEApi.instance().partHelper().setItemBusRenderer(this);
    }

    @Nullable
    @Override
    public PartFluidExport createPartFromItemStack(ItemStack is) {
        return new PartFluidExport(is);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        return AEApi.instance().partHelper().placeBus(player.getHeldItem(), x, y, z, side, player, world);
    }

    public ItemFluidExport register() {
        GameRegistry.registerItem(this, "fluid_export", "hbmplus");
        setCreativeTab(CreativeTabs.tabCombat);
        return this;
    }

    @Override
    public void registerIcons(IIconRegister _iconRegister) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    public ItemStack stack(int size) {
        return new ItemStack(this, size);
    }

    public ItemStack stack() {
        return new ItemStack(this, 1);
    }
}
