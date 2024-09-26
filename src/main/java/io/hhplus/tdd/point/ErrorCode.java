package io.hhplus.tdd.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INSUFFICIENT_POINTS("POINT_001", "포인트가 부족합니다."),
    INVALID_POINT_AMOUNT("POINT_002", "유효하지 않은 금액입니다."),
    POINT_CHARGE_LIMIT_EXCEEDED("POINT_003", "포인트 1회 최대 충전 금액을 초과했습니다"),
    // 포인트 내역 에러
    INVALID_POINT_HISTORY("POINT_HISTORY_001", "유효하지 않은 내역입니다."),
    // 기타 에러
    INVALID_REQUEST_PARAMETER("REQUEST_001", "잘못된 데이터 형식입니다."),
    ;

    private final String code;
    private final String message;
}
