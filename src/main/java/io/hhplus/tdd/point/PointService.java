package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PointService {
    private final UserPointTable userPointTable;

    private final PointHistoryService pointHistoryService;

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
    public UserPoint chargeUserPoints(long userId, long amount) {
        if (amount < 0) throw new PointException(ErrorCode.INVALID_POINT_AMOUNT);

        UserPoint userPoint = getUserPoint(userId);
        UserPoint updated = userPointTable.insertOrUpdate(userId, userPoint.point() + amount);
        pointHistoryService.saveChargeHistory(updated.id(), amount, updated.updateMillis());
        return updated;
    }

    /**
     * 포인트를 사용하고, 사용후 남은 잔액을 담은 UserPoint를 리턴한다
     *
     * @param userId 포인트를 충전하려는 유저 id
     * @param amount 포인트를 충전하려는 양
     * @return 포인트 사용 이후 업데이트된 포인트 상태
     */
    public UserPoint useUserPoints(long userId, long amount) throws PointException {
        if (amount < 0) throw new PointException(ErrorCode.INVALID_POINT_AMOUNT);

        UserPoint userPoint = getUserPoint(userId);
        if (userPoint.point() < amount) throw new PointException(ErrorCode.INSUFFICIENT_POINTS);

        UserPoint updated = userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
        pointHistoryService.saveUseHistory(updated.id(), amount, updated.updateMillis());
        return updated;
    }

}
