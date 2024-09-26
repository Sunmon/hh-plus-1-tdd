package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class PointService {
    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointTable userPointTable;

    private final PointHistoryService pointHistoryService;

    // 사용자별 락 객체를 관리하기 위한 맵
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    @Autowired
    public PointService(UserPointTable userPointTable, PointHistoryService pointHistoryService) {
        this.userPointTable = userPointTable;
        this.pointHistoryService = pointHistoryService;
    }

    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    /**
     * 포인트를 충전하고, 충전된 후 잔액을 담은 UserPoint를 리턴한다
     *
     * @param userId 포인트를 충전하려는 유저 id
     * @param amount 포인트를 충전하려는 양
     * @return 포인트 충전 이후 업데이트된 포인트 상태
     */
    public UserPoint chargeUserPoints(long userId, long amount) throws PointException {
        log.info("[포인트 서비스] 충전 id: {}, 금액: {}", userId, amount);

        if (amount < 0) throw new PointException(ErrorCode.INVALID_POINT_AMOUNT);
        ReentrantLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock()); // 유저별 락
        lock.lock();
        try {
            // 트랜젝션 롤백 처리 안 했음...
            if (amount > UserPoint.CHARGE_LIMIT) throw new PointException(ErrorCode.POINT_CHARGE_LIMIT_EXCEEDED);

            UserPoint userPoint = getUserPoint(userId);
            UserPoint updated = userPointTable.insertOrUpdate(userId, userPoint.point() + amount);
            pointHistoryService.saveChargeHistory(updated.id(), amount, updated.updateMillis());
            return updated;
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                userLocks.remove(userId, lock);
            }
        }
    }

    /**
     * 포인트를 사용하고, 사용후 남은 잔액을 담은 UserPoint를 리턴한다
     *
     * @param userId 포인트를 충전하려는 유저 id
     * @param amount 포인트를 충전하려는 양
     * @return 포인트 사용 이후 업데이트된 포인트 상태
     */
    public UserPoint useUserPoints(long userId, long amount) throws PointException {
        log.info("[포인트 서비스] 사용 id: {}, 금액: {}", userId, amount);
        if (amount < 0) throw new PointException(ErrorCode.INVALID_POINT_AMOUNT);

        ReentrantLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock()); // 유저별 락
        lock.lock();

        try {
            // 트랜젝션 롤백 처리 안 했음...
            UserPoint userPoint = getUserPoint(userId);
            if (userPoint.point() < amount) throw new PointException(ErrorCode.INSUFFICIENT_POINTS);
            UserPoint updated = userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
            pointHistoryService.saveUseHistory(updated.id(), amount, updated.updateMillis());
            return updated;
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                userLocks.remove(userId, lock);
            }
        }
    }

}
