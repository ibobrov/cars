package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                           @ModelAttribute User user, Model model, HttpSession session) {
        var userOptional = userService.save(user);
        if (userOptional.isEmpty()) {
            model.addAttribute("error",
                    "The user with this login exists, or the data is incorrect.");
            return "users/register";
        }
        return loginUser(referrer, user, model, session);
    }

    @PostMapping("/registerRedirect")
    public String registerRedirect(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                           @ModelAttribute User user, Model model, HttpSession session) {
        var link = register(referrer, user, model, session);
        return link.equals("users/register") ? "users/register" : "redirect:/";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                            @ModelAttribute User user, Model model, HttpSession session) {
        var userOptional = userService.findByLoginPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Login or password entered incorrectly");
            return "users/login";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:" + referrer;
    }

    @PostMapping("/loginRedirect")
    public String loginUserRedirect(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                            @ModelAttribute User user, Model model, HttpSession session) {
        var link = loginUser(referrer, user, model, session);
        return link.equals("users/login") ? "users/login" : "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                         HttpSession session) {
        session.invalidate();
        return "redirect:" + referrer;
    }
}
