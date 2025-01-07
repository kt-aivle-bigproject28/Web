package com.bigproject.fic2toon.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
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
    public String processAgree(@RequestParam(name = "agreement", required = true) boolean agreed) {
        if (agreed) {
            return "redirect:/login/signup";
        } else {
            return "redirect:/login/agree?error=true";
        }
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute UserDto userDto,
                                BindingResult bindingResult,
                                Model model) {
        // 유효성 검사: 휴대폰 번호가 11자리 숫자인지 확인
        if (!userDto.getPhone().matches("\\d{11}")) {
            model.addAttribute("error", "휴대폰 번호는 11자리 숫자만 입력 가능합니다.");
            return "login/signup";
        }

        if (bindingResult.hasErrors()) {
            return "login/signup";
        }

        try {
            userService.create(userDto);
            return "redirect:/login/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login/signup";
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
        // 유효성 검사: 휴대폰 번호가 11자리 숫자인지 확인
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
