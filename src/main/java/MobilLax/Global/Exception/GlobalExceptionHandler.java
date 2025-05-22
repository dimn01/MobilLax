/*
 * GlobalExceptionHandler.java
 * ✅ 목적: 전역 예외를 공통 형식(ApiResponse)으로 처리하기 위한 핸들러
 */

package MobilLax.Global.Exception;

import MobilLax.Global.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 일반적인 예외 처리 (RuntimeException 등)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 오류: " + ex.getMessage()));
    }

    // ✅ 존재하지 않는 리소스 접근 등
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("요청한 리소스를 찾을 수 없습니다: " + ex.getMessage()));
    }

    // ✅ 유효성 검사 실패 (Validation 오류)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("요청 유효성 오류: " + errorMessage));
    }

    // ✅ 기타 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("알 수 없는 오류가 발생했습니다: " + ex.getMessage()));
    }
}