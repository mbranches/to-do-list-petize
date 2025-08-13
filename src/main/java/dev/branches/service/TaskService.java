package dev.branches.service;

import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.entity.User;
import dev.branches.exception.BadRequestException;
import dev.branches.exception.NotFoundException;
import dev.branches.repository.TaskRepository;
import dev.branches.repository.specification.TaskSpecification;
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
                pageable.getPageSize(),
                pageable.getSort()
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
                .orElseThrow(() -> new NotFoundException("Tarefa com id '%s' não encontrada".formatted(id)));
    }

    public void update(User requestingUser, String id, Task taskWithNewDatas, Optional<TaskStatus> statusOptional) {
        if (!id.equals(taskWithNewDatas.getId())) throw new BadRequestException("O id da url (%s) é diferente do id do corpo da requisição (%s)".formatted(id, taskWithNewDatas.getId()));

        Task taskToUpdate = findByIdAndUserOrThrowsNotFoundException(id, requestingUser);

        TaskStatus taskStatus = statusOptional.orElse(TaskStatus.PENDENTE);

        if(taskStatus.equals(TaskStatus.CONCLUIDA))
            assertThatTheTaskDoesHasNoSubtasksWithStatusDifferentOfConcluida(taskToUpdate);

        taskToUpdate.setTitle(taskWithNewDatas.getTitle());
        taskToUpdate.setDescription(taskWithNewDatas.getDescription());
        taskToUpdate.setStatus(taskStatus);
        taskToUpdate.setDueDate(taskWithNewDatas.getDueDate());
        taskToUpdate.setPriority(taskWithNewDatas.getPriority());

        repository.save(taskToUpdate);
    }

    private void assertThatTheTaskDoesHasNoSubtasksWithStatusDifferentOfConcluida(Task taskToVerify) {
        taskToVerify.getSubtasks()
                .forEach(task -> {
                    assertThatTheTaskDoesHasNoSubtasksWithStatusDifferentOfConcluida(task);

                    boolean taskHaveNonConcluidaStatus = !task.getStatus().equals(TaskStatus.CONCLUIDA);

                    if (taskHaveNonConcluidaStatus) {
                        throw new BadRequestException("Não é possível setar o status 'CONCLUIDA' à task, a subtask '%s' possui o status '%s'".formatted(task.getTitle(), task.getStatus()));
                    }
                });

    }

    public void updateStatus(User requestingUser, String id, TaskStatus status) {
        Task task = findByIdAndUserOrThrowsNotFoundException(id, requestingUser);

        if (status.equals(TaskStatus.CONCLUIDA))
            assertThatTheTaskDoesHasNoSubtasksWithStatusDifferentOfConcluida(task);

        task.setStatus(status);

        repository.save(task);
    }

    public void deleteById(User requestingUser, String id) {
        Task taskToDelete = findByIdAndUserOrThrowsNotFoundException(id, requestingUser);

        repository.delete(taskToDelete);
    }
}
