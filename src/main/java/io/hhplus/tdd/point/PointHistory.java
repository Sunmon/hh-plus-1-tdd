package io.hhplus.tdd.point;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
    public PointHistory {
        if (!isValid(id, userId, amount)) {
            throw new PointHistoryException(ErrorCode.INVALID_POINT_HISTORY, new PointHistory(id, userId, amount, type, updateMillis));
        }
    }

    private boolean isValid(long id, long userId, long amount) {
        return id > 0 && userId > 0 && amount >= 0;
    }
}
