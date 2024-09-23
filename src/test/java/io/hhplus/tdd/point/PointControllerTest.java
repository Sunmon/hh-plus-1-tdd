package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointControllerTest {

    UserPointTable userPointTable;
    PointHistoryTable pointHistoryTable;

    //    PointController pointController;
    @BeforeEach
    void beforeEach() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
    }


    @DisplayName("특정 유저의 포인트를 조회한다")
    @Test
    void point() {
        // given
        long id = 123L;
        //when
        PointController pointController = new PointController(userPointTable, pointHistoryTable);
        UserPoint userPoint = pointController.point(id);
        //then
        assertThat(userPoint.point()).isEqualTo(0);
    }

    @DisplayName("특정 유저의 포인트를 충전한다")
    @Test
    void charge() {
        // given
        long id1 = 123L;
        long id2 = 111L;
        PointController pointController = new PointController(userPointTable, pointHistoryTable);

        //when
        pointController.charge(id1, 1000);
        pointController.charge(id2, 1000);
        pointController.charge(id2, 1000);
        // then
        assertThat(userPointTable.selectById(id1).point()).isEqualTo(1000);
        assertThat(userPointTable.selectById(id2).point()).isEqualTo(2000);
    }


    @DisplayName("특정 유저의 포인트를 사용한다")
    @Test
    void use() {
        // given
        long id = 123L;
        UserPointTable userPointTable = new UserPointTable();
        PointController pointController = new PointController(userPointTable, pointHistoryTable);
        //when
        pointController.charge(id, 1000);
        pointController.use(id, 600);
        // then
        assertThat(userPointTable.selectById(id).point()).isEqualTo(400);
    }

    @DisplayName("특정 유저의 포인트 사용내역을 조회한다")
    @Test
    void histories() {
        // given
        long id = 123L;
        PointController pointController = new PointController(userPointTable, pointHistoryTable);
        //when
        pointController.charge(id, 100);
        pointController.charge(id, 1000);
        pointController.use(id, 300);
        pointController.charge(id, 900);

        // then
        List<PointHistory> history = pointController.history(id);
        assertThat(pointController.point(id).point()).isEqualTo(1700);
        assertThat(history).containsExactly(
                new PointHistory(1, id, 100, TransactionType.CHARGE, 0),
                new PointHistory(2, id, 1000, TransactionType.CHARGE, 0),
                new PointHistory(3, id, 300, TransactionType.USE, 0),
                new PointHistory(4, id, 900, TransactionType.CHARGE, 0)
        );
    }
}