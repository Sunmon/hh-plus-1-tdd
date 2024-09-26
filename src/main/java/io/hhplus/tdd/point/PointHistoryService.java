package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointHistoryService {

    private final PointHistoryTable pointHistoryTable;

    @Autowired
    public PointHistoryService(PointHistoryTable pointHistoryTable) {
        this.pointHistoryTable = pointHistoryTable;
    }

    public List<PointHistory> getPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    protected PointHistory saveChargeHistory(long userId, long amount, long updateMillis) {
        return savePointHistory(userId, amount, TransactionType.CHARGE, updateMillis);
    }

    protected PointHistory saveUseHistory(long userId, long amount, long updateMillis) {
        return savePointHistory(userId, amount, TransactionType.USE, updateMillis);
    }

    private PointHistory savePointHistory(long userId, long amount, TransactionType type, long updateMillis) {
        return pointHistoryTable.insert(userId, amount, type, updateMillis);
    }
}
