package spartaclub.cafeorderservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // -- 인증 -----------------------------------------------------------------
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않는 토큰입니다."),

    // -- 사용자 ---------------------------------------------------------------
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 사용자 입니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "DUPLICATE_PHONE", "이미 등록된 전화번호 입니다."),
    INVALID_PIN(HttpStatus.UNAUTHORIZED, "INVALID_PIN", "PIN이 일치하지 않습니다."),

    // -- 메뉴 -----------------------------------------------------------------
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_NOT_FOUND", "존재하지 않는 메뉴입니다."),

    // -- 포인트 ---------------------------------------------------------------
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "INSUFFICIENT_POINT", "포인트가 부족합니다."),
    //INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_CHARGE_AMOUNT", "허용되지 않는 충전 단위 입니다."),
    POINT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "POINT_LIMIT_EXCEEDED", "최대 보유 가능 포인트는 200,000P 입니다."),
    INVALID_REFUND_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_REFFUND_AMOUNT", "환불은 10,000P단위로 가능합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
