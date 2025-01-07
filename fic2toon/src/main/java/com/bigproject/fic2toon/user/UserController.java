package com.bigproject.fic2toon.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute UserDto userDto,
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
    public String agreeForm() {
        return "login/agree";
    }

    @PostMapping("/agree")
    public String agree(@RequestParam(name = "agreement", required = true) boolean agreed) {
        return agreed ? "redirect:/signup" : "redirect:/agree?error=true";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute UserDto userDto,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors() || !isValidPhoneNumber(userDto.getPhone())) {
            model.addAttribute("error", "입력 정보를 확인해주세요.");
            return "login/signup";
        }

        try {
            userService.create(userDto);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login/signup";
        }
    }

    @GetMapping("/findpw")
    public String findPasswordForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login/findpw";
    }

    @PostMapping("/findpw")
    public String findPassword(@Valid @ModelAttribute UserDto userDto,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors() || !isValidPhoneNumber(userDto.getPhone())) {
            model.addAttribute("error", "입력 정보를 확인해주세요.");
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
        return "redirect:/";
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{11}");
    }
}
