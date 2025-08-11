package dev.branches.utils;

import dev.branches.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserUtils {
    public static List<User> newUserList() {
        LocalDateTime createdAt1 = LocalDateTime.of(2025, 8, 9, 16, 0, 0);
        User user1 = new User("uuid-user-1", "Joel Lima", "joel@email.com", "encrypted-password-1", createdAt1, createdAt1);

        LocalDateTime createdAt2 = LocalDateTime.of(2025, 8, 10, 16, 0, 0);
        User user2 = new User("uuid-user-2", "Kauan Silva", "ksilva@email.com", "encrypted-password-2", createdAt2, createdAt2);

        return new ArrayList<>(List.of(user1, user2));
    };

    public static User newUserToRegister() {
        User user = new User();
        user.setName("Marcus Branches");
        user.setEmail("marcusbranches@dev.com");
        user.setPassword("encrypted-password");

        return user;
    }

    public static User newUserRegistered() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 11, 8, 16, 0, 0);

        return newUserToRegister()
                .withId("uuid-user-4")
                .withCreatedAt(createdAt)
                .withUpdatedAt(createdAt);
    }
}
