package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest
public class PointConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointHistoryService pointHistoryService;

    @Test
    @DisplayName("동시에 각기 다른 n명의 유저가 충전을 시도할 떄, 데이터 일관성이 유지되어야 한다.")
    void testConcurrentChargePoint() throws ExecutionException, InterruptedException {
        // given
        long[] userIds = {1, 2, 3, 4, 5};
        long[] amounts = {100, 200, 300, 400, 500};
        int threadCount = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        //when
        List<Callable<UserPoint>> chargePoints = new ArrayList<>();
        for (int i = 0; i < userIds.length; i++) {
            long userId = userIds[i];
            long amount = amounts[i];
            chargePoints.add(() -> pointService.chargeUserPoints(userId, amount));
        }

        executor.invokeAll(chargePoints);
        executor.shutdown();

        // Then
        // 포인트 확인
        for (int i = 0; i < userIds.length; i++) {
            UserPoint updatedPoint = pointService.getUserPoint(userIds[i]);
            assertThat(updatedPoint.point()).isEqualTo(amounts[i]);
        }

        // 히스토리 확인
        for (int i = 0; i < userIds.length; i++) {
            List<PointHistory> histories = pointHistoryService.getPointHistories(userIds[i]);
            assertThat(histories).hasSize(1);
            assertThat(histories.get(0).amount()).isEqualTo(amounts[i]);
        }
    }

    @Test
    @DisplayName("동시에 같은 유저가 n개의 충전/사용/조회를 시도할 떄, 데이터 일관성이 유지되어야 한다.")
    void testConcurrentPointRequest() throws ExecutionException, InterruptedException {
        // given
        long userId = 1;
        long[] amounts = {200, 500, 300, 400, 500};
        int threadCount = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Callable<UserPoint>> requests = List.of(
                () -> pointService.chargeUserPoints(userId, amounts[0]),
                () -> pointService.chargeUserPoints(userId, amounts[1]),
                () -> pointService.chargeUserPoints(userId, amounts[2])
        );

        //when
        List<Future<UserPoint>> futures = executor.invokeAll(requests);
        executor.shutdown();
        boolean terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        if (!terminated) {
            fail("Executor 종료되지 않음.");
        }

        // Then
        // 포인트 확인
        List<UserPoint> results = new ArrayList<>();
        Map<UserPoint, Long> resultRequestMap = new HashMap<>();
        for (int i = 0; i < futures.size(); i++) {
            UserPoint result = futures.get(i).get();
            results.add(result);
            resultRequestMap.put(result, amounts[i]);
        }

        long prevPoint = 0;
        results.sort(Comparator.comparingLong(UserPoint::updateMillis));
        List<Long> resultAmounts = new ArrayList();

        for (UserPoint userPoint : results) {
            Long userPointAmount = resultRequestMap.get(userPoint);
            assertThat(userPoint.point()).isEqualTo(userPointAmount + prevPoint);
            prevPoint = userPoint.point();
            resultAmounts.add(userPointAmount);
        }


        // 히스토리 확인
        List<PointHistory> histories = pointHistoryService.getPointHistories(userId);
        assertThat(histories).hasSize(requests.size());
        for (int i = 0; i < requests.size(); i++) {
            assertThat(histories.get(i).amount()).as(i + "번째 히스토리 - " + histories.get(i)).isEqualTo(resultAmounts.get(i));
        }

        // 최종 금액 확인
        assertThat(pointService.getUserPoint(userId).point()).isEqualTo(1000);
    }
}
