package dev.branches.service;

import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository repository;

    public Task create(Task taskToCreate, Optional<TaskStatus> optionalStatus) {
        TaskStatus status = optionalStatus.orElse(TaskStatus.PENDENTE);

        taskToCreate.setStatus(status);

        return repository.save(taskToCreate);
    }
}
