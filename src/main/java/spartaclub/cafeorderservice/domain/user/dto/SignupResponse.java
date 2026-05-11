package spartaclub.cafeorderservice.domain.user.dto;

import spartaclub.cafeorderservice.domain.user.entity.User;

// 회원가입 성공 시 클라이언트에게 돌려줄 응답 DTO
public record SignupResponse(
        Long userId,
        String phone
) {
    
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getPhone());
    }
}
