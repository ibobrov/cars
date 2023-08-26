package ru.job4j.cars.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * It checks the basic behavior of controllers, returning a negative
 * result and wrapping some methods in redirect.
 */
class UserControllerTest {
    private final UserService userService = mock(UserService.class);
    private final UserController userController = new UserController(userService);
    private final HttpSession session = mock(HttpSession.class);
    private final ConcurrentModel model = new ConcurrentModel();
    private final User user = new User(1, "test", "pass");

    @Test
    public void whenGetRegistrationPageThenGetThisPage() {
        assertThat(userController.getRegistrationPage()).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterUserThenLoginUserAndRedirectToReferrer() {
        when(userService.save(any(), any())).thenReturn(Optional.of(user));
        var view = userController.register("", user, "ilya", new ConcurrentModel(), session);
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenRegisterUserThenUserNotCreated() {
        when(userService.save(any(), any())).thenReturn(empty());
        var view = userController.register("", user, "ilya", model, session);
        assertThat(view).isEqualTo("users/register");
        assertThat(model.getAttribute("error"))
                .isEqualTo("The user with this login exists, or the data is incorrect.");
    }

    @Test
    public void whenRegisterUserAndRedirectThenUserCreatedAndRedirectToIndex() {
        when(userService.save(any(), any())).thenReturn(Optional.of(user));
        var view = userController.registerRedirect("", user, "ilya", model, session);
        assertThat(view).isEqualTo("redirect:/");
    }

    @Test
    public void whenTryRegisterAndRedirectToIndexNotExistUserThenRedirectToRegisterAgain() {
        when(userService.save(any(), any())).thenReturn(empty());
        var view = userController.registerRedirect("", user, "ilya", model, session);
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenGetLoginPageThenGetThisPage() {
        assertThat(userController.getLoginPage()).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserThenUserLogin() {
        when(userService.findByLoginPassword(any(), any())).thenReturn(Optional.of(user));
        var view = userController.loginUser("/", user, model, session);
        verify(session).setAttribute("user", user);
        assertThat(view).isEqualTo("redirect:/");
    }

    @Test
    public void whenLoginUserThenUserNotLoginBecauseUserNotFound() {
        when(userService.findByLoginPassword(any(), any())).thenReturn(empty());
        var view = userController.loginUser("", user, model, session);
        assertThat(view).isEqualTo("users/login");
        assertThat(model.getAttribute("error"))
                .isEqualTo("Login or password entered incorrectly");
    }

    @Test
    public void whenLoginUserThenUserLoginAndRedirectToIndex() {
        when(userService.findByLoginPassword(any(), any())).thenReturn(Optional.of(user));
        var view = userController.loginUserRedirect("/user/login", user, model, session);
        verify(session).setAttribute("user", user);
        assertThat(view).isEqualTo("redirect:/");
    }

    @Test
    public void whenTryLoginAndRedirectToIndexNotExistUserThenRedirectToLoginAgain() {
        when(userService.findByLoginPassword(any(), any())).thenReturn(empty());
        var view = userController.loginUserRedirect("/user/login", user, model, session);
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLogoutUserThenLogout() {
        var view = userController.logout("/", session);
        verify(session).invalidate();
        assertThat(view).isEqualTo("redirect:/");
    }
}