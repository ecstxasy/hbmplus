package com.ecstasy.hbmplus.AE2.FluidImport;

import com.ecstasy.hbmplus.Shared.ModLogger;
import com.google.common.collect.ImmutableSet;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIdentifier;

import api.hbm.fluid.IFluidStandardSender;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.config.YesNo;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEItemStack;
import appeng.client.texture.CableBusTextures;
import appeng.core.AELog;
import appeng.core.settings.TickRates;
import appeng.core.sync.GuiBridge;
import appeng.helpers.MultiCraftingTracker;
import appeng.helpers.Reflected;
import appeng.me.GridAccessException;
import appeng.parts.automation.PartSharedItemBus;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class PartFluidImport extends PartSharedItemBus implements ICraftingRequester, IFluidStandardSender {
    private final MultiCraftingTracker craftingTracker = new MultiCraftingTracker(this, 9);
    private final BaseActionSource mySrc;
    private long itemToSend = 1;
    private boolean didSomething = false;
    private int nextSlot = 0;

    public FluidTank tank;
    public boolean isLoaded = true;
    private static final int FLUID_FILTER_SLOT = 4;

    private ItemStack fluidFilterProxyItem;
	
    public void setFluidFilter(ItemStack proxyItem) {
        this.fluidFilterProxyItem = proxyItem;
    }

    private FluidType getFluidTypeFromProxy(ItemStack proxyItem) {
        if (proxyItem.isItemEqual(new ItemStack(ModItems.fluid_icon))) {
            ModLogger.logger.info(Fluids.fromID(proxyItem.getItemDamage()).getLocalizedName());
            return Fluids.fromID(proxyItem.getItemDamage());
        }
        return Fluids.NONE;
    }

    @SuppressWarnings("unused")
    private FluidType getFluidTypeFromFilterSlot() {
        ItemStack filterStack = this.getInventoryByName("config").getStackInSlot(FLUID_FILTER_SLOT);
        if (filterStack != null && filterStack.getItem() instanceof ItemFluidIdentifier) {
            // Convert the proxy item to a FluidType
            return getFluidTypeFromProxy(filterStack);
        }
        return Fluids.NONE;
    }

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}


    @Reflected
    public PartFluidImport(final ItemStack is) {
        super(is);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager().registerSetting(Settings.CRAFT_ONLY, YesNo.NO);
        this.getConfigManager().registerSetting(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
        this.mySrc = new MachineSource(this);
        this.tank = new FluidTank(Fluids.NONE, 24000);
    }

    @Override
	public FluidTank[] getAllTanks() {
		return new FluidTank[] {tank};
	}

	@Override
	public FluidTank[] getSendingTanks() {
		return new FluidTank[] {tank};
	}

     @Override
    public void readFromNBT(final NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.craftingTracker.readFromNBT(extra);
        this.nextSlot = extra.getInteger("nextSlot");
    }

    @Override
    public void writeToNBT(final NBTTagCompound extra) {
        super.writeToNBT(extra);
        this.craftingTracker.writeToNBT(extra);
        extra.setInteger("nextSlot", this.nextSlot);
    }

    @Override
protected TickRateModulation doBusWork() {
    if (!this.getProxy().isActive() || !this.canDoBusWork()) {
        return TickRateModulation.IDLE;
    }

    this.itemToSend = this.calculateItemsToSend();
    this.didSomething = false;

    final InventoryAdaptor destination = this.getHandler();
    final SchedulingMode schedulingMode = (SchedulingMode) this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);

    if (destination != null) {
        // Use the fluid filter to determine what fluid to export
        FluidType fluidTypeToExport = getFluidTypeFromProxy(fluidFilterProxyItem);

        ForgeDirection dir = this.getSide();
        ForgeDirection rot = dir.getRotation(ForgeDirection.DOWN);
//            IAEItemStack addedStack = AEItemStack.create(destination.addItems(items.getItemStack()));

        int x = this.getTile().xCoord + dir.offsetX * 1 + rot.offsetX * 0;
        int y = this.getTile().yCoord;
        int z = this.getTile().zCoord + dir.offsetZ * 1 + rot.offsetZ * 0;

        this.trySubscribe(fluidTypeToExport, this.getTile().getWorldObj(), x, y, z, getSide());

        if (fluidTypeToExport != Fluids.NONE) {
            // Convert FluidType to FluidStack for HBM's system
            long fluidAmountToExport = getDemand(fluidTypeToExport, 0); // Adjust pressure as needed

            if (fluidAmountToExport > 0) {
                long remainingFluid = transferFluid(fluidTypeToExport, 0, fluidAmountToExport);
                if (remainingFluid < fluidAmountToExport) {
                    this.didSomething = true;
                    // Log or handle successful export
                }
            }
        }

        this.updateSchedulingMode(schedulingMode, x);
    } else {
        return TickRateModulation.SLEEP;
    }

    return this.didSomething ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
}

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(4, 4, 12, 12, 12, 14);
        bch.addBox(5, 5, 14, 11, 11, 15);
        bch.addBox(6, 6, 15, 10, 10, 16);
        bch.addBox(6, 6, 11, 10, 10, 12);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(final IPartRenderHelper rh, final RenderBlocks renderer) {
        rh.setTexture(
                CableBusTextures.PartExportSides.getIcon(),
                CableBusTextures.PartExportSides.getIcon(),
                CableBusTextures.PartMonitorBack.getIcon(),
                this.getItemStack().getIconIndex(),
                CableBusTextures.PartExportSides.getIcon(),
                CableBusTextures.PartExportSides.getIcon());

        rh.setBounds(4, 4, 12, 12, 12, 14);
        rh.renderInventoryBox(renderer);

        rh.setBounds(5, 5, 14, 11, 11, 15);
        rh.renderInventoryBox(renderer);

        rh.setBounds(6, 6, 15, 10, 10, 16);
        rh.renderInventoryBox(renderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(final int x, final int y, final int z, final IPartRenderHelper rh,
            final RenderBlocks renderer) {
        this.setRenderCache(rh.useSimplifiedRendering(x, y, z, this, this.getRenderCache()));
        rh.setTexture(
                CableBusTextures.PartExportSides.getIcon(),
                CableBusTextures.PartExportSides.getIcon(),
                CableBusTextures.PartMonitorBack.getIcon(),
                this.getItemStack().getIconIndex(),
                CableBusTextures.PartExportSides.getIcon(),
                CableBusTextures.PartExportSides.getIcon());

        rh.setBounds(4, 4, 12, 12, 12, 14);
        rh.renderBlock(x, y, z, renderer);

        rh.setBounds(5, 5, 14, 11, 11, 15);
        rh.renderBlock(x, y, z, renderer);

        rh.setBounds(6, 6, 15, 10, 10, 16);
        rh.renderBlock(x, y, z, renderer);

        rh.setTexture(
                CableBusTextures.PartMonitorSidesStatus.getIcon(),
                CableBusTextures.PartMonitorSidesStatus.getIcon(),
                CableBusTextures.PartMonitorBack.getIcon(),
                this.getItemStack().getIconIndex(),
                CableBusTextures.PartMonitorSidesStatus.getIcon(),
                CableBusTextures.PartMonitorSidesStatus.getIcon());

        rh.setBounds(6, 6, 11, 10, 10, 12);
        rh.renderBlock(x, y, z, renderer);

        this.renderLights(x, y, z, rh, renderer);
    }

    @Override
    public int cableConnectionRenderTo() {
        return 5;
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final Vec3 pos) {
        if (!player.isSneaking()) {
            if (Platform.isClient()) {
                return true;
            }

            Platform.openGUI(player, this.getHost().getTile(), this.getSide(), GuiBridge.GUI_BUS);
            return true;
        }

        return false;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(TickRates.ExportBus.getMin(), TickRates.ExportBus.getMax(), this.isSleeping(), false);
    }

    @Override
    public RedstoneMode getRSMode() {
        return (RedstoneMode) this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        return this.doBusWork();
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.craftingTracker.getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(final ICraftingLink link, final IAEItemStack items, final Actionable mode) {
        final InventoryAdaptor d = this.getHandler();

        try {
            if (d != null && this.getProxy().isActive()) {
                final IEnergyGrid energy = this.getProxy().getEnergy();
                final double power = items.getStackSize();

                if (energy.extractAEPower(power, mode, PowerMultiplier.CONFIG) > power - 0.01) {
                    if (mode == Actionable.MODULATE) {
                        return AEItemStack.create(d.addItems(items.getItemStack()));
                    }
                    return AEItemStack.create(d.simulateAdd(items.getItemStack()));
                }
            }
        } catch (final GridAccessException e) {
            AELog.debug(e);
        }

        return items;
    }

    @Override
    public void jobStateChange(final ICraftingLink link) {
        this.craftingTracker.jobStateChange(link);
    }

    @Override
    protected boolean isSleeping() {
        return this.getHandler() == null || super.isSleeping();
    }

    @SuppressWarnings("unused")
    private boolean craftOnly() {
        return this.getConfigManager().getSetting(Settings.CRAFT_ONLY) == YesNo.YES;
    }

    @SuppressWarnings("unused")
    private boolean isCraftingEnabled() {
        return this.getInstalledUpgrades(Upgrades.CRAFTING) > 0;
    }

    @SuppressWarnings("unused")
    private void pushItemIntoTarget(final InventoryAdaptor d, final IEnergyGrid energy,
            final IMEInventory<IAEItemStack> inv, IAEItemStack ais) {
        final ItemStack is = ais.getItemStack();
        is.stackSize = (int) this.itemToSend;

        final ItemStack o = d.simulateAdd(is);
        final long canFit = o == null ? this.itemToSend : this.itemToSend - o.stackSize;

        if (canFit > 0) {
            ais = ais.copy();
            ais.setStackSize(canFit);
            final IAEItemStack itemsToAdd = Platform.poweredExtraction(energy, inv, ais, this.mySrc);

            if (itemsToAdd != null) {
                this.itemToSend -= itemsToAdd.getStackSize();

                final ItemStack failed = d.addItems(itemsToAdd.getItemStack());
                if (failed != null) {
                    ais.setStackSize(failed.stackSize);
                    inv.injectItems(ais, Actionable.MODULATE, this.mySrc);
                } else {
                    this.didSomething = true;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private int getStartingSlot(final SchedulingMode schedulingMode, final int x) {
        if (schedulingMode == SchedulingMode.RANDOM) {
            return Platform.getRandom().nextInt(this.availableSlots());
        }

        if (schedulingMode == SchedulingMode.ROUNDROBIN) {
            return (this.nextSlot + x) % this.availableSlots();
        }

        return x;
    }

    private void updateSchedulingMode(final SchedulingMode schedulingMode, final int x) {
        if (schedulingMode == SchedulingMode.ROUNDROBIN) {
            this.nextSlot = (this.nextSlot + x) % this.availableSlots();
        }
    }
}
