package com.ecstasy.hbmplus.Util;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.type.HazardTypeBase;

public class CustomHazard {
    public static HazardData makeData() { return new HazardData(); }
	public static HazardData makeData(HazardTypeBase hazard) { return new HazardData().addEntry(hazard); }
	public static HazardData makeData(HazardTypeBase hazard, float level) { return new HazardData().addEntry(hazard, level); }
	public static HazardData makeData(HazardTypeBase hazard, float level, boolean override) { return new HazardData().addEntry(hazard, level, override); }
}
