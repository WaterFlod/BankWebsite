package com.bank.account.controller;

import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.dto.RegistrationDebitRequest;
import com.bank.account.dto.RegistrationRequest;
import com.bank.account.model.AccountType;
import com.bank.account.security.CustomUserDetailsService;
import com.bank.account.security.UserDetailsImpl;
import com.bank.account.service.AccountService;
import com.bank.account.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final UserService userService;
    private final AccountService accountService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/debit")
    public String debitAccount(Model model) {
        model.addAttribute("request", new RegistrationDebitRequest());
        return "debit";
    }

    @PostMapping("/debit")
    public String createDebitAccount(@Valid @ModelAttribute("request") RegistrationDebitRequest request,
                                     HttpSession session,
                                     Model model){
        if (userService.existsUserByEmail(request.getEmail())) {
            model.addAttribute("error", "Пользователь с таким email уже существует");
            return "debit";
        }
        if (userService.existsUserByPhoneNumber(request.getPhoneNumber())) {
            model.addAttribute("error", "Пользователь с таким номером телефона уже существует");
            return "debit";
        }
        if (request.getInitialDeposit().signum() == -1) {
            model.addAttribute("error", "Первоначальный депозит не может быть отрицательным");
            return "debit";
        }
        userService.registerUser(debitRequestToRegistrationRequest(request));

        session.setAttribute("email", request.getEmail());

        CreateAccountRequest createAccountRequest = new CreateAccountRequest(request.getEmail(), AccountType.CHECKING, request.getInitialDeposit());

        accountService.createAccount(createAccountRequest);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return "redirect:/auth/set-password";
    }

    @GetMapping("/credit")
    public String creditAccount(Model model) {
        return "credit";
    }

    @GetMapping("/savings")
    public String savingsAccount(Model model) {
        return "savings";
    }

    private RegistrationRequest debitRequestToRegistrationRequest(RegistrationDebitRequest debRequest) {
        return new RegistrationRequest(
                debRequest.getEmail(),
                debRequest.getPhoneNumber(),
                debRequest.getFirstName(),
                debRequest.getLastName()
        );
    }
}
