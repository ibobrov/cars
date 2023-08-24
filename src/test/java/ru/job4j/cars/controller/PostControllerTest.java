package ru.job4j.cars.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.NewPostDto;
import ru.job4j.cars.dto.PostDto;
import ru.job4j.cars.dto.PostPreview;
import ru.job4j.cars.model.CarModel;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.CarModelService;
import ru.job4j.cars.service.EngineService;
import ru.job4j.cars.service.PostService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostControllerTest {
    private final PostService postService = mock(PostService.class);
    private final CarModelService carModelService = mock(CarModelService.class);
    private final EngineService engineService = mock(EngineService.class);
    private final PostController postController = new PostController(postService, carModelService, engineService);
    private final ConcurrentModel model = new ConcurrentModel();

    @Test
    public void whenGetPostPageThenReturnThisPage() {
        var dto = new PostDto();
        var recommendation = List.of(new PostPreview());
        when(postService.findById(1)).thenReturn(Optional.of(dto));
        when(postService.getRecommendation(4)).thenReturn(recommendation);
        assertThat(postController.getPost(1, model)).isEqualTo("posts/post");
        assertThat(model.getAttribute("post")).isEqualTo(dto);
        assertThat(model.getAttribute("prevPosts")).isEqualTo(recommendation);
    }

    @Test
    public void whenGetPostPageThenReturnErrorPage() {
        assertThat(postController.getPost(1, model)).isEqualTo("errors/error-404");
    }

    @Test
    public void whenGetCreationPageThenReturnThisPage() {
        var models = List.of(new CarModel());
        var engines = List.of(new Engine());
        when(carModelService.getAll()).thenReturn(models);
        when(engineService.getAll()).thenReturn(engines);
        assertThat(postController.getCreation(model)).isEqualTo("posts/create");
        assertThat(model.getAttribute("models")).isEqualTo(models);
        assertThat(model.getAttribute("engines")).isEqualTo(engines);
    }

    @Test
    public void whenPostCreateWithPhotoThenCreateAndRedirectToCatalog() throws IOException {
        var session = mock(HttpSession.class);
        var dto = new NewPostDto();
        var multipartFile1 = mock(MultipartFile.class);
        var multipartFile2 = mock(MultipartFile.class);
        var photos = List.of(multipartFile1, multipartFile2);
        when(multipartFile1.getOriginalFilename()).thenReturn("file1");
        when(multipartFile2.getOriginalFilename()).thenReturn("file2");
        when(multipartFile1.getBytes()).thenReturn(new byte[] {1, 2});
        when(multipartFile2.getBytes()).thenReturn(new byte[] {3, 4});
        var user = new User(1);
        when(session.getAttribute("user")).thenReturn(user);
        var beforeConvert = List.of(new FileDto("file1", new byte[] {1, 2}),
                new FileDto("file2", new byte[] {3, 4}));
        when(postService.create(dto, user, beforeConvert)).thenReturn(true);
        assertThat(postController.create(photos, session, dto, model)).isEqualTo("redirect:/posts/catalog");
    }

    @Test
    public void whenPostCreateWithoutPhotoThenCreateAndRedirectToCatalog() throws IOException {
        var session = mock(HttpSession.class);
        var dto = new NewPostDto();
        var multipartFile1 = mock(MultipartFile.class);
        var photos = List.of(multipartFile1);
        when(multipartFile1.getOriginalFilename()).thenReturn("");
        when(multipartFile1.getBytes()).thenReturn(new byte[] {1, 2});
        var user = new User(1);
        when(session.getAttribute("user")).thenReturn(user);
        List<FileDto> beforeConvert = List.of();
        when(postService.create(dto, user, beforeConvert)).thenReturn(true);
        assertThat(postController.create(photos, session, dto, model)).isEqualTo("redirect:/posts/catalog");
    }

    @Test
    public void whenPostCreateThenReturnError() {
        var session = mock(HttpSession.class);
        var dto = new NewPostDto();
        assertThat(postController.create(List.of(), session, dto, model)).isEqualTo("errors/error");
        assertThat(model.getAttribute("message")).isEqualTo("Failed. Not added.");
    }

    @Test
    public void whenGetLastDayThenReturnThisPage() {
        var lastDayList = List.of(new PostPreview());
        when(postService.getLastDay()).thenReturn(lastDayList);
        assertThat(postController.getLastDay(model)).isEqualTo("posts/lastDay");
        assertThat(model.getAttribute("prevPosts")).isEqualTo(lastDayList);
    }

    @Test
    public void whenGetCatalogThenReturnThisPage() {
        var allList = List.of(new PostPreview());
        when(postService.getAll()).thenReturn(allList);
        assertThat(postController.getCatalog(model)).isEqualTo("posts/catalog");
        assertThat(model.getAttribute("prevPosts")).isEqualTo(allList);
    }

    @Test
    public void whenPostCatalogFindByFilterThenCallTheMethodAndReturnResult() {
        var filters = Map.of("k1", "v1");
        var byFilterList = List.of(new PostPreview());
        when(postService.findByFilter(filters)).thenReturn(byFilterList);
        assertThat(postController.findByFilter(filters, model)).isEqualTo("posts/catalog");
        assertThat(model.getAttribute("prevPosts")).isEqualTo(byFilterList);
    }
}