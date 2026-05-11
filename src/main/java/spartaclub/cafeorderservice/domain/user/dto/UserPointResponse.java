package spartaclub.cafeorderservice.domain.user.dto;

import spartaclub.cafeorderservice.domain.user.entity.User;

// 사용자 정보 조회 응답 DTO
public record UserPointResponse(
        Long userId,      // 사용자 DB ID
        String phone,     // 전화번호
        Long point        // 현재 보유 포인트
) {
    // User 엔티티 → DTO 변환 (정적 팩토리 메서드)
    public static UserPointResponse from(User user) {
        return new UserPointResponse(user.getId(), user.getPhone(), user.getPoint());
    }
}
