package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    private User user;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        user = new User(1, "email@email.com", "name", "password");
    }

    @Test
    public void whenRequestRegisterPageThenGetRegisterPage() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterUserThenGetSameDataAndRedirectToIndexPage() {
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/index");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenRegisterUserThatDBHasThenCanNotGetIndexPageAndGet404PageWithErrorMessage() {
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void whenRequestLoginPageThenGetLoginPage() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserThenGetSameDataAndRedirectToIndexPage() {
        var userEmailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var userPasswordArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(userEmailArgumentCaptor.capture(), userPasswordArgumentCaptor.capture()))
                .thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, new MockHttpServletRequest());
        var actualUserEmail = userEmailArgumentCaptor.getValue();
        var actualUserPassword = userPasswordArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/index");
        assertThat(actualUserEmail).isEqualTo(user.getEmail());
        assertThat(actualUserPassword).isEqualTo(user.getPassword());
    }

    @Test
    public void whenLoginUserWithWrongDataThenCanNotGetIndexPageAndGetLoginPageWithErrorMessage() {
        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, new MockHttpServletRequest());
        var actualErrorMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualErrorMessage).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenRequestLogoutThenRedirectToLoginPage() {
        MockHttpSession session = new MockHttpSession();
        var view = userController.logout(session);

        assertThat(session.isInvalid()).isTrue();
        assertThat(view).isEqualTo("redirect:/users/login");
    }

}