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
        // TODO try-catch
        // TODO System.currentMillis는 굳이 인자로 안 넣어주고 생성할때 자동으로 생성할 수 있지 않나?

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
    public UserPoint useUserPoints(long userId, long amount) {
        // TODO try-catch
        UserPoint userPoint = getUserPoint(userId);
        return userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
//        pointHistoryTable
//        return getUserPoint(userId);
    }
}
