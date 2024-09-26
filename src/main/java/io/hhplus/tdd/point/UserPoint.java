package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    final static long CHARGE_LIMIT = 10000;

    public UserPoint {
        if (point < 0) throw new PointException(ErrorCode.INVALID_POINT_AMOUNT);
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
