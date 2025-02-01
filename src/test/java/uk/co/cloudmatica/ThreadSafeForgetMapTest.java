package uk.co.cloudmatica;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note:    The reason for not declaring a ForgettingMap<K, V> as a member is only so it can be used with
 * different generic types, in the test cases. Hope this isn't overkill!
 */
public class ThreadSafeForgetMapTest {

    public static final int TWO = 2;
    public static final int FIVE = 5;

    @Test
    public void when_do_not_want_low_hanging_fruit_spoiling_coverage() {
        Application.main(new String[] {});
    }

    @Test
    public void when_over_capacity_map_does_not_exceed_capacity() {

        ForgettingMap<Integer, Object> forgetMap;

        forgetMap = new ThreadSafeForgetMap<>(TWO);

        forgetMap.add(1, new Object());
        forgetMap.add(2, new Object());
        forgetMap.add(3, new Object());

        assertThat(forgetMap.size()).isEqualTo(2);
    }

    @Test
    public void when_over_capacity_and_favoured_elements_least_favoured_replaced() {

        ForgettingMap<String, Object> forgetMap;

        forgetMap = new ThreadSafeForgetMap<>(FIVE);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k1", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k2", new Object(), 2);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k3", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k4", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k5", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k6", new Object(), 1);

        assertThat(forgetMap.find("k2")).isNull();
    }

    @Test
    public void when_all_values_are_of_same_interest_first_on_is_first_off() {

        ForgettingMap<String, Object> forgetMap;
        forgetMap = new ThreadSafeForgetMap<>(TWO);

        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k1", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k2", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k3", new Object(), 3);

        assertThat(forgetMap.find("k1")).isNull();
        assertThat(forgetMap.find("k2")).isNotNull();
        assertThat(forgetMap.find("k3")).isNotNull();
    }

    @Test
    public void when_more_than_one_least_used_the_first_on_of_them_is_removed() {

        ForgettingMap<String, Object> forgetMap;
        forgetMap = new ThreadSafeForgetMap<>(FIVE);

        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k1", new Object(), 2);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k2", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k3", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k4", new Object(), 2);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k5", new Object(), 3);
        doFindInMapOnThisKeyThisManyTimes(forgetMap, "k6", new Object(), 3);

        // Oldest of two least used, this (k1) should be removed not the newest.
        assertThat(forgetMap.find("k1")).isNull();
        assertThat(forgetMap.find("k2")).isNotNull();
        assertThat(forgetMap.find("k3")).isNotNull();
        assertThat(forgetMap.find("k4")).isNotNull();
        assertThat(forgetMap.find("k5")).isNotNull();
        assertThat(forgetMap.find("k6")).isNotNull();
    }

    public static <K, V> void doFindInMapOnThisKeyThisManyTimes(ForgettingMap<K, V> forgettingMap, K key,
                                                          V value, Integer times) {

        forgettingMap.add(key, value);

        for (int i = 0; i < times; i++) {
            forgettingMap.find(key);
        }
    }
}
