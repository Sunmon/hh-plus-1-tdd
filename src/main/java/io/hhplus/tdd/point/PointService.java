package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;

public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    @Autowired
    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    /**
     * 포인트를 충전하고, 충전된 후 잔액을 담은 UserPoint를 리턴한다
     *
     * @param userId
     * @param amount
     * @return
     */
    public UserPoint chargeUserPoints(long userId, long amount) {
        if (amount < 0) throw new PointException(ErrorCode.INVALID_POINT_AMOUNT);
        // TODO try-catch
        UserPoint userPoint = getUserPoint(userId);
        // FIXME 이 지점에 히스토리를 넣는게 맞나? 리턴하기 전... 유저포인트 업데이트 하기 전에?
        return userPointTable.insertOrUpdate(userId, userPoint.point() + amount);
//        pointHistoryTable
//        return getUserPoint(userId);
    }

    /**
     * 포인트를 사용하고, 사용후 남은 잔액을 담은 UserPoint를 리턴한다
     *
     * @param userId
     * @param amount
     * @return
     */
    public UserPoint useUserPoints(long userId, long amount) throws PointException {
        if (amount < 0) throw new PointException(ErrorCode.INVALID_POINT_AMOUNT);
        // TODO try-catch
        UserPoint userPoint = getUserPoint(userId);
        if (userPoint.point() < amount) throw new PointException(ErrorCode.INSUFFICIENT_POINTS);


        return userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
//        pointHistoryTable
//        return getUserPoint(userId);
    }
}
