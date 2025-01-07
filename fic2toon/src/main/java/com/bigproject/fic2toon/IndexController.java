package com.bigproject.fic2toon;

import com.bigproject.fic2toon.user.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping
    public String mainPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login/login";
        }

        model.addAttribute("userType", user.getIdType());
        return "board/home";
    }
}