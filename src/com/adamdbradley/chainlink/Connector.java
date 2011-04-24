package com.adamdbradley.chainlink;

/**
 * Represents an input or output to a {@link Component}.
 */
public interface Connector {

    public String getName();
    public boolean isStereo();
    public Component getComponent();

}
