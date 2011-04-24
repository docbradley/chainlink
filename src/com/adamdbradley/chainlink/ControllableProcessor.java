package com.adamdbradley.chainlink;

public interface ControllableProcessor {

    public String getDescription();

    /**
     * 
     * TODO: symbolic bank representation
     * @param bank optional bank identifier.
     * @param patchNumber patch identifier.
     * @return
     */
    public Patch getPatch(Short bank, short patchNumber);
    public Patch[] getAllPatches();

}
