package com.ecstasy.hbmplus.Machines.MachineShredderLarge;

import org.lwjgl.opengl.GL11;

import com.ecstasy.hbmplus.Blocks.InitBlocks;
import com.ecstasy.hbmplus.Shared.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.tileentity.IItemRendererProvider;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class RenderMachineShredder extends TileEntitySpecialRenderer implements IItemRendererProvider {

    private double side = 0.3D;
    private double height = 2.75D;
    
    @Override public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y, z + 0.5D);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
    
        GL11.glRotatef(90, 0F, 1F, 0F);
        switch(tileEntity.getBlockMetadata()) {
            case 12:
                GL11.glRotatef(0, 0F, 1F, 0F);
                break;
            case 13:
                GL11.glRotatef(180, 0F, 1F, 0F);
                break;
            case 14:
                GL11.glRotatef(90, 0F, 1F, 0F);
                break;
            case 15:
                GL11.glRotatef(-90, 0F, 1F, 0F);
                break;
        }

        bindTexture(ResourceManager.shredder_texture);
        ResourceManager.shredder.renderPart("Base");

        TileEntityMachineShredderLarge shredderTileEntity = (TileEntityMachineShredderLarge) tileEntity;

        spin(shredderTileEntity);
    
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

    private void spin(TileEntityMachineShredderLarge shredderTileEntity) {
        float rot = (shredderTileEntity != null && shredderTileEntity.power > 0) || shredderTileEntity == null ? (System.currentTimeMillis() / 5) % 360 : 0;

        GL11.glPushMatrix();
        GL11.glTranslated(side, height, 0);
        GL11.glRotatef(rot, 0F, 0F, 1F);
        ResourceManager.shredder.renderPart("Blades1");
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(-side, height, 0);
        GL11.glRotatef(rot, 0F, 0F, -1F);
        ResourceManager.shredder.renderPart("Blades2");
        GL11.glPopMatrix();
    }

    @Override public Item getItemForRenderer() {
		return Item.getItemFromBlock(InitBlocks.machine_shredder_large);
	}

    

	@Override public IItemRenderer getRenderer() {
		return new ItemRenderBase( ) {
			public void renderInventory() {
				GL11.glTranslated(0, -4, 0);
				GL11.glScaled(2.5, 2.5, 2.5);
			}
			public void renderCommon() {
				GL11.glScaled(1, 1, 1);
				GL11.glShadeModel(GL11.GL_SMOOTH);
				bindTexture(ResourceManager.shredder_texture);
                ResourceManager.shredder.renderPart("Base");
                spin(null);
				GL11.glShadeModel(GL11.GL_FLAT);
			}
		};
	}
}
