package com.bank.account.controller;

import com.bank.account.dto.SetPassword;
import com.bank.account.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        return "login";
    }

    @GetMapping("/set-password")
    public String setPasswordForm(HttpSession session,
                                  Model model) {
        model.addAttribute("request", new SetPassword());
        return "password";
    }

    @PostMapping("/set-password")
    public String setPassword(@Valid @ModelAttribute("request") SetPassword request,
                              HttpSession session,
                              Model model) {
        if (session.getAttribute("email") == null) {
            return "redirect:/product/debit";
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("confirmError", "Ваши пароли не совпадают");
            return "password";
        }
        if (request.getPassword().length() < 8) {
            model.addAttribute("error", "Пароль должен быть не менее 8 символов");
            return "password";
        }

        userService.setPassword((String) session.getAttribute("email"), request.getPassword());



        return "redirect:/account";
    }
}
