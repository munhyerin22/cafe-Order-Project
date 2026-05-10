package spartaclub.cafeorderservice.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorResponse fail) {

    // 성공(데이터 있음)
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 성공 (데이터 없음)
    public static ApiResponse<Void> of() {
        return new ApiResponse<>(true, null, null);
    }

    // 실패
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message));
    }

    // 에러 상세 정보
    public record ErrorResponse(String code, String message) {}
}
