package com.adamdbradley.chainlink;

import java.util.Collection;

public interface Patch<T extends ControllableProcessor> {

    /**
     * The kind of processor against which this patch is defined.
     * Never <code>null</code>.
     * @return
     */
    public Class<T> getProcessorType();

    /**
     * The particular processor this patch is from.  This represents
     * a patch the was retrieved from or sent to an actual processor,
     * or can be <code>null</code> for a purely ephemeral patch.
     * @return
     */
    public T getProcessor();

    /**
     * The short, human-readable name of this patch. 
     * Subject to the restrictions of {@link #getProcessorType()}.
     * @return
     */
    public String getName();

    public Short getBank();

    public Short getPatchNumber();

}
