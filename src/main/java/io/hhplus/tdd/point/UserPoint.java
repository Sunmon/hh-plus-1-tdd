package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public UserPoint {
        if (point < 0) throw new PointException(ErrorCode.INSUFFICIENT_POINTS);
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
