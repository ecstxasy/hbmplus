package com.ecstasy.hbmplus.Items;

import com.hbm.hazard.HazardRegistry;
import com.hbm.items.ModItems;

public class ItemsMod {
    public static ItemCustomBreedingRod BreedingPu240;
    public static ItemCustomBreedingRod BreedingPu241;
    public static ItemCustomBreedingRod BreedingAmMix;

    public static void Init() {
        BreedingPu240 = new ItemCustomBreedingRod("pu240", ModItems.billet_pu240, HazardRegistry.pu240, 1);
        BreedingPu241 = new ItemCustomBreedingRod("pu241", ModItems.billet_pu241, HazardRegistry.pu241, 2);
        BreedingAmMix = new ItemCustomBreedingRod("am_mix", ModItems.billet_am_mix, HazardRegistry.amrg, 3);
    }
}