package de.polocloud.api.logger.def;

public class Pair<T, L> {

    private T t;
    private L l;

    public Pair(T t, L l) {
        this.t = t;
        this.l = l;
    }

    public T getKey() {
        return t;
    }

    public L getValue() {
        return l;
    }
}
