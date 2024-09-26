package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 자동으로 Mock객체 초기화, 정리
public class PointHistoryServiceTest {

    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    PointHistoryService pointHistoryService;


    @DisplayName("특정 유저의 포인트 내역을 조회한다.")
    @Test
    void testGetPointHistories() {
        //given
        long id = 1;
        long userId = 999;
        long currentTime = Clock.systemDefaultZone().millis();

        List<PointHistory> mockHistories = List.of(
                new PointHistory(id++, userId, 1000, TransactionType.CHARGE, currentTime),
                new PointHistory(id++, userId, 500, TransactionType.USE, currentTime),
                new PointHistory(id++, userId, 100, TransactionType.USE, currentTime),
                new PointHistory(id++, userId, 2000, TransactionType.CHARGE, currentTime)
        );

        //when
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(mockHistories);
        List<PointHistory> histories = pointHistoryService.getPointHistories(userId);

        //then
        assertThat(histories).isEqualTo(mockHistories);
        verify(pointHistoryTable, times(1)).selectAllByUserId(userId);
    }


    @DisplayName("특정 유저의 단일 포인트 내역을 저장한다.")
    @Test
    void testGetSavedPointHistory() {
        //given
        long id = 1;
        long userId = 999;
        long amount = 1000;
        long currentTime = Clock.systemDefaultZone().millis();

        PointHistory mockCharge = new PointHistory(id++, userId, amount, TransactionType.CHARGE, currentTime);
        PointHistory mockUse = new PointHistory(id++, userId, amount, TransactionType.USE, currentTime);

        when(pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, currentTime)).thenReturn(mockCharge);
        when(pointHistoryTable.insert(userId, amount, TransactionType.USE, currentTime)).thenReturn(mockUse);

        // when
        PointHistory chargeHistory = pointHistoryService.saveChargeHistory(userId, amount, currentTime);
        PointHistory useHistory = pointHistoryService.saveUseHistory(userId, amount, currentTime);

        //then
        assertThat(chargeHistory).isEqualTo(mockCharge);
        assertThat(useHistory).isEqualTo(mockUse);
    }


}
