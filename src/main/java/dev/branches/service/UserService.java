package dev.branches.service;

import dev.branches.dto.LoginResponse;
import dev.branches.dto.RegisterRequest;
import dev.branches.entity.User;
import dev.branches.exception.BadRequestException;
import dev.branches.infra.security.JwtTokenService;
import dev.branches.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Tag(name = "Autenticação")
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public void create(RegisterRequest request) {
        String userEmail = request.email();

        assertThatEmailDoesNotBelongsToAnotherUser(userEmail);

        User user = new User();
        user.setEmail(userEmail);
        user.setName(request.name());
        user.setPassword(passwordEncoder.encode(request.password()));

        repository.save(user);
    }

    private void assertThatEmailDoesNotBelongsToAnotherUser(String email) {
        repository.findByEmail(email)
                .ifPresent(user -> {
                    throw new BadRequestException("O email '%s' pertence a outro usuário".formatted(email));
                });
    }

    public String login(String email, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        User user = (User) authentication.getPrincipal();

        return jwtTokenService.generateToken(user);
    }
}
