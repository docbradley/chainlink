package com.adamdbradley.chainlink;

import com.adamdbradley.chainlink.exceptions.CommunicationFailureException;

public interface ControllableProcessor {

    public String getDescription();

    /**
     * 
     * TODO: symbolic bank representation
     * @param bank optional bank identifier.
     * @param patchNumber patch identifier.
     * @return
     */
    public Patch getPatch(Short bank, short patchNumber) throws CommunicationFailureException ;
    public Patch[] getAllPatches() throws CommunicationFailureException;

}
