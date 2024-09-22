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
        long id1 = 123L;
        long id2 = 111L;
//        Long id3 = -1L;
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(111L, 1000);
        //when
        PointController pointController = new PointController(userPointTable);
        UserPoint userPoint1 = pointController.point(id1);
        UserPoint userPoint2 = pointController.point(id2);
        //then
        assertThat(userPoint1.point()).isEqualTo(0);
        assertThat(userPoint2.point()).isEqualTo(1000);
    }

}