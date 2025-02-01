package uk.co.cloudmatica;

public interface ForgettingMap<K, V> {

    void add(K key, V value);
    V find(K key);
    int size();
}
