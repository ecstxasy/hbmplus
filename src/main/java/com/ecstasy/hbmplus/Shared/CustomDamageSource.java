package com.ecstasy.hbmplus.Shared;

import net.minecraft.util.DamageSource;

public class CustomDamageSource extends DamageSource {
    public static DamageSource blender = (new DamageSource("blender").setDamageIsAbsolute().setDamageBypassesArmor());

    public CustomDamageSource(String name) {
		super(name);
	}

}
