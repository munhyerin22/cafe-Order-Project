package spartaclub.cafeorderservice.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spartaclub.cafeorderservice.common.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 직접 던질 비즈니스 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("[BusinessException] code={}, message={}", errorCode.getCode(), e.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), e.getMessage()));
    }

    // @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {

        FieldError fieldError = e.getBindingResult().getFieldErrors().getFirst();
        String message = fieldError.getDefaultMessage();
        log.error("[ValidationException] field={}, message={}", fieldError.getField(), message);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("VALIDATION_ERROR", message));
    }

    // 예상치 못한 서버 에러 처리(최후의 보루)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[UnhandledException]", e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.fail("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}
