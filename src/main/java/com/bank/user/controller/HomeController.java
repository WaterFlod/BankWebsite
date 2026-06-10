package com.bank.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/home")
    public String home(Authentication auth,
                       Model model) {
        if (auth != null) {
            model.addAttribute("username", auth.getName());
        }
        return "home";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}