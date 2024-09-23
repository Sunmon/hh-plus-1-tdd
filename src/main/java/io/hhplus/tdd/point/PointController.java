package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    @Autowired
    public PointController(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }


    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return userPointTable.selectById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        // NOTE - 원본을 불변으로 넘겨줄 필요가 있는지?
        return pointHistoryTable.selectAllByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        // TODO System.currentMillis는 굳이 인자로 안 넣어주고 생성할때 자동으로 생성할 수 있지 않나?
        UserPoint userPoint = userPointTable.selectById(id);
        // FIXME 이 지점에 히스토리를 넣는게 맞나? 리턴하기 전... 유저포인트 업데이트 하기 전에?
        pointHistoryTable.insert(userPoint.id(), amount, TransactionType.CHARGE, 0);
        return userPointTable.insertOrUpdate(id, userPoint.point() + amount);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        UserPoint userPoint = userPointTable.selectById(id);
        // FIXME 이 지점에 히스토리를 넣는게 맞나? 리턴하기 전... 유저포인트 업데이트 하기 전에?
        pointHistoryTable.insert(userPoint.id(), amount, TransactionType.USE, 0);
        return userPointTable.insertOrUpdate(id, userPoint.point() - amount);
    }
}
