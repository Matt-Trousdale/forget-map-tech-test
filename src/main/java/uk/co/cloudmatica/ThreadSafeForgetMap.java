package uk.co.cloudmatica;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.Comparator.comparingInt;

/**
 *
 * @param <K> Key
 * @param <V> Value
 *
 *           This class implements a Facade pattern
 *           @see https://en.wikipedia.org/wiki/Facade_pattern#:~:text=The%20facade%20pattern%20(also%20spelled,complex%20underlying%20or%20structural%20code.
 *
 *           Essenssially just exposing the required fuctionality. This allows wrapping of existing JDK and delegating
 *           the work and decorating for the extra requirement/s. I though of implementing Map<K.., V..> but it
 *           would have meant adding many more methods that were not required for this test.
 *
 *           The ThreadSafeForgetMap impl keeps values upto capacity. This shoould use no more memory than nessesary.
 *           When capacity is reached. The ThreadSafeForgetMap should query it's own contents, finding the least used
 *           value. If there is no candidate it removes the oldest e.g. first. ConcurrentSkipListMap is ordered.
 */

public final class ThreadSafeForgetMap<K, V> implements ForgettingMap<K, V> {

    private final int capacity;
    private final ConcurrentSkipListMap<K, Pair<V>> contents;

    public ThreadSafeForgetMap(final int capacity) {

        ConcurrentHashMap<K, Pair<V>> map = new ConcurrentHashMap<>(capacity);
        contents = new ConcurrentSkipListMap<>(map);

        this.capacity = capacity;
    }

    /**
     *
     * @param key map key
     * @param value map value
     *
     *              Synchronized purely to stop map growing past capacity. If removed because maps size is not
     *              a worry then {@link #replaceLeastUsed(Object, Object)} must be synchronized.
     *
     */
    @Override
    public synchronized void add(final K key, final V value) {

        if (size() < capacity) {
            contents.put(key, PairFactory.newPair(value));
        } else {
            replaceLeastUsed(key, value);
        }
    }

    /**
     *
     * @param key map key
     * @param value map value
     *
     *              Would be ok to remove synchronized block here. As at present it is only called from
     *              the {@link #add(Object, Object)} method in this class, which is synchronized.
     *              It has been left in as has no detrimental effects on performance, but if a new method is
     *              created and calls this without synchronized. This would create a data race.
     *              Essentially just future proofing, could be removed.
     */
    private synchronized void replaceLeastUsed(final K key, final V value) {

            K keyToRemove = contents
                    .entrySet()
                    .stream()
                    .min(comparingInt(entry -> entry.getValue().accessCount))
                    .map(Entry::getKey)
                    .orElse(contents.firstKey());

            contents.remove(keyToRemove);
            add(key, value);
    }

    @Override
    public V find(final K key) {

        Pair<V> valueAndCount = contents.get(key);

        if (valueAndCount != null) {
            valueAndCount.accessCount = valueAndCount.accessCount + 1;
            return valueAndCount.value;
        } else {
            return null;
        }
    }

    @Override
    public final int size() {

        /**
         * Left commented out for ease of telling if map exceeds capacity.
         */
//        if (contents.size() > 5) {
//            System.out.println("Problem size should never be more than - " + capacity +
//                   " actual - " +contents.size());
//        }
        return contents.size();
    }

    /**
     * Effective Java: "Prefer builders for Dependency Injection."
     */
    private static class PairFactory {

        private static <T> Pair<T> newPair(final T value) {
            return new Pair<T>(value);
        }
    }

    /**
     *
     * @param <V> value
     *           Effective Java: "Prefer static member classes over non static".
     *           Nested class: as for this exercise {@link #Pair(V value)} is of no use outside this class.
     *           It's really an implementation detail that should be hidden as it contains find count etc.
     *           Also the reason no getters, just accessing internally.
     */
    private static class Pair<V> {

        private final V value;
        private int accessCount = 0;

        Pair(final V value) {

            this.value = value;
        }
    }
}
