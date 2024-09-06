package com.ecstasy.hbmplus.Shared;

import com.hbm.render.loader.HFRWavefrontObject;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

public class ResourceManager {
    public static final ResourceLocation shredder_texture = new ResourceLocation("hbmplus", "textures/models/machines/shredder.png");
	public static final IModelCustom shredder = new HFRWavefrontObject(new ResourceLocation("hbmplus", "models/machines/shredder.obj"));
}