package dev.branches.service;

import dev.branches.dto.LoginRequest;
import dev.branches.dto.RegisterRequest;
import dev.branches.entity.User;
import dev.branches.exception.BadRequestException;
import dev.branches.infra.security.JwtTokenService;
import dev.branches.repository.UserRepository;
import dev.branches.utils.UserUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService service;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private Authentication authentication;
    private List<User> userList;

    @BeforeEach
    void init() {
        userList = UserUtils.newUserList();
    }

    @Test
    @DisplayName("create creates user when successful")
    @Order(1)
    void create_CreatesUser_WhenSuccessful() {
        User userToRegister = UserUtils.newUserToRegister();

        User registeredUser = UserUtils.newUserRegistered();
        String password = "branches123";

        when(repository.findByEmail(userToRegister.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(password))
                .thenReturn(userToRegister.getPassword());
        when(repository.save(userToRegister))
                .thenReturn(registeredUser);

        assertThatNoException()
                .isThrownBy(() -> service.create(userToRegister.withPassword(null), password));
    }

    @Test
    @DisplayName("create throws BadRequestException when the user email to register belongs to another user")
    @Order(2)
    void create_ThrowsBadRequestException_WhenTheUserEmailToRegisterBelongsToAnotherUser() {
        User userEmailOwner = userList.getFirst();

        User userToRegister = new User();
        userToRegister.setName("qualquer nome");
        userToRegister.setEmail(userEmailOwner.getEmail());

        String password = "qualquer senha";

        when(repository.findByEmail(userToRegister.getEmail()))
                .thenReturn(Optional.of(userEmailOwner));

        assertThatThrownBy(() -> service.create(userToRegister, password))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("O email '%s' pertence a outro usuário".formatted(userEmailOwner.getEmail()));
    }

    @Test
    @DisplayName("login returns access jwt token when successful")
    @Order(3)
    void login_ReturnsAccessJwtToken_WhenSuccessful() {
        User userToLogin = userList.getFirst();

        String jwtToken = "jwt.token-xxx";

        when(authenticationManager.authenticate(ArgumentMatchers.any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal())
                .thenReturn(userToLogin);
        when(jwtTokenService.generateToken(userToLogin))
                .thenReturn(jwtToken);

        String response = service.login(userToLogin.getEmail(), userToLogin.getPassword());

        assertThat(response)
                .isNotNull()
                .isNotBlank()
                .isEqualTo(jwtToken);
    }
}