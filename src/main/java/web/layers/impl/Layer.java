package web.layers.impl;

import web.layers.ILayer;
import web.neuron.IAxon;
import web.neuron.INConnection;
import web.neuron.INeuron;
import web.neuron.impl.NeuronRunnerService;
import web.signals.ISignal;
import web.storages.IInputMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rakovskyi Dmytro on 08.06.2018.
 */
public class Layer implements ILayer {
    private HashMap<Long, INeuron> map;
    private HashMap<Long, List<ISignal>> input;
    private Boolean isProcessed;
    private List<INeuron> notProcessed;

    public Layer(int layerId) {
        isProcessed = false;
        notProcessed = new ArrayList<INeuron>();
        this.layerId = layerId;
        map = new HashMap<Long, INeuron>();
        input = new HashMap<Long, List<ISignal>>();
    }

    private int layerId;

    @Override
    public void register(INeuron neuron) {
        map.put(neuron.getId(), neuron);

    }


    @Override
    public void addInput(ISignal signal, Long neuronId) {
        if (input.containsKey(neuronId)) {
            input.get(neuronId).add(signal);
        } else {
            List<ISignal> list = new ArrayList<>();
            list.add(signal);
            input.put(neuronId, list);
        }
    }

    @Override
    public void process() {
        INeuron neur;
        NeuronRunnerService ns = NeuronRunnerService.getService();
        for (Long neuronId : map.keySet()) {
            if (input.containsKey(neuronId)) {
                neur = map.get(neuronId);
                neur.addSignals(input.get(neuronId));
                ns.addNeuron(neur);
            }
        }
        ns.process();

    }

    @Override
    public int getId() {
        return layerId;
    }

    @Override
    public Boolean isProcessed() {

        if (!isProcessed && notProcessed.size() == 0) {
            isProcessed = true;
            for (INeuron ner : map.values()) {
                if (!ner.hasResult()) {
                    notProcessed.add(ner);
                    isProcessed = false;
                }
            }
        } else {

            for (INeuron ner : notProcessed) {
                if (ner.hasResult()) {
                    notProcessed.remove(ner);
                }
            }
            if (notProcessed.size() == 0) {
                isProcessed = true;
            }
        }
        return isProcessed;
    }

    @Override
    public void dumpResult(IInputMeta meta) {
        HashMap<Integer, HashMap<Long, List<ISignal>>> result = new HashMap<>();
        for (Long neurId : map.keySet()) {
            INeuron neur = map.get(neurId);
            IAxon axon = neur.getAxon();
            HashMap<ISignal, List<INConnection>> tMap = axon.processSignal(neur.getResult());
            for (ISignal signal : tMap.keySet()) {
                for (INConnection connection : tMap.get(signal)) {
                    int layerId = connection.getTargetLayerId();
                    Long targetNeurId = connection.getTargetNeuronId();
                    if (result.containsKey(layerId)) {
                        if (result.get(layerId).containsKey(targetNeurId)) {
                            result.get(layerId).get(targetNeurId).add(signal);
                        } else {
                            List<ISignal> signals = new ArrayList<>();
                            signals.add(signal);
                            result.get(layerId).put(targetNeurId, signals);
                        }
                    } else {
                        List<ISignal> signals = new ArrayList<>();
                        signals.add(signal);
                        HashMap<Long, List<ISignal>> ttMap = new HashMap<>();
                        ttMap.put(targetNeurId, signals);
                        result.put(layerId, ttMap);


                    }
                }
            }
        }
        for (int layerId : result.keySet()) {
            meta.mergeResults(result.get(layerId), layerId);
        }

    }


    @Override
    public String toJSON() {
        return null;
    }
}
