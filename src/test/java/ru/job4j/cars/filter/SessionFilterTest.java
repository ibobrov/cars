package ru.job4j.cars.filter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoException;
import ru.job4j.cars.model.User;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

class SessionFilterTest {
    private final SessionFilter filter = new SessionFilter();
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain chain = mock(FilterChain.class);

    @Test
    public void whenSessionWithoutUserThenFilterAddGuestUserAndCallNextInChain() {
        var captorUser = ArgumentCaptor.forClass(User.class);
        var session = mock(HttpSession.class);
        var thrown = catchThrowable(() -> {
            when(request.getSession()).thenReturn(session);
            when(session.getAttribute("user")).thenReturn(null);
            doThrow(new MockitoException("test")).when(chain).doFilter(request, response);
            doNothing().when(request).setAttribute(any(), captorUser.capture());
            filter.doFilter(request, response, chain);
        });
        assertThat(thrown).isInstanceOf(MockitoException.class);
        assertThat(thrown.getMessage()).isEqualTo("test");
        assertThat(captorUser.getValue().getLogin()).isEqualTo("Guest");
    }

    @Test
    public void whenSessionHaveUserThenFilterCallsNextInChain() {
        var captorUser = ArgumentCaptor.forClass(User.class);
        var user = new User();
        user.setLogin("name");
        var session = mock(HttpSession.class);
        var thrown = catchThrowable(() -> {
            when(request.getSession()).thenReturn(session);
            when(session.getAttribute("user")).thenReturn(user);
            doThrow(new MockitoException("test")).when(request).setAttribute(any(), captorUser.capture());
            filter.doFilter(request, response, chain);
        });
        assertThat(thrown).isInstanceOf(MockitoException.class);
        assertThat(thrown.getMessage()).isEqualTo("test");
        assertThat(captorUser.getValue()).isEqualTo(user);
    }
}