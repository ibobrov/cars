package ru.job4j.cars.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Check that the returned controller pages match the status in request.
 */
class CustomErrorControllerTest {
    private final CustomErrorController errorController = new CustomErrorController();
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    public void whenRenderErrorPageHaveRequestWith404ThenReturn404Page() {
        when(request.getAttribute(any())).thenReturn(HttpStatus.NOT_FOUND.value());
        var view = errorController.renderErrorPage(request);
        assertThat(view).isEqualTo("errors/error-404");
    }

    @Test
    public void whenRenderErrorPageHaveRequestWith500ThenReturn500Page() {
        when(request.getAttribute(any())).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        var view = errorController.renderErrorPage(request);
        assertThat(view).isEqualTo("errors/error-500");
    }

    @Test
    public void whenRenderErrorPageHaveRequestWithoutStatusThenReturnErrorPage() {
        when(request.getAttribute(any())).thenReturn(null);
        var view = errorController.renderErrorPage(request);
        assertThat(view).isEqualTo("errors/error");
    }
}