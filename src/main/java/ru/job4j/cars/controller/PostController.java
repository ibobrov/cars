package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.NewPostDto;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.HibernatePostRepository;
import ru.job4j.cars.service.CarModelService;
import ru.job4j.cars.service.EngineService;
import ru.job4j.cars.service.PostService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postService;
    private final CarModelService carModelService;
    private final EngineService engineService;
    private final Logger logger = LoggerFactory.getLogger(HibernatePostRepository.class);

    @GetMapping("/{id}")
    public String getPost(@PathVariable int id, Model model) {
        var post = postService.findById(id);
        if (post.isEmpty()) {
            return "errors/error-404";
        }
        model.addAttribute("post", post.get());
        model.addAttribute("prevPosts",
                postService.getRecommendation(IndexController.RECOMMENDATION_SIZE));
        return "posts/post";
    }

    @GetMapping("/create")
    public String getCreation(Model model) {
        model.addAttribute("models", carModelService.getAll());
        model.addAttribute("engines", engineService.getAll());
        return "posts/create";
    }

    @PostMapping("/create")
    public String create(List<MultipartFile> files, HttpSession session, NewPostDto dto, Model model) {
        var user = (User) session.getAttribute("user");
        if (!postService.create(dto, user, convertFiles(files))) {
            model.addAttribute("message", "Failed. Not added.");
            return "errors/error";
        }
        return "redirect:/posts/catalog";
    }

    @GetMapping("/last_day")
    public String getLastDay(Model model) {
        model.addAttribute("prevPosts", postService.getLastDay());
        return "posts/lastDay";
    }

    @GetMapping("/catalog")
    public String getCatalog(Model model) {
        model.addAttribute("prevPosts", postService.getAll());
        return "posts/catalog";
    }

    @PostMapping("/catalog")
    public String findByFilter(@RequestParam Map<String, String> filter, Model model) {
        model.addAttribute("prevPosts", postService.findByFilter(filter));
        return "posts/catalog";
    }

    @GetMapping("/user_posts")
    public String getUserPosts(HttpSession session, Model model) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("message", "User info not found.");
            return "errors/error-404";
        }
        model.addAttribute("prevPosts", postService.findByUser(user.getId()));
        return "posts/userPosts";
    }

    @PostMapping("/hide")
    public String hidePost(@RequestParam int id, HttpSession session, Model model) {
        var user = (User) session.getAttribute("user");
        if (user == null || !postService.hide(id)) {
            model.addAttribute("message", "User or post not found.");
            return "errors/error-404";
        }
        return "redirect:/posts/user_posts";
    }

    /**
     * Not for production, there is a nuance. Quoting <b>getOriginalFilename()</b> method:
     * "Note: Please keep in mind this filename is supplied by the client and should not be used blindly.
     * In addition to not using the directory portion, the file name could also contain characters such
     * as ".." and others that can be used maliciously. It is recommended to not use this filename directly.
     * Preferably generate a unique one and save this one somewhere for reference, if necessary."
     */
    private List<FileDto> convertFiles(List<MultipartFile> files) {
        var rsl = new ArrayList<FileDto>();
        for (var file : files) {
            try {
                if (!Objects.equals(file.getOriginalFilename(), "")) {
                    rsl.add(new FileDto(file.getOriginalFilename(), file.getBytes()));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return rsl;
    }
}
