package io.hhplus.tdd.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INSUFFICIENT_POINTS("POINT_001", "포인트가 부족합니다."),
    INVALID_POINT_AMOUNT("POINT_002", "유효하지 않은 금액입니다."),
    INVALID_POINT_HISTORY("POINT_HISTORY_001", "유효하지 않은 내역입니다."),
    ;

    private final String code;
    private final String message;
}
