package com.bigproject.fic2toon.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login/login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute UserDto userDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "login/login";
        }

        try {
            User user = userService.login(userDto);
            session.setAttribute("user", user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login/login";
        }
    }

    @GetMapping("/agree")
    public String agree() {
        return "login/agree";
    }

    @PostMapping("/agree")
    public String processAgree(@RequestParam(name = "agreement", required = true) boolean agreed, Model model) {
        if (agreed) {
            return "redirect:/signup";
        } else {
            model.addAttribute("error", "약관에 동의해야 진행할 수 있습니다.");
            return "login/agree";
        }
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login/signup";
    }

    @PostMapping("/api/signup")
    @ResponseBody
    public ResponseEntity<?> processSignup(@Valid @RequestBody UserDto userDto,
                                           BindingResult bindingResult) {
        System.out.println("회원가입 요청: " + userDto);

        if (!userDto.getPhone().matches("\\d{11}")) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "전화번호는 11자리 숫자여야 합니다."));
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "입력값 검증 실패."));
        }

        try {
            userService.create(userDto);
            System.out.println("회원가입 성공: " + userDto.getId());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            System.err.println("회원가입 중 오류 발생: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "회원가입 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/findpw")
    public String findPassword() {
        return "login/findpw";
    }

    @PostMapping("/findpw")
    public String processFindPassword(@Valid @ModelAttribute UserDto userDto,
                                      BindingResult bindingResult,
                                      Model model) {
        if (!userDto.getPhone().matches("\\d{11}")) {
            model.addAttribute("error", "전화번호는 11자리 숫자만 입력 가능합니다.");
            return "login/findpw";
        }

        if (bindingResult.hasErrors()) {
            return "login/findpw";
        }

        try {
            String password = userService.findPassword(userDto);
            model.addAttribute("password", password);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "login/findpw";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login/login";
    }
}
