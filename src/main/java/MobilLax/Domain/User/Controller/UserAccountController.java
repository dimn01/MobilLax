/*
 * UserAccountController.java
 *
 * ✅ 파일 목적:
 * - 사용자 로그인 및 회원가입 HTML 화면 렌더링
 * - JSON 기반 회원가입 API 처리
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-22
 */

package MobilLax.Domain.User.Controller;

import MobilLax.Domain.User.Service.UserAccountService;
import MobilLax.Global.Response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "👤 사용자 인증 API", description = "회원가입 및 로그인 관련 API를 제공합니다.")
class UserAccountApiController {

    private final UserAccountService userAccountService;

    /**
     * ✅ JSON 기반 회원가입 API
     */
    @Operation(summary = "회원가입", description = "사용자 이름, 이메일, 비밀번호를 받아 회원가입을 처리합니다.")
    @PostMapping("/register")
    public ApiResponse<String> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password
    ) {
        try {
            userAccountService.registerUser(name, email, password);
            return ApiResponse.ok("\u2705 회원가입에 성공했습니다.");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("\u274c 회원가입 실패: " + e.getMessage());
        }
    }
}

@Controller
@RequiredArgsConstructor
class UserAccountViewController {

    private final UserAccountService userAccountService;

    /**
     * ✅ 로그인 페이지 렌더링
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "이메일 또는 비밀번호가 잘못되었습니다.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "로그아웃 되었습니다.");
        }
        return "member/login";
    }

    /**
     * ✅ 회원가입 페이지 렌더링
     */
    @GetMapping("/register")
    public String showSignupForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "member/register";
    }

    /**
     * ✅ HTML 기반 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            userAccountService.registerUser(name, email, password);
            model.addAttribute("successMessage", "회원가입에 성공했습니다.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "member/register";
        }
    }
}
