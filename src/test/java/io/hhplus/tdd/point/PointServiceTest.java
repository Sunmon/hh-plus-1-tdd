package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // 자동으로 Mock객체 초기화, 정리
public class PointServiceTest {

    @Mock
    UserPointTable userPointTable;
    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    PointService pointService;


    @BeforeEach
    void beforeEach() {
//        MockitoAnnotations.openMocks(this);
//        pointHistoryTable = new PointHistoryTable();
//        userPointTable = new UserPointTable();
//        pointService = new PointService(userPointTable, pointHistoryTable);


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

    @DisplayName("특정 유저의 포인트를 충전한다.")
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
    }

    @DisplayName("특정 유저의 포인트를 사용한다.")
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
    }
}
