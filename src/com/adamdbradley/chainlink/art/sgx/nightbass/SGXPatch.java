package com.adamdbradley.chainlink.art.sgx.nightbass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.adamdbradley.chainlink.Component;
import com.adamdbradley.chainlink.Connector;
import com.adamdbradley.chainlink.InputConnector;
import com.adamdbradley.chainlink.OutputConnector;
import com.adamdbradley.chainlink.art.sgx.nightbass.SGXComponent.SGXOutputConnector;

public class SGXPatch implements com.adamdbradley.chainlink.UpdatablePatch<Processor> {

    Short bank;
    Short patchNumber;
    String name;
    Processor processor;
    List<Component> components;

    public SGXPatch() {
        components = new ArrayList<Component>(16);
        SGXComponent preamp = new SGXComponent("Preamp", null, null, "signal");
        SGXComponent compressor = new SGXComponent("Compressor", "input", (SGXOutputConnector) preamp.getOutputs().iterator().next(), "signal");
        SGXComponent expander = new SGXComponent("Expander", "input", (SGXOutputConnector) compressor.getOutputs().iterator().next(), "signal");
        // etc
        SGXComponent output = new SGXComponent("Digital Out", "input", (SGXOutputConnector) expander.getOutputs().iterator().next(), null);
    }

    @Override
    public Short getBank() {
        return bank;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Short getPatchNumber() {
        return patchNumber;
    }

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public Class<Processor> getProcessorType() {
        return Processor.class;
    }

}
