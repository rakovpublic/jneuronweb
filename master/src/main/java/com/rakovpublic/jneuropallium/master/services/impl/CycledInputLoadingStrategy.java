package com.rakovpublic.jneuropallium.master.services.impl;

import com.rakovpublic.jneuropallium.master.services.IInputLoadingStrategy;
import com.rakovpublic.jneuropallium.master.services.ISignalsPersistStorage;
import com.rakovpublic.jneuropallium.worker.net.storages.IInitInput;
import com.rakovpublic.jneuropallium.worker.net.storages.ILayerMeta;
import com.rakovpublic.jneuropallium.worker.net.storages.ILayersMeta;
import com.rakovpublic.jneuropallium.worker.net.storages.InputInitStrategy;
import com.rakovpublic.jneuropallium.worker.net.storages.inmemory.InMemoryLayerMeta;
import com.rakovpublic.jneuropallium.worker.neuron.INeuron;
import com.rakovpublic.jneuropallium.worker.neuron.ISignalChain;
import com.rakovpublic.jneuropallium.worker.neuron.impl.cycleprocessing.CycleNeuron;
import com.rakovpublic.jneuropallium.worker.neuron.impl.cycleprocessing.CycleSignalsProcessingChanin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CycledInputLoadingStrategy implements IInputLoadingStrategy {
    private ILayersMeta layersMeta;
    private HashMap<IInitInput,InputInitStrategy> externalInputs;
    int counter;

    public CycledInputLoadingStrategy(ILayersMeta layersMeta, HashMap<IInitInput, InputInitStrategy> externalInputs, int defaultLoopsCount) {
        counter=0;
        this.layersMeta = layersMeta;
        this.externalInputs = externalInputs;
        init(defaultLoopsCount);
    }

    private void init(int defaultLoopsCount){
        ISignalChain signalChain= new CycleSignalsProcessingChanin();
        CycleNeuron cycleNeuron = new CycleNeuron(defaultLoopsCount,signalChain);
        List<INeuron> neurons = new LinkedList<>();
        neurons.add(cycleNeuron);
        ILayerMeta layerMeta= new InMemoryLayerMeta(Integer.MIN_VALUE,neurons);
        layersMeta.addLayerMeta(layerMeta);

    }

    @Override
    public Boolean populateInput(ISignalsPersistStorage signalsPersistStorage, HashMap<IInitInput, InputStatusMeta> inputStatuses) {
        signalsPersistStorage.cleanOutdatedSignals();
        if(counter>=((CycleNeuron)layersMeta.getLayerByID(Integer.MIN_VALUE).getNeuronByID(0l)).getLoopCount()){
            for(IInitInput iii:inputStatuses.keySet()){
                if(inputStatuses.get(iii).getCurrentRuns()>=inputStatuses.get(iii).getUpdateOnceInNRuns()){
                    signalsPersistStorage.putSignals(externalInputs.get(iii).getInputs(layersMeta,iii.readSignals()));
                    inputStatuses.get(iii).setCurrentRuns(0);
                }else {
                    inputStatuses.get(iii).setCurrentRuns(inputStatuses.get(iii).getCurrentRuns()+1);
                }
            }
            counter=0;
        }else {
            counter+=1;
        }
        return true;
    }
}
