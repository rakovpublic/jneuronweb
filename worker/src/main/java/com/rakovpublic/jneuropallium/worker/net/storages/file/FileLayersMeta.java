package com.rakovpublic.jneuropallium.worker.net.storages.file;

import com.rakovpublic.jneuropallium.worker.exceptions.LayersFolderIsEmptyOrNotExistsException;
import com.rakovpublic.jneuropallium.worker.net.storages.ILayerMeta;
import com.rakovpublic.jneuropallium.worker.net.storages.ILayersMeta;
import com.rakovpublic.jneuropallium.worker.net.storages.IResultLayerMeta;
import com.rakovpublic.jneuropallium.worker.net.storages.filesystem.IFileSystem;
import com.rakovpublic.jneuropallium.worker.net.storages.filesystem.IFileSystemItem;

import java.util.*;
import java.util.stream.Collectors;


public class FileLayersMeta<S extends IFileSystemItem> implements ILayersMeta {
    private S file;
    private IFileSystem fileSystem;
    private HashMap<Integer,ILayerMeta> layers;


    public FileLayersMeta(S file, IFileSystem<S> fs) {
        this.fileSystem = fs;
        this.file = file;
        layers = new HashMap<>();
    }

    @Override
    public List<ILayerMeta> getLayers() {
        // S layersDir = fileSystem.getItem(file+ fileSystem.getFolderSeparator()+"layers");
        //new File(file.getAbsolutePath() + File.pathSeparator + "layers");
        if(layers.isEmpty()){
        List<ILayerMeta> res = new ArrayList<>();
        List<S> temp = new ArrayList<>();
        if (!file.exists() || !file.isDirectory()) {
            throw new LayersFolderIsEmptyOrNotExistsException();
        }

        temp = fileSystem.listFiles(file);
        Collections.sort(temp, new Comparator<IFileSystemItem>() {
            @Override
            public int compare(IFileSystemItem o1, IFileSystemItem o2) {
                return Integer.compare(Integer.parseInt(o1.getName()), Integer.parseInt(o2.getName()));
            }
        });
        int i = 0;
        for (IFileSystemItem f : temp) {
            ILayerMeta layerMeta = new FileLayerMeta(f, fileSystem);
            layers.put(layerMeta.getID(),layerMeta);
            res.add(layerMeta);
            i++;
            if (i == temp.size() - 1) {
                break;
            }
        }
            return res;
        }else {
            return layers.values().stream().collect(Collectors.toList());
        }

    }

    @Override
    public IResultLayerMeta getResultLayer() {
        // S layersDir = fileSystem.getItem(file+ fileSystem.getFolderSeparator()+"layers");
        //new File(file.getAbsolutePath() + File.pathSeparator + "layers");
        List<ILayerMeta> res = new ArrayList<>();
        List<S> temp = new ArrayList<>();
        if (!file.exists() || !file.isDirectory()) {
            throw new LayersFolderIsEmptyOrNotExistsException();
        }

        temp = fileSystem.listFiles(file);
        temp.removeIf(iFileSystemItem -> {
            if (iFileSystemItem.isDirectory()) {
                return true;
            }
            return false;
        });
        Collections.sort(temp, new Comparator<IFileSystemItem>() {
            @Override
            public int compare(IFileSystemItem o1, IFileSystemItem o2) {
                return Integer.compare(Integer.parseInt(o1.getName()), Integer.parseInt(o2.getName()));
            }
        });
        return new FileResultLayerMeta(temp.get(temp.size() - 1), fileSystem);
    }

    @Override
    public ILayerMeta getLayerByID(int id) {

        return layers.get(id);
    }

    @Override
    public void addLayerMeta(ILayerMeta layerMeta) {
        layers.put(layerMeta.getID(),layerMeta);

    }
}
