package uk.co.cloudmatica.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import uk.co.cloudmatica.ForgettingMap;
import uk.co.cloudmatica.ThreadSafeForgetMap;

import java.util.concurrent.ExecutorService;

import static java.lang.String.valueOf;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static uk.co.cloudmatica.ThreadSafeForgetMapTest.FIVE;
import static uk.co.cloudmatica.ThreadSafeForgetMapTest.doFindInMapOnThisKeyThisManyTimes;

@SpringBootTest
@TestConfiguration
public class ThreadSafeForgetMapTests {

    @Test
    public void test_map_under_multi_threaded_env() {

            ForgettingMap<String, Integer> forgetMap;
            forgetMap = new ThreadSafeForgetMap<>(FIVE);

            System.out.println("Running many adds with executor ");
            long timeTaken = timeElapseForGetPut(forgetMap);

            System.out.println("Time taken = " + timeTaken);

            assertThat(forgetMap.find("-1")).isNotNull();
            assertThat(forgetMap.find("-100")).isNotNull();
            assertThat(forgetMap.size()).isEqualTo(5);
        }

        /*
         * Run 2,000,000 through map, check first two with most still there.
         */
        private long timeElapseForGetPut(ForgettingMap<String, Integer> map) {

            ExecutorService executorService = newFixedThreadPool(5);
            long startTime = System.nanoTime();

            doFindInMapOnThisKeyThisManyTimes(map, "-1", 1, 3);
            doFindInMapOnThisKeyThisManyTimes(map, "-100", 100, 3);

            for (int i = 0; i < 4; i++) {

                executorService.execute(() -> {
                    for (int j = 0; j < 500_000; j++) {

                        int value = current().nextInt(10000);
                        String key = valueOf(value);
                        map.add(key, value);
                        map.find(key);
                    }
                });
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("Error: " + e.getLocalizedMessage());
            }
            executorService.execute(() -> {
                for (int j = 0; j < 500_000; j++) {

                    Object result = map.find("-1");

                    assertThat(result).isEqualTo(1);
                }
            });


            executorService.shutdown();
            try {
                executorService.awaitTermination(15, SECONDS);
            } catch (Exception e) {
                fail(e.getMessage());
            }
            return (System.nanoTime() - startTime) / 500_000;
        }
}
