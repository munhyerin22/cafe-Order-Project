package spartaclub.cafeorderservice.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spartaclub.cafeorderservice.auth.dto.LoginRequest;
import spartaclub.cafeorderservice.auth.dto.LoginResponse;
import spartaclub.cafeorderservice.auth.service.AuthService;
import spartaclub.cafeorderservice.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthController {

    private final AuthService authService;

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login (
            @RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
