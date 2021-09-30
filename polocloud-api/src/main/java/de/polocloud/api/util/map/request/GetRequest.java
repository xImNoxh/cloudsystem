package de.polocloud.api.util.map.request;

import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.map.MapEntry;
import de.polocloud.api.util.map.UniqueMap;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetRequest<K, V> {

    private final UniqueMap<K, V> parent;

    public GetRequest(UniqueMap<K, V> parent) {
        this.parent = parent;
    }

    public MapEntry<K, V> atPosition(int position) {
        return this.parent.getValues().get(position);
    }

    public boolean containsKey(K key) {
        return parent.iterable().stream().anyMatch(kvMapEntry -> kvMapEntry.getValue().equals(key));
    }

    public boolean containsValue(V value) {
        return parent.iterable().stream().anyMatch(kvMapEntry -> kvMapEntry.getValue().equals(value));
    }

    public V atKey(K key) {
        MapEntry<K, V> pair = this.parent.iterable().stream().filter(kvPair -> kvPair.getKey().equals(key)).findFirst().orElse(null);
        if (pair == null) {
            return null;
        }
        return pair.getValue();
    }

    public int position(K key) {
        for (Integer integer : this.parent.getValues().keySet()) {
            MapEntry<K, V> kvPair = this.parent.getValues().get(integer);
            if (kvPair.getKey().equals(key)) {
                return integer;
            }
        }
        PoloHelper.sneakyThrows(() -> {
            throw new NotFoundException("No index for key " + key + " found! It is not contained by this map!");
        });
        return -1;
    }

    public List<K> allKey(V value) {
        List<K> list = new ArrayList<>();
        List<MapEntry<K, V>> collect = this.parent.iterable().stream().filter(kvPair -> kvPair.getValue().equals(value)).collect(Collectors.toList());

        for (MapEntry<K, V> kvPair : collect) {
            list.add(kvPair.getKey());
        }
        return list;
    }

    public List<K> allKeys() {
        List<K> list = new ArrayList<>();
        List<MapEntry<K, V>> collect = this.parent.iterable().stream().collect(Collectors.toList());

        for (MapEntry<K, V> kvPair : collect) {
            list.add(kvPair.getKey());
        }
        return list;
    }

    public List<V> allValue(K key) {
        List<V> list = new ArrayList<>();
        List<MapEntry<K, V>> collect = this.parent.iterable().stream().filter(kvPair -> kvPair.getKey().equals(key)).collect(Collectors.toList());

        for (MapEntry<K, V> kvPair : collect) {
            list.add(kvPair.getValue());
        }
        return list;
    }

    public List<V> allValues() {
        List<V> list = new ArrayList<>();
        List<MapEntry<K, V>> collect = this.parent.iterable().stream().collect(Collectors.toList());

        for (MapEntry<K, V> kvPair : collect) {
            list.add(kvPair.getValue());
        }
        return list;
    }

    public K atValue(V value) {
        MapEntry<K, V> pair = this.parent.iterable().stream().filter(kvPair -> kvPair.getValue().equals(value)).findFirst().orElse(null);
        if (pair == null) {
            return null;
        }
        return pair.getKey();
    }

}
