package spartaclub.cafeorderservice.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spartaclub.cafeorderservice.auth.security.CustomUserDetails;
import spartaclub.cafeorderservice.common.response.ApiResponse;
import spartaclub.cafeorderservice.domain.user.dto.*;
import spartaclub.cafeorderservice.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestBody @Valid SignupRequest request) {

        SignupResponse response = userService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(response));
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserPointResponse>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserPointResponse response = userService.getMyInfo(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    // 전화번호 변경
    @PutMapping("/me/phone")
    public ResponseEntity<ApiResponse<Void>> updatePhone(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdatePhoneRequest request) {

        userService.updatePhone(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.of());
    }

    // Pin 변경
    @PutMapping("/me/pin")
    public ResponseEntity<ApiResponse<Void>> updatePin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdatePinRequest request) {

        userService.updatePin(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.of());
    }

}
