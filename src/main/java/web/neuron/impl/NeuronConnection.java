package web.neuron.impl;

import web.neuron.INConnection;
import web.neuron.IWeight;

public class NeuronConnection implements INConnection {
    @Override
    public int getTargetLayerId() {
        return 0;
    }

    @Override
    public int getSourceLayerId() {
        return 0;
    }

    @Override
    public Long getTargetNeuronId() {
        return null;
    }

    @Override
    public Long getSourceNeuronId() {
        return null;
    }

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public IWeight getWeight() {
        return null;
    }
}