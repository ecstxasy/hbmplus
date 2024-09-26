package com.ecstasy.hbmplus.AE2.HBMFluidInterface;

import java.util.HashSet;
import java.util.Set;

import com.ecstasy.hbmplus.Shared.ModLogger;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.ModItems;
import com.hbm.util.fauxpointtwelve.DirPos;

import api.hbm.fluid.IFluidConductor;
import api.hbm.fluid.IFluidConnector;
import api.hbm.fluid.IFluidStandardTransceiver;
import api.hbm.fluid.IPipeNet;
import appeng.api.config.Actionable;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.tile.misc.TileInterface;
import appeng.util.item.AEItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityHBMFluidInterface extends TileInterface implements IFluidStandardTransceiver {

    private int maxFluid = 24000;
    private FluidTank tank;
    private MachineSource src;

    public TileEntityHBMFluidInterface() {
        this.tank = new FluidTank(Fluids.NONE, maxFluid);
        this.src = new MachineSource(this);
    }
    

    public DirPos[] getConPos() {
        return new DirPos[] {
            new DirPos(xCoord + 1, yCoord, zCoord, com.hbm.lib.Library.POS_X),
            new DirPos(xCoord - 1, yCoord, zCoord, com.hbm.lib.Library.NEG_X),
            new DirPos(xCoord, yCoord + 1, zCoord, com.hbm.lib.Library.POS_Y),
            new DirPos(xCoord, yCoord - 1, zCoord, com.hbm.lib.Library.NEG_Y),
            new DirPos(xCoord, yCoord, zCoord + 1, com.hbm.lib.Library.POS_Z),
            new DirPos(xCoord, yCoord, zCoord - 1, com.hbm.lib.Library.NEG_Z)
        };
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

    @Override public void updateEntity() {

        try {

            final IMEMonitor<IAEItemStack> itemInventory = this.getProxy().getStorage().getItemInventory();

            if (tank.getTankType() == Fluids.NONE) {
                for (DirPos pos: getConPos()) {
                    TileEntity te = worldObj.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    
                    if (te instanceof IFluidConductor) {
                        IFluidConductor con = (IFluidConductor) te;
    
                        for (FluidType type : Fluids.getAll()) {
                            if (con.getPipeNet(type) != null) {
                                tank.setTankType(type);
                                break;
                            }
                        }
    
                    }
                }
            }

            if (tank.getTankType() != Fluids.NONE) {
                this.subscribeToAllAround(tank.getTankType(), this);

                ItemStack fluidItemStack = new ItemStack(ModItems.fluid_icon, tank.getFill(), tank.getTankType().getID());
                IAEItemStack itemStackToInsert = AEItemStack.create(fluidItemStack);
                IAEItemStack notInserted = itemInventory.injectItems(itemStackToInsert, Actionable.SIMULATE, src);

                int remainingFluid = transmitFluidFairly(this.getTile().getWorldObj(), tank, this, tank.getFill(), true, false, getConPos());

                if (notInserted == null || notInserted.getStackSize() == 0) {
                    itemInventory.injectItems(itemStackToInsert, Actionable.MODULATE, src);
                    ModLogger.logger.info("All items inserted successfully");
                    tank.setFill(remainingFluid);
                }
        
                if (tank.getFill() <= 0) {
                    tank.setTankType(Fluids.NONE);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        
        
    }

    @Override public FluidTank[] getSendingTanks() {
        return new FluidTank[0];
    }

    @Override public FluidTank[] getReceivingTanks() {
        return new FluidTank[] {tank};
    }

    @Override public FluidTank[] getAllTanks() {
        return new FluidTank[] {tank};
    }

    @Override public boolean isLoaded() {
        return true;
    }
}
