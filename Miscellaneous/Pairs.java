package Miscellaneous;

import java.util.ArrayList;

/**
 * utility class to store key-value pairs.
 */
@SuppressWarnings("unused")
public class Pairs<K extends Comparable<K>, V extends Comparable<V>> {
    private ArrayList<K> keys;
    private ArrayList<V> values;//key/value stores

    /**
     * default constructor
     */
    public Pairs() {
        keys = new ArrayList<>(100);
        values = new ArrayList<>(100);
    }

    /**
     * parametrized constructor
     */
    public Pairs(int cap) {
        keys = new ArrayList<>(cap);
        values = new ArrayList<>(cap);
    }

    /**
     * add a key-value pair to the stores
     */
    public void add(K key, V value) {
        keys.add(key);
        values.add(value);
    }

    /**
     * retrieve a key's index in the store
     */
    private int getKeyIndex(K key) {
        int i = 0;
        for (; i < keys.size(); i++) {
            if (keys.get(i).equals(key))
                return i;
        }
        return -1;
    }

    /**
     * retrieve a value's index in the store
     */
    private int getValueIndex(V value) {
        int i = 0;
        for (; i < values.size(); i++) {
            if (values.get(i).equals(value))
                return i;
        }
        return -1;
    }

    /**
     * remove a key-value pair from the store
     */
    public void remove(K key) {
        int keyIndex = getKeyIndex(key);
        if (keyIndex > -1) {//key exists
            keys.remove(keyIndex);
            values.remove(keyIndex);
        } else
            System.out.println(key + " to remove does not exist");
    }

    public int size() {
        return keys.size();
    }

    /**
     * retrieve a key from the store
     */
    public K getKey(V value) {
        if (getValueIndex(value) == -1) {
            System.out.println(value + " does not exist");
            return null;
        } else
            return keys.get(getValueIndex(value));
    }

    /**
     * retrieve a value from the store
     */
    public V getValue(K key) {
        if (getKeyIndex(key) == -1) {
            System.out.println(key + " does not exist");
            return null;
        } else
            return values.get(getKeyIndex(key));
    }
}