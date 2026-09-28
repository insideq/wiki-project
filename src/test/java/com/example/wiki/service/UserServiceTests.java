package com.example.wiki.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.PageRs;
import com.example.wiki.api.user.UserRq;
import com.example.wiki.api.user.UserRs;
import com.example.wiki.api.user.UserUpdateRq;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.error.AlreadyExistsException;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.error.PasswordConfirmationException;
import com.example.wiki.repository.UserRepository;

@SpringBootTest
@Transactional
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "qwerty123";

    @BeforeEach
    void setUp() {
        if (userRepository.findByLoginIgnoreCase(LOGIN).isEmpty()) {
            userService.create(new UserRq(LOGIN, PASSWORD, PASSWORD));
        }
    }

    @Test
    void create_shouldReturnUser() {
        final UserRs user = userService.create(new UserRq("new_user", "pass123", "pass123"));

        assertThat(user).isNotNull();
        assertThat(user.login()).isEqualTo("new_user");
        assertThat(user.role()).isEqualTo("USER");
    }

    @Test
    void create_shouldThrowIfDuplicate() {
        assertThatThrownBy(() -> userService.create(new UserRq(LOGIN, PASSWORD, PASSWORD)))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void create_shouldThrowIfPasswordsDontMatch() {
        assertThatThrownBy(() -> userService.create(new UserRq("mismatch_user", "pass1", "pass2")))
                .isInstanceOf(PasswordConfirmationException.class);
    }

    @Test
    void get_shouldReturnUser() {
        final UserRs user = userService.get(LOGIN);

        assertThat(user).isNotNull();
        assertThat(user.login()).isEqualTo(LOGIN);
    }

    @Test
    void get_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> userService.get("no_such_user"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAll_shouldReturnPage() {
        final PageRs<UserRs> page = userService.getAll(PageRequest.of(0, 10));

        assertThat(page.items()).isNotEmpty();
    }

    @Test
    void update_shouldChangePassword() {
        final UserRs updated = userService.update(
                LOGIN,
                new UserUpdateRq(PASSWORD, "new_password123", "new_password123"));

        assertThat(updated.login()).isEqualTo(LOGIN);

        // проверяем, что новый пароль работает
        final UserDetails details = userService.loadUserByUsername(LOGIN);
        assertThat(details.getPassword()).isNotEqualTo(PASSWORD);
    }

    @Test
    void update_shouldThrowIfOldPasswordIncorrect() {
        assertThatThrownBy(() -> userService.update(
                LOGIN,
                new UserUpdateRq("wrong_old", "new_pass123", "new_pass123")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void delete_shouldRemoveUser() {
        userService.create(new UserRq("to_delete", "pass123", "pass123"));

        userService.delete("to_delete", "pass123");

        assertThatThrownBy(() -> userService.get("to_delete"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_shouldThrowIfPasswordIncorrect() {
        assertThatThrownBy(() -> userService.delete(LOGIN, "wrong_pass"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        final UserDetails details = userService.loadUserByUsername(LOGIN);

        assertThat(details).isNotNull();
        assertThat(details.getUsername()).isEqualTo(LOGIN);
        assertThat(details.getAuthorities())
                .anyMatch(a -> "ROLE_USER".equals(a.getAuthority()));
    }

    @Test
    void loadUserByUsername_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> userService.loadUserByUsername("no_such_user"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getEntityByLogin_shouldReturnEntity() {
        final UserEntity entity = userService.getEntityByLogin(LOGIN);

        assertThat(entity).isNotNull();
        assertThat(entity.getLogin()).isEqualTo(LOGIN);
    }
}