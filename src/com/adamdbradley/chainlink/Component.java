package com.adamdbradley.chainlink;

import java.util.Collection;
import java.util.Map;

/**
 * A component represents an atomic element of the processor's audio pipeline, 
 * e.g. the input jack, a combiner/splitter/router, a flanger, a distortion pedal, 
 * a tuner, the output jack, etc.  For static processors like the ART SGX or most 
 * PODs, these represent a series of configurable elements in a static order.  
 * For assignable pipeline processors like the M13, these represent "stomp box 
 * positions" which can be assigned to any processor type, interconnected by
 * 3-by-3 switchable collector-distributors.  In a system with dynamic routing, 
 * these are abstract elements of computational/processing power which can be 
 * wired together arbitrarily.
 */
public interface Component {

    public String getName();

    /**
     * 
     * @return false if the component is in "bypass" mode.
     */
    public boolean isActive();

    /**
     * 
     * @return <code>null</code> if this {@link Component} has no logical inputs (e.g., it is an input jack itself, or a tone generator)
     */
    public Collection<InputConnector> getInputs();

    /**
     * 
     * @return <code>null</code> ifthis {@link Component} has no logical outputs (e.g., it is an output jack itself).
     */
    public Collection<OutputConnector> getOutputs();

    /**
     * Map from each of this {@link Component}'s {@link InputConnector}s 
     * to another {@link Component}'s {@link OutputConnector}s.
     * @return
     */
    public Map<InputConnector,OutputConnector> getInputRouting();
    public Map<Connector,Connector> getOutputRouting();

}
