package com.ecstasy.hbmplus.Items;

import com.hbm.items.ModItems;

public class ItemsMod {
    public static ItemCustomBreedingRod BreedingPu240;
    public static ItemCustomBreedingRod BreedingPu241;
    public static ItemCustomBreedingRod BreedingAmMix;

    public static final HazardTypeBase RADIATION = new HazardTypeRadiation();

    public static final float billet = 0.5F;

	public static final float pu240 = 7.5F;
	public static final float pu241 = 25.0F;
    public static final float amrg = 9.0F;

    public static void Init() {
        BreedingPu240 = new ItemCustomBreedingRod("pu240", ModItems.billet_pu240, pu240, 1);
        BreedingPu241 = new ItemCustomBreedingRod("pu241", ModItems.billet_pu241, pu241, 2);
        BreedingAmMix = new ItemCustomBreedingRod("am_mix", ModItems.billet_am_mix, amrg, 3);
    }
}