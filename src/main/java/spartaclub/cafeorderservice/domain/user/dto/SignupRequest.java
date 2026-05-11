package spartaclub.cafeorderservice.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// record : 불변 데이터 클래스. getter, equals, hashCode 자동 생성
public record SignupRequest(

        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다."
        )
        String phone,

        @NotBlank(message = "PIN을 입력해주세요")
        @Size(min =4, max =4, message = "PIN은 4자리여야 합니다")
        String pin
) {}
