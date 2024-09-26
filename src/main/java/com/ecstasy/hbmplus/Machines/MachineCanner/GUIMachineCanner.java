package com.ecstasy.hbmplus.Machines.MachineCanner;

import org.lwjgl.opengl.GL11;

import com.hbm.inventory.gui.GuiInfoContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIMachineCanner extends GuiInfoContainer {

    private static final ResourceLocation texture = new ResourceLocation("hbmplus:textures/gui/gui_canner.png");
    private TileEntityMachineCanner machine;

    public GUIMachineCanner(InventoryPlayer invPlayer, TileEntityMachineCanner machine) {
        super(new ContainerMachineCanner(invPlayer, machine));
        this.machine = machine;

        this.xSize = 176;
        this.ySize = 222;
    }

    @SuppressWarnings("static-access")
	@Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);

        this.drawElectricityInfo(this, mouseX, mouseY, guiLeft+8, guiTop+18, 16, 87, machine.power, machine.maxPower);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j) {
        String name = this.machine.hasCustomInventoryName() ? this.machine.getInventoryName() : I18n.format(this.machine.getInventoryName());
        
        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        if(machine.power > 0) {
            int powerHeight = (int) machine.getPowerScaled(87);
            drawTexturedModalRect(guiLeft+8, guiTop+18 + (87 - powerHeight), 176, 0, 16, powerHeight);
        }

		if (machine.isProgressing && machine.progress > 0) {
			int progressHeight = (int) machine.getCentrifugeProgressScaled(31);
			drawTexturedModalRect(guiLeft + 98, guiTop + 55, 192, 0, 16, progressHeight);
		}
        
    }
    
}
