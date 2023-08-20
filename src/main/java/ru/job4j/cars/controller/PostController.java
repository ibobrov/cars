package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.NewPostDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.CarModelService;
import ru.job4j.cars.service.EngineService;
import ru.job4j.cars.service.FileService;
import ru.job4j.cars.service.PostService;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postService;
    private final CarModelService carModelService;
    private final EngineService engineService;
    private final FileService fileService;

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
    public String create(List<MultipartFile> files, NewPostDto dto, Model model) throws IOException {
        var user = new User();
        user.setId(1);
        if (!postService.create(dto, user, saveFiles(files))) {
            model.addAttribute("message", "Failed. Not added.");
            return "errors/error";
        }
        return "redirect:/index";
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

    private Set<File> saveFiles(List<MultipartFile> files) throws IOException {
        var set = new HashSet<File>();
        for (var file : files) {
            set.add(fileService.save(new FileDto(file.getOriginalFilename(), file.getBytes())));
        }
        return set;
    }
}
