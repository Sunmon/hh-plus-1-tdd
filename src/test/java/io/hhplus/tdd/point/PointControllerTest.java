package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PointControllerTest {

    @DisplayName("특정 유저의 포인트를 조회한다")
    @Test
    void point() {
        // given
        long id = 123L;
        UserPointTable userPointTable = new UserPointTable();
        //when
        PointController pointController = new PointController(userPointTable);
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
        UserPointTable userPointTable = new UserPointTable();
        //when
        PointController pointController = new PointController(userPointTable);

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
        //when
        PointController pointController = new PointController(userPointTable);

        pointController.charge(id, 1000);
        pointController.use(id, 600);
        // then
        assertThat(userPointTable.selectById(id).point()).isEqualTo(400);
    }
}