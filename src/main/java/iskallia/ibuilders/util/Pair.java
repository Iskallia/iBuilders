package iskallia.ibuilders.util;

import java.util.Objects;

public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }

    public V getValue() { return value; }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() * 31 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)return true;

        if(o instanceof Pair) {
            Pair pair = (Pair)o;
            if(!Objects.equals(key, pair.key))return false;
            if(!Objects.equals(value, pair.value))return false;
            return true;
        }

        return false;
    }

}
