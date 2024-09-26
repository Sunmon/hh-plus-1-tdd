package io.hhplus.tdd;

//import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.hhplus.tdd.point.PointException;
import io.hhplus.tdd.point.PointHistoryException;
import io.hhplus.tdd.point.PointHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// 전역 예외 처리기
@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }

    @ExceptionHandler(value = PointException.class)
    public ResponseEntity<ErrorResponse> handlePointException(PointException e) {
        return ResponseEntity.status(500).body(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }


    @ExceptionHandler(value = PointHistoryException.class)
    public ResponseEntity<ErrorResponse> handlePointHistoryException(PointHistoryException e) {
        return ResponseEntity.status(500).body(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

//    @ExceptionHandler(value = MismatchedInputException.class)
//    public ResponseEntity<ErrorResponse> handleMismatchedException(MismatchedInputException e) {
//        return ResponseEntity.status(500).body(new ErrorResponse(e.toString(), e.getMessage()));
//    }

}
