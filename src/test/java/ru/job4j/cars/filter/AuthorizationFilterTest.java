package ru.job4j.cars.filter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

class AuthorizationFilterTest {
    private final AuthorizationFilter filter = new AuthorizationFilter();
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain chain = mock(FilterChain.class);

    @Test
    public void whenFilterCallsNextInChain() {
        var thrown = catchThrowable(() -> {
            when(request.getRequestURI()).thenReturn("/anything");
            doThrow(new MockitoException("test")).when(chain).doFilter(request, response);
            filter.doFilter(request, response, chain);
        });
        assertThat(thrown).isInstanceOf(MockitoException.class);
        assertThat(thrown.getMessage()).isEqualTo("test");
    }

    @Test
    public void whenFilterRedirectBecauseUserNotAuthorized() {
        var redirect = ArgumentCaptor.forClass(String.class);
        var thrown = catchThrowable(() -> {
            when(request.getRequestURI()).thenReturn("/posts/create");
            var session = mock(HttpSession.class);
            when(request.getSession()).thenReturn(session);
            when(session.getAttribute("user")).thenReturn(null);
            when(request.getContextPath()).thenReturn("");
            doThrow(new MockitoException("test")).when(response).sendRedirect(redirect.capture());
            filter.doFilter(request, response, chain);
        });
        assertThat(thrown).isInstanceOf(MockitoException.class);
        assertThat(thrown.getMessage()).isEqualTo("test");
        assertThat(redirect.getValue()).isEqualTo("/users/login");
    }
}