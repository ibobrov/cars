package ru.job4j.cars.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cars.service.PostService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class IndexControllerTest {
    private final PostService postService = mock(PostService.class);
    private final ConcurrentModel model = new ConcurrentModel();

    @Test
    public void getGetIndexThenGetIndexPage() {
        var indexController = new IndexController(postService);
        var view = indexController.getIndex(model);
        assertThat(view).isEqualTo("index");
    }
}