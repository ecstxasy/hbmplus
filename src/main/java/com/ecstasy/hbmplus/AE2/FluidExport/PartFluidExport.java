package com.ecstasy.hbmplus.AE2.FluidExport;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIdentifier;
import com.hbm.util.fauxpointtwelve.DirPos;

import api.hbm.fluid.IFluidConductor;
import api.hbm.fluid.IFluidConnector;
import api.hbm.fluid.IFluidStandardTransceiver;
import api.hbm.fluid.IPipeNet;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.client.texture.CableBusTextures;
import appeng.core.settings.TickRates;
import appeng.core.sync.GuiBridge;
import appeng.helpers.Reflected;
import appeng.me.GridAccessException;
import appeng.parts.automation.PartSharedItemBus;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class PartFluidExport extends PartSharedItemBus implements IFluidStandardTransceiver {
    private final BaseActionSource mySrc;
    private boolean didSomething = false;

    private FluidTank tank;
    private boolean isLoaded = true;
    private static final int FLUID_FILTER_SLOT = 0; // Adjust the slot index if necessary

    @Reflected
    public PartFluidExport(final ItemStack is) {
        super(is);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager().registerSetting(Settings.CRAFT_ONLY, YesNo.NO);
        this.mySrc = new MachineSource(this);
        this.tank = new FluidTank(Fluids.NONE, 24000); // Adjust the capacity as needed
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }



    @Override
    public void readFromNBT(final NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.tank.readFromNBT(extra, "tank");
    }

    @Override
    public void writeToNBT(final NBTTagCompound extra) {
        super.writeToNBT(extra);
        this.tank.writeToNBT(extra, "tank");
    }

    @Override
    protected TickRateModulation doBusWork() {
        if (!this.getProxy().isActive() || !this.canDoBusWork()) {
            return TickRateModulation.IDLE;
        }

        this.didSomething = false;

        try {
            final IMEMonitor<IAEItemStack> itemInventory = this.getProxy().getStorage().getItemInventory();

            // Determine which fluid to export based on the filter slot
            FluidType fluidTypeToExport = getFluidTypeFromFilterSlot();

            if (fluidTypeToExport != Fluids.NONE) {
                // Check if HBM network has demand for the fluid
                if (hasDemandForFluid(fluidTypeToExport)) {
                    // Proceed to extract fluid items from the AE2 network
                    ItemStack fluidIconStack = new ItemStack(ModItems.fluid_icon, 64, fluidTypeToExport.getID());
                    IAEItemStack itemStackToExtract = AEItemStack.create(fluidIconStack);

                    // Simulate extraction
                    IAEItemStack extractedStack = itemInventory.extractItems(itemStackToExtract, Actionable.SIMULATE, mySrc);

                    if (extractedStack != null && extractedStack.getStackSize() > 0) {
                        // Determine how many items we can actually extract
                        int itemsToExtract = (int) extractedStack.getStackSize();
                        int maxFillAmount = tank.getMaxFill() - tank.getFill();
                        int maxItemsBasedOnTank = maxFillAmount / 1000; // Assuming 1000 mB per item
                        int finalItemsToExtract = Math.min(itemsToExtract, maxItemsBasedOnTank);

                        if (finalItemsToExtract > 0) {
                            // Actually extract the items
                            itemStackToExtract.setStackSize(finalItemsToExtract);
                            @SuppressWarnings("unused")
                            IAEItemStack actualExtracted = itemInventory.extractItems(itemStackToExtract, Actionable.MODULATE, mySrc);

                            // Convert items to fluid and fill the tank
                            int fluidAmount = finalItemsToExtract * 1000; // Convert to mB
                            tank.setTankType(fluidTypeToExport);
                            tank.setFill(tank.getFill() + fluidAmount);

                            this.didSomething = true;

                            // Now attempt to push the fluid into the HBM network
                            pushFluidToNetwork(fluidTypeToExport);

                        } else {
                            // Tank is full, can't extract more items
                            return TickRateModulation.SLOWER;
                        }
                    } else {
                        // No items to extract
                        return TickRateModulation.SLOWER;
                    }
                } else {
                    // HBM network does not need the fluid; do not extract items
                    return TickRateModulation.SLOWER;
                }
            } else {
                // No fluid specified
                return TickRateModulation.SLOWER;
            }

        } catch (GridAccessException e) {
            e.printStackTrace();
        }

        return this.didSomething ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
    }

    private void pushFluidToNetwork(FluidType fluidType) {
        World world = this.getHost().getTile().getWorldObj();
        DirPos[] positions = getAdjacentPositions();

        // Subscribe to the fluid network
        for (DirPos pos : positions) {
            this.trySubscribe(fluidType, world, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
        }

        // Attempt to push fluid to connected receivers
        int previousFill = tank.getFill();
        int newFill = transmitFluid();

        int sentAmount = previousFill - newFill;
        if (sentAmount > 0) {
            tank.setFill(newFill);
            if (tank.getFill() <= 0) {
                tank.setTankType(Fluids.NONE);
            }
        }

        // Unsubscribe after transfer
        for (DirPos pos : positions) {
            this.tryUnsubscribe(fluidType, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    private int transmitFluid() {
        // Prepare parameters
        World world = this.getHost().getTile().getWorldObj();
        DirPos[] positions = getAdjacentPositions();

        // Transfer fluids
        int fill = tank.getFill();
        fill = transmitFluidFairly(
            world, tank, this, fill, false, true, positions
        );

        return fill;
    }


    private static int transmitFluidFairly(World world, FluidTank tank, IFluidConnector that, int fill, boolean connect, boolean send, DirPos[] connections) {

        Set<IPipeNet> nets = new HashSet<>();
        Set<IFluidConnector> consumers = new HashSet<>();
        FluidType type = tank.getTankType();
        int pressure = tank.getPressure();

        for (DirPos pos : connections) {

            TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

            if (te instanceof IFluidConductor) {
                IFluidConductor con = (IFluidConductor) te;
                if (con.getPipeNet(type) != null) {
                    nets.add(con.getPipeNet(type));
                    con.getPipeNet(type).unsubscribe(that);
                    consumers.addAll(con.getPipeNet(type).getSubscribers());
                }

            } else if (te instanceof IFluidConnector) {
                consumers.add((IFluidConnector) te);
            }
        }

        consumers.remove(that);

        if (fill > 0 && send) {
            // Send fluids to consumers
            for (IFluidConnector consumer : consumers) {
                if (consumer != null && consumer != that) {
                    long demand = consumer.getDemand(type, pressure);
                    if (demand > 0) {
                        long toSend = Math.min(fill, demand);
                        long remaining = consumer.transferFluid(type, pressure, toSend);
                        long sent = toSend - remaining;
                        fill -= sent;
                        if (fill <= 0) {
                            break;
                        }
                    }
                }
            }
        }

        // Resubscribe to buffered nets, if necessary
        if (connect) {
            for (IPipeNet net : nets) {
                net.subscribe(that);
            }
        }

        return fill;
    }

    private boolean hasDemandForFluid(FluidType fluidType) {
        Set<IFluidConnector> consumers = getConnectedConsumers(fluidType);

        for (IFluidConnector consumer : consumers) {
            if (consumer != null && consumer != this) {
                long demand = consumer.getDemand(fluidType, tank.getPressure());
                if (demand > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<IFluidConnector> getConnectedConsumers(FluidType fluidType) {
        World world = this.getHost().getTile().getWorldObj();
        DirPos[] positions = getAdjacentPositions();
        Set<IFluidConnector> consumers = new HashSet<>();

        for (DirPos pos : positions) {
            TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

            if (te instanceof IFluidConductor) {
                IFluidConductor con = (IFluidConductor) te;
                IPipeNet net = con.getPipeNet(fluidType);
                if (net != null) {
                    consumers.addAll(net.getSubscribers());
                }
            } else if (te instanceof IFluidConnector) {
                consumers.add((IFluidConnector) te);
            }
        }

        consumers.remove(this);
        return consumers;
    }

    private FluidType getFluidTypeFromFilterSlot() {
        ItemStack filterStack = this.getInventoryByName("config").getStackInSlot(FLUID_FILTER_SLOT);

        if (filterStack != null && filterStack.getItem() instanceof ItemFluidIdentifier) {
            ItemFluidIdentifier id = (ItemFluidIdentifier) filterStack.getItem();
            return id.getType(null, 0, 0, 0, filterStack);
        }
        return Fluids.NONE;
    }

    private DirPos[] getAdjacentPositions() {
        // Get the position in front of the export bus
        int x = this.getHost().getTile().xCoord + this.getSide().offsetX;
        int y = this.getHost().getTile().yCoord + this.getSide().offsetY;
        int z = this.getHost().getTile().zCoord + this.getSide().offsetZ;

        // Create a DirPos array with the adjacent position
        return new DirPos[] { new DirPos(x, y, z, this.getSide()) };
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
    protected boolean isSleeping() {
        return false;
    }

    // Implementing IFluidStandardTransceiver methods

    @Override
    public long transferFluid(FluidType type, int pressure, long amount) {
        // We don't receive fluids in this export bus
        return amount; // Can't accept any fluid
    }

    @Override
    public long getDemand(FluidType type, int pressure) {
        // We report no demand because we don't receive fluids
        return 0;
    }

    @Override
    public FluidTank[] getAllTanks() {
        return new FluidTank[] { tank };
    }

    @Override
    public FluidTank[] getSendingTanks() {
        return new FluidTank[] { tank };
    }

    @Override
    public FluidTank[] getReceivingTanks() {
        return new FluidTank[0]; // We don't receive fluids
    }
}
