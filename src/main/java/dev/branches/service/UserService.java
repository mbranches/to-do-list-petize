package dev.branches.service;

import dev.branches.dto.RegisterRequest;
import dev.branches.entity.User;
import dev.branches.exception.BadRequestException;
import dev.branches.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Tag(name = "Autenticação")
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

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
}
