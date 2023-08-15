package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.dto.Filter;
import ru.job4j.cars.service.PostService;

@Controller
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    public String getPost(@PathVariable int id, Model model) {
        var post = postService.findById(id);
        if (post.isEmpty()) {
            return "errors/error-404";
        }
        model.addAttribute("post", post.get());
        return "posts/post";
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
    public String findByFilter(Filter filter, Model model) {
        model.addAttribute("prevPosts", postService.findByFilter(filter));
        return "posts/catalog";
    }
}
