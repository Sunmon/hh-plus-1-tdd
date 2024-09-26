package io.hhplus.tdd.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PointHistoryException extends RuntimeException {
    private final ErrorCode errorCode;
    private final PointHistory pointHistory;
}
