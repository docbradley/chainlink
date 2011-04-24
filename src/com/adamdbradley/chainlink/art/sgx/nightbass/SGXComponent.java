package com.adamdbradley.chainlink.art.sgx.nightbass;

import java.util.Collection;
import java.util.Map;

import com.adamdbradley.chainlink.Component;
import com.adamdbradley.chainlink.Connector;
import com.adamdbradley.chainlink.InputConnector;
import com.adamdbradley.chainlink.OutputConnector;

public class SGXComponent implements com.adamdbradley.chainlink.Component {

    private final String name;
    private SGXOutputConnector inputConnectorFrom;
    private final SGXInputConnector inputConnector;
    private final SGXOutputConnector outputConnector;
    private SGXInputConnector outputConnectorTo;

    public SGXComponent(String name, 
            String inputConnectorName,
            SGXOutputConnector inputConnectFrom,
            String outputConnectorName) {
        this.name = name;
        if (inputConnectorName != null) {
            this.inputConnector = new SGXInputConnector(inputConnectorName, true);
            this.inputConnectorFrom = inputConnectFrom;
            if (inputConnectorFrom != null) {
                ((SGXComponent) inputConnectorFrom.getComponent()).outputConnectorTo = this.inputConnector;
            }
        } else {
            this.inputConnector = null;
        }
        if (outputConnectorName != null) {
            this.outputConnector = new SGXOutputConnector(outputConnectorName, true);
        } else {
            this.outputConnector = null;
        }
    }

    public SGXComponent getThis() {
        return this;
    }

    abstract class SGXConnector implements Connector {
        private final String name;
        private final boolean stereo;

        public SGXConnector(String name, boolean stereo) {
            this.name = name;
            this.stereo = stereo;
        }

        @Override
        public Component getComponent() { return getThis(); }

        @Override
        public String getName() { return name; }

        @Override
        public boolean isStereo() { return stereo; }
    }

    class SGXInputConnector extends SGXConnector implements InputConnector {
        public SGXInputConnector(String name, boolean stereo) {
            super (name, stereo);
        }
    }

    class SGXOutputConnector extends SGXConnector implements OutputConnector {
        public SGXOutputConnector(String name, boolean stereo) {
            super (name, stereo);
        }
    }

    @Override
    public Map<InputConnector, OutputConnector> getInputRouting() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<InputConnector> getInputs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Connector, Connector> getOutputRouting() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<OutputConnector> getOutputs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        return false;
    }


}
