package dev.branches.utils;

import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TaskUtils {
    public static List<Task> newTaskList() {
        User ownerUser1 = UserUtils.newUserList().getFirst();
        LocalDate dueDate1 = LocalDate.of(2025, 8, 9);
        LocalDateTime createdAt1 = LocalDateTime.of(dueDate1, LocalTime.of(16, 0, 0));

        Task task1 = Task.builder()
                .id("uuid-task-1")
                .user(ownerUser1)
                .title("Realizar teste técnico")
                .description("Realizar teste técnico para vaga de estágio em backend da Petize")
                .dueDate(dueDate1)
                .status(TaskStatus.EM_ANDAMENTO)
                .priority(Priority.ALTA)
                .subtasks(new ArrayList<>())
                .createdAt(createdAt1)
                .updatedAt(createdAt1)
                .build();

        LocalDate dueDate2 = LocalDate.of(2025, 8, 10);
        LocalDateTime createdAt2 = LocalDateTime.of(dueDate2, LocalTime.of(16, 0, 0));

        Task task2 = Task.builder()
                .id("uuid-task-2")
                .user(ownerUser1)
                .title("Documentar teste técnico PETIZE")
                .description("Documentar o teste técnico para vaga de estágio em backend da Petize")
                .dueDate(dueDate2)
                .status(TaskStatus.CONCLUIDA)
                .priority(Priority.ALTA)
                .subtasks(new ArrayList<>())
                .parent(task1)
                .createdAt(createdAt2)
                .updatedAt(createdAt2)
                .build();

        User ownerUser2 = UserUtils.newUserList().getLast();
        LocalDate dueDate3 = LocalDate.of(2025, 8, 11);
        LocalDateTime createdAt3 = LocalDateTime.of(dueDate3, LocalTime.of(16, 0, 0));

        Task task3 = Task.builder()
                .id("uuid-task-3")
                .user(ownerUser2)
                .title("Documentar teste técnico PETIZE")
                .description("Documentar o teste técnico para vaga de estágio em backend da Petize")
                .dueDate(dueDate3)
                .status(TaskStatus.PENDENTE)
                .priority(Priority.ALTA)
                .subtasks(new ArrayList<>())
                .createdAt(createdAt3)
                .updatedAt(createdAt3)
                .build();

        return new ArrayList<>(List.of(task1, task2, task3));
    }

    public static Task newTaskToCreate() {
        User ownerUser = UserUtils.newUserList().getFirst();

        LocalDate dueDate = LocalDate.of(2025, 8, 12);
        return Task.builder()
                .user(ownerUser)
                .title("Limpar a casa")
                .description("Varrer e passar pano na sala e na cozinha")
                .dueDate(dueDate)
                .priority(Priority.REGULAR)
                .build();
    }

    public static Task newTaskCreated() {
        LocalDateTime createdAt = LocalDateTime.of(newTaskToCreate().getDueDate(), LocalTime.of(16, 0, 0));

        return newTaskToCreate()
                .withId("uuid-task-4")
                .withCreatedAt(createdAt)
                .withUpdatedAt(createdAt);
    }

    public static Task newTaskWithNewDataForUpdate() {
        Task taskToUpdate = newTaskList().getFirst();

        return Task.builder()
                .id(taskToUpdate.getId())
                .user(taskToUpdate.getUser())
                .title("New title")
                .description("New description")
                .dueDate(taskToUpdate.getDueDate())
                .build();
    }

    public static Task newTaskUpdated() {
        Task taskToUpdate = newTaskList().getFirst();

        return taskToUpdate
                .withTitle(newTaskWithNewDataForUpdate().getTitle())
                .withDescription(newTaskWithNewDataForUpdate().getDescription())
                .withDueDate(newTaskWithNewDataForUpdate().getDueDate())
                .withStatus(TaskStatus.EM_ANDAMENTO);
    }
}
