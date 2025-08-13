package dev.branches.entity;

import dev.branches.dto.request.TaskPostRequest;
import dev.branches.dto.request.TaskPutRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ToString
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
@Getter
@Entity(name = "tb_task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;
    @Column(length = 50, nullable = false)
    private String title;
    @Column(length = 200)
    private String description;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private Task parent;
    @OneToMany(mappedBy = "parent")
    private List<Task> subtasks;

    public static Task by(User user, TaskPostRequest postRequest) {
        return Task.builder()
                .user(user)
                .title(postRequest.title())
                .description(postRequest.description())
                .dueDate(LocalDate.parse(postRequest.dueDate()))
                .priority(Priority.valueOf(postRequest.priority()))
                .build();
    }

    public static Task by(User user, TaskPutRequest putRequest) {
        return Task.builder()
                .id(putRequest.id())
                .user(user)
                .title(putRequest.title())
                .description(putRequest.description())
                .dueDate(LocalDate.parse(putRequest.dueDate()))
                .priority(Priority.valueOf(putRequest.priority()))
                .build();
    }
}
