package spartaclub.cafeorderservice.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// 전화번호 변경 요청 DTO
public record UpdatePhoneRequest(

        // 새로운 전화번호 (010-xxxx-xxxx 형식 강제)
        @NotBlank(message = "새 전화번호를 입력해주세요")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다."
        )
        String newPhone,

        // 본인 확인을 위한 현재 PIN
        @NotBlank(message = "현재 PIN을 입력해주세요")
        String currentPin
) {}
