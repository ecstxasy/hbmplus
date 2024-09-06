package com.ecstasy.hbmplus.Fluids;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.util.EnumSymbol;

public class CustomFluids extends Fluids {
    public static FluidType SALTWATER;
    public static FluidType SODIUM_HYDROXIDE; // added this forgetting that an electroylsis fluid recipe can only output 2 fluids

    private static void add(FluidType fluid) {
        Fluids.metaOrder.add(fluid);
    }

    public static void init() {
        SALTWATER = new FluidType("SALTWATER", 0x7D7DFF, 0, 0, 0, EnumSymbol.NONE).addTraits(Fluids.LIQUID);
        SODIUM_HYDROXIDE = new FluidType("SODIUM_HYDROXIDE", 0x0000FA, 3, 0, 1, EnumSymbol.NONE).addTraits(Fluids.LIQUID);

        add(SALTWATER);
        add(SODIUM_HYDROXIDE);

    }
}