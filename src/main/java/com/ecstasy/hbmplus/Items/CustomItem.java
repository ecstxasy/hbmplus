package com.ecstasy.hbmplus.Items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CustomItem extends Item {
    private final String iconName;

    public CustomItem(String name) {
        this.iconName = name;
        setUnlocalizedName(name);
        setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("hbmplus:" + this.iconName);
    }
}