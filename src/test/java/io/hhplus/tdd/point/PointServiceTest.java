package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 자동으로 Mock객체 초기화, 정리
public class PointServiceTest {

    @Mock
    UserPointTable userPointTable;

    @Mock
    PointHistoryService pointHistoryService;


    @InjectMocks
    PointService pointService;


    @BeforeEach
    void beforeEach() {
    }

    @DisplayName("특정 유저의 포인트를 조회한다.")
    @Test
    void testGetUserPoint() {
        //given
        long id = 999;
        long amount = 1000;
        long currentTime = Clock.systemDefaultZone().millis();
        UserPoint mockPoint = new UserPoint(id, amount, currentTime);
        when(userPointTable.selectById(id)).thenReturn(mockPoint);

        //when
        UserPoint point = pointService.getUserPoint(id);

        //then
        assertThat(point).isEqualTo(mockPoint);
    }

    @DisplayName("특정 유저의 포인트를 충전하고, 내역을 기록한다.")
    @Test
    void testChargeUserPoint() {
        //given
        long id = 999;
        long initialAmount = 1000;
        long addAmount = 5000;
        long currentTime = Clock.systemDefaultZone().millis();

        UserPoint initialPoint = new UserPoint(id, initialAmount, currentTime);
        UserPoint updatedPoint = new UserPoint(id, initialAmount + addAmount, currentTime);

        when(userPointTable.selectById(id)).thenReturn(initialPoint);
        when(userPointTable.insertOrUpdate(id, initialAmount + addAmount)).thenReturn(updatedPoint);

        //when
        UserPoint point = pointService.chargeUserPoints(id, addAmount);

        //then
        assertThat(point).isEqualTo(updatedPoint);
        verify(pointHistoryService).saveChargeHistory(id, addAmount, currentTime);
    }

    @DisplayName("특정 유저의 포인트를 사용하고, 내역을 기록한다.")
    @Test
    void testUseUserPoint() {
        //given
        long id = 999;
        long initialAmount = 1000;
        long currentTime = Clock.systemDefaultZone().millis();
        long useAmount = 500;

        UserPoint mockPoint = new UserPoint(id, initialAmount, currentTime);
        UserPoint updatedMockPoint = new UserPoint(id, initialAmount - useAmount, currentTime);

        when(userPointTable.selectById(id)).thenReturn(mockPoint);
        when(userPointTable.insertOrUpdate(id, initialAmount - useAmount)).thenReturn(updatedMockPoint);

        //when
        UserPoint point = pointService.useUserPoints(id, useAmount);

        //then
        assertThat(point).isEqualTo(updatedMockPoint);
        verify(pointHistoryService).saveUseHistory(id, useAmount, currentTime); // mock 객체만 검증 가능.
    }

    @DisplayName("잔액이 부족할 경우 돈을 사용할 수 없다.")
    @Test
    void testUseUserPointInsufficientBalance() {
        //given
        long id = 999;
        long initialAmount = 1000;
        long currentTime = Clock.systemDefaultZone().millis();
        long useAmount = 5000;

        UserPoint mockPoint = new UserPoint(id, initialAmount, currentTime);
        when(userPointTable.selectById(id)).thenReturn(mockPoint);

        //when, then
        assertThatThrownBy(() -> pointService.useUserPoints(id, useAmount))
                .isInstanceOf(PointException.class)
                .extracting(err -> ((PointException) err).getErrorCode())
                .isEqualTo(ErrorCode.INSUFFICIENT_POINTS);

        verify(pointHistoryService, never()).saveUseHistory(id, useAmount, currentTime);

    }

    @DisplayName("충전/사용금액을 음수로 설정할 수 없다.")
    @Test
    void testNegativeAmountPoint() {
        //given
        long id = 999;
        long negativeAmount = -5000;
        long negativeAmountLessThanBalance = -500;

        //when, then
        // 음수 포인트 사용 테스트
        assertThatThrownBy(() -> pointService.useUserPoints(id, negativeAmount))
                .isInstanceOf(PointException.class)
                .extracting(err -> ((PointException) err).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_POINT_AMOUNT);

        assertThatThrownBy(() -> pointService.useUserPoints(id, negativeAmountLessThanBalance))
                .isInstanceOf(PointException.class)
                .extracting(err -> ((PointException) err).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_POINT_AMOUNT);

        // 음수 포인트 충전 테스트
        assertThatThrownBy(() -> pointService.chargeUserPoints(id, negativeAmount))
                .isInstanceOf(PointException.class)
                .extracting(err -> ((PointException) err).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_POINT_AMOUNT);

        assertThatThrownBy(() -> pointService.chargeUserPoints(id, negativeAmountLessThanBalance))
                .isInstanceOf(PointException.class)
                .extracting(err -> ((PointException) err).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_POINT_AMOUNT);

        verify(pointHistoryService, never()).saveUseHistory(anyLong(), anyLong(), anyLong());
        verify(pointHistoryService, never()).saveChargeHistory(anyLong(), anyLong(), anyLong());
    }

}
