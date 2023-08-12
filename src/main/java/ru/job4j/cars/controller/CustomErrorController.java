package ru.job4j.cars.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String renderErrorPage(HttpServletRequest request) {
        var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        var statusCode = 0;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }
        return switch (statusCode) {
            case 404 -> "errors/error-404";
            case 500 -> "errors/error-500";
            default -> "errors/error";
        };
    }
}
