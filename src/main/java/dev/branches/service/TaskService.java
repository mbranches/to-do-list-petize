package dev.branches.service;

import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.entity.User;
import dev.branches.exception.BadRequestException;
import dev.branches.exception.NotFoundException;
import dev.branches.repository.TaskRepository;
import dev.branches.repository.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static dev.branches.repository.specification.TaskSpecification.*;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository repository;

    public Task create(Task taskToCreate, Optional<TaskStatus> optionalStatus) {
        TaskStatus status = optionalStatus.orElse(TaskStatus.PENDENTE);

        taskToCreate.setStatus(status);

        return repository.save(taskToCreate);
    }

    public Page<Task> listAll(Pageable pageable,
                              User requestingUser,
                              TaskStatus status,
                              Priority priority,
                              LocalDate dueDateFrom,
                              LocalDate dueDateTo) {
        Specification<Task> filter = TaskSpecification.taskOwnedBy(requestingUser)
                .and(taskHasStatus(status))
                .and(taskHasPriority(priority))
                .and(taskHasDueDateGreaterThanOrEqualTo(dueDateFrom))
                .and(taskHasDueDateLessThanOrEqualTo(dueDateTo));

        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return repository.findAll(filter, safePageable);
    }

    public Task addSubtask(User requestingUser, String parentTaskId, Task subtaskToCreate, Optional<TaskStatus> statusOptional) {
        Task parentTask = findByIdAndUserOrThrowsNotFoundException(parentTaskId, requestingUser);

        TaskStatus subtaskStatus = statusOptional.orElse(TaskStatus.EM_ANDAMENTO);

        boolean parentTaskHasConcluidaStatus = parentTask.getStatus().equals(TaskStatus.CONCLUIDA);
        if(parentTaskHasConcluidaStatus && !subtaskStatus.equals(TaskStatus.CONCLUIDA)) {
            throw new BadRequestException("Não é possível adicionar uma task que não foi concluída a uma task concluída");
        }

        subtaskToCreate.setStatus(subtaskStatus);
        subtaskToCreate.setParent(parentTask);

        Task createdSubtask = repository.save(subtaskToCreate);

        parentTask.getSubtasks().add(createdSubtask);

        return parentTask;
    }

    public Task findByIdAndUserOrThrowsNotFoundException(String id, User user) {
        return repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Task com id '%s' não encontrada".formatted(id)));
    }
}
