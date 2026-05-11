package spartaclub.cafeorderservice.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// PIN 변경 요청 DTO
public record UpdatePinRequest(

        // 현재 PIN (본인 확인용)
        @NotBlank(message = "현재 PIN을 입력해주세요")
        @Size(min = 4, max = 4, message = "PIN은 4자리여야 합니다")
        String currentPin,

        // 변경할 새 PIN
        @NotBlank(message = "새 PIN을 입력해주세요")
        @Size(min = 4, max = 4, message = "PIN은 4자리여야 합니다")
        String newPin
) {}
