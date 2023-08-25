package ru.job4j.cars.service;

import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleUserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final SimpleUserService userService = new SimpleUserService(userRepository);

    @Test
    public void whenSaveUserThenReturnSameUser() {
        var user = new User(1, "login", "pass");
        when(userRepository.save(user)).thenReturn(user);
        assertThat(userService.save(user).get()).isEqualTo(user);
    }

    @Test
    public void whenSaveUserThenReturnEmpty() {
        var user1 = new User(-1, "", "Password1");
        var user2 = new User(-1, "name2", "");
        var user3 = new User(0, "name3", "Password3");
        when(userRepository.save(any())).thenReturn(new User());
        assertThat(userService.save(user1)).isEqualTo(empty());
        assertThat(userService.save(user2)).isEqualTo(empty());
        assertThat(userService.save(user3)).isEqualTo(empty());
    }

    @Test
    public void whenFindByMailAndPasswordThenReturnUser() {
        var user = new User();
        when(userRepository.findByLoginPassword(any(), any())).thenReturn(Optional.of(user));
        assertThat(userService.findByLoginPassword("", "").get()).isEqualTo(user);
    }

    @Test
    public void whenFindByMailAndPasswordThenReturnEmpty() {
        when(userRepository.findByLoginPassword(any(), any())).thenReturn(empty());
        assertThat(userService.findByLoginPassword("", "")).isEqualTo(empty());
    }
}