package com.bank.account.controller;

import com.bank.account.dto.RegistrationRequest;
import com.bank.user.model.User;
import com.bank.user.security.CustomUserDetailsService;
import com.bank.account.service.AccountService;
import com.bank.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final UserService userService;
    private final AccountService accountService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/checking")
    public String checkingAccount(Authentication auth, Model model) {
        if (auth != null) {
            model.addAttribute("username", auth.getName());
        }
        model.addAttribute("request", new RegistrationRequest());
        return "checking";
    }

    @PostMapping("/checking")
    public String createCheckingAccount(@Valid @ModelAttribute("request") RegistrationRequest request,
                                     HttpSession session, Authentication auth,
                                     Model model){

        String email;
        BigDecimal initialBalance;
        User user;

        if (auth == null) {
            boolean flag = false;

            if (userService.existsUserByEmail(request.getEmail())) {
                model.addAttribute("error", "Пользователь с таким email уже существует");
                flag = true;
            }
            if (userService.existsUserByPhoneNumber(request.getPhoneNumber())) {
                model.addAttribute("error", "Пользователь с таким номером телефона уже существует");
                flag = true;
            }
            if (request.getInitialDeposit().signum() == -1) {
                model.addAttribute("error", "Первоначальный депозит не может быть отрицательным");
                flag = true;
            }
            if (request.getPassword().length() < 8) {
                model.addAttribute("error", "Пароль должен быть не менее 8 символов");
                flag = true;
            }
            if (flag) {
                return "debit";
            }

            email = request.getEmail();
            user = userService.registerUser(request.getFirstName(), request.getLastName(),
                    request.getEmail(), request.getPhoneNumber(), request.getPassword());
        } else {
            email = auth.getName();
            user = userService.findUserByIdentifier(email);
        }

        initialBalance = request.getInitialDeposit();

        session.setAttribute("email", email);

        accountService.createCheckingAccount(user, initialBalance);

        if (auth == null) {
            SecurityContext context = setContext(user.getEmail());
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        }
        return "redirect:/account";
    }

    @GetMapping("/savings")
    public String savingsAccount(Authentication auth, Model model) {
        if (auth != null) {
            model.addAttribute("username", auth.getName());
        }
        model.addAttribute("request", new RegistrationRequest());
        return "savings";
    }

    @PostMapping("/savings")
    public String createSavingsAccount(@Valid @ModelAttribute("request") RegistrationRequest request,
                                        HttpSession session, Authentication auth,
                                        Model model){
        String email;
        BigDecimal initialBalance;
        User user;

        if (auth == null) {
            boolean flag = false;

            if (userService.existsUserByEmail(request.getEmail())) {
                model.addAttribute("error", "Пользователь с таким email уже существует");
                flag = true;
            }
            if (userService.existsUserByPhoneNumber(request.getPhoneNumber())) {
                model.addAttribute("error", "Пользователь с таким номером телефона уже существует");
                flag = true;
            }
            if (request.getInitialDeposit().signum() == -1) {
                model.addAttribute("error", "Первоначальный депозит не может быть отрицательным");
                flag = true;
            }
            if (request.getPassword().length() < 8) {
                model.addAttribute("error", "Пароль должен быть не менее 8 символов");
                flag = true;
            }
            if (flag) {
                return "savings";
            }

            email = request.getEmail();
            user = userService.registerUser(request.getFirstName(), request.getLastName(),
                    request.getEmail(), request.getPhoneNumber(), request.getPassword());
        } else {
            email = auth.getName();
            user = userService.findUserByIdentifier(email);
        }
        initialBalance = request.getInitialDeposit();

        session.setAttribute("email", email);

        accountService.createSavingsAccount(user, initialBalance);

        if (auth == null) {
            SecurityContext context = setContext(user.getEmail());
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        }

        return "redirect:/account";
    }

    @GetMapping("/credit")
    public String creditAccount(Authentication auth, Model model) {
        if (auth != null) {
            model.addAttribute("username", auth.getName());
        }
        model.addAttribute("request", new RegistrationRequest());
        return "credit";
    }

    @PostMapping("/credit")
    public String createCreditAccount(@Valid @ModelAttribute("request") RegistrationRequest request,
                                       HttpSession session, Authentication auth,
                                       Model model){
        String email;
        User user;

        if (auth == null) {
            boolean flag = false;

            if (userService.existsUserByEmail(request.getEmail())) {
                model.addAttribute("error", "Пользователь с таким email уже существует");
                flag = true;
            }
            if (userService.existsUserByPhoneNumber(request.getPhoneNumber())) {
                model.addAttribute("error", "Пользователь с таким номером телефона уже существует");
                flag = true;
            }
            if (request.getInitialDeposit().signum() == -1) {
                model.addAttribute("error", "Первоначальный депозит не может быть отрицательным");
                flag = true;
            }
            if (request.getPassword().length() < 8) {
                model.addAttribute("error", "Пароль должен быть не менее 8 символов");
                flag = true;
            }
            if (flag) {
                return "savings";
            }

            email = request.getEmail();
            user = userService.registerUser(request.getFirstName(), request.getLastName(),
                    request.getEmail(), request.getPhoneNumber(), request.getPassword());
        } else {
            email = auth.getName();
            user = userService.findUserByIdentifier(email);
        }

        session.setAttribute("email", email);

        accountService.createCreditAccount(user, request.getInitialDeposit(), new BigDecimal("500000"));

        SecurityContext context = setContext(user.getEmail());
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return "redirect:/account";
    }

    private SecurityContext setContext(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return context;
    }
}
