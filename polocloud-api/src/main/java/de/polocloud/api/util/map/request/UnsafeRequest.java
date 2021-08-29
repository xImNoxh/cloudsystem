package de.polocloud.api.util.map.request;

import com.google.gson.JsonObject;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.util.Acceptable;
import de.polocloud.api.util.map.MapEntry;
import de.polocloud.api.util.map.UniqueMap;

import java.util.HashMap;
import java.util.Map;

public class UnsafeRequest<K, V> {

    /**
     * The parent map of this request
     */
    private final UniqueMap<K, V> parent;

    /**
     * Constructs this request
     *
     * @param parent the parent
     */
    public UnsafeRequest(UniqueMap<K, V> parent) {
        this.parent = parent;
    }

    /**
     * Creates a standard {@link Map} with no doubled values
     * And no filter so every value will be accepted
     *
     * @return created map
     */
    public Map<K, V> toNotUniqueHashMap() {
        return toNotUniqueHashMap(null);
    }

    /**
     * Creates a standard {@link Map} where no doubeld values
     * are allowed and only the provided filter decides which values are
     * getting accepted to be put into the map
     *
     * @param filter the filter
     * @return created map
     */
    public Map<K, V> toNotUniqueHashMap(Acceptable<V> filter) {
        Map<K, V> map = new HashMap<>();
        this.parent.iterable().forEach((k, v) -> {
            if (filter == null || filter.isAccepted(v)) {
                map.put(k, v);
            }
        });
        return map;
    }

    /**
     * Transforms the parent {@link UniqueMap} into a {@link JsonObject}
     * And pays attention to doubled values
     * Json entries are sorted by their position and followed by the {@link MapEntry} as Sub- {@link JsonObject}
     *
     * @return json object
     */
    public JsonObject toJsonObject() {
        JsonData jsonData = new JsonData();

        for (MapEntry<K, V> entry : this.parent.iterable()) {
            jsonData.append(String.valueOf(entry.getPosition()), new JsonData(String.valueOf(entry.getKey()), entry.getValue()));
        }

        return jsonData.getBase().getAsJsonObject();
    }

}
