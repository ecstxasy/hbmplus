package com.ecstasy.hbmplus.Machines.MachineShredderLarge;

import com.ecstasy.hbmplus.Util.Vector2;
import com.hbm.inventory.SlotCraftingOutput;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemMachineUpgrade;

import api.hbm.energymk2.IBatteryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMachineShredderLarge extends Container {

    private TileEntityMachineShredderLarge triFurnace;

    public ContainerMachineShredderLarge(InventoryPlayer invPlayer, TileEntityMachineShredderLarge tileEntityMachineShredderLarge) {
        this.triFurnace = tileEntityMachineShredderLarge;

	  	Vector2 inputStart = new Vector2(44, 18);
		Vector2 outputStart = new Vector2(44, 90);
		int s = 0;

		// Input slots (7x2 grid)
		for (int row = 0; row < 2; row++) {
    		for (int col = 0; col < 7; col++) {
        		this.addSlotToContainer(new Slot(tileEntityMachineShredderLarge, s, (int)inputStart.x + col * 18, (int)inputStart.y + row * 18));
				s++;
    		}
		}

		// output slots (also 7x2)
		for (int row = 0; row < 2; row++) {
    		for (int col = 0; col < 7; col++) {
        		this.addSlotToContainer(new SlotCraftingOutput(invPlayer.player, tileEntityMachineShredderLarge, s, (int)outputStart.x + col * 18, (int)outputStart.y + row * 18));
				s++;
    		}
		}

		this.addSlotToContainer(new Slot(tileEntityMachineShredderLarge, s, 8, 108)); // battery
		s++;

		this.addSlotToContainer(new Slot(tileEntityMachineShredderLarge, s, 26, 18)); // upgrade slots
		s++;
		this.addSlotToContainer(new Slot(tileEntityMachineShredderLarge, s, 26, 38));
		s++;
		this.addSlotToContainer(new Slot(tileEntityMachineShredderLarge, s, 26, 58));
		s++;

        // Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        // Hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 198));
        }
    }

    @Override public boolean canInteractWith(EntityPlayer player) {
        return triFurnace.isUseableByPlayer(player);
    }

	//i think this is for choosing which slot shift clicking certain items go. idk lol i just copied and pasted it
    @Override public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack rStack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			rStack = stack.copy();
			SlotCraftingOutput.checkAchievements(player, stack);
			
            if (index <= 13) {
				if (!this.mergeItemStack(stack, 13, this.inventorySlots.size(), true)) {
					return null;
				}
			} else {
				
				if(rStack.getItem() instanceof IBatteryItem || rStack.getItem() == ModItems.battery_creative) {
					if(!this.mergeItemStack(stack, 28, 29, false)) return null;
				} else if(rStack.getItem() instanceof ItemMachineUpgrade) {
					if(!this.mergeItemStack(stack, 29, 31, false)) return null;
				} else {
					if(!this.mergeItemStack(stack, 0, 13, false)) return null;
				}
			}
			
			if(stack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if(stack.stackSize == rStack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, rStack);
		}

		return rStack;
	}
}
