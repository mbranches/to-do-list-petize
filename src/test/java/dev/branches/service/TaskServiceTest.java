package dev.branches.service;

import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.repository.TaskRepository;
import dev.branches.utils.TaskUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @InjectMocks
    private TaskService service;
    @Mock
    private TaskRepository repository;
    private List<Task> taskList;

    @BeforeEach
    void init() {
        taskList = TaskUtils.newTaskList();
    }

    @Test
    @DisplayName("create creates task when successful")
    @Order(1)
    void create_CreatesTask_WhenSuccessful() {
        Task taskToCreate = TaskUtils.newTaskToCreate();
        Task createdTask = TaskUtils.newTaskCreated();

        when(repository.save(taskToCreate.withStatus(createdTask.getStatus())))
                .thenReturn(createdTask);

        Task response = service.create(taskToCreate, Optional.of(TaskStatus.EM_ANDAMENTO));

        assertThat(response)
                .isNotNull()
                .isEqualTo(createdTask);
    }

    @Test
    @DisplayName("create creates task with pendente status when the status argument is empty")
    @Order(2)
    void create_CreatesTaskWithPendenteStatus_WhenTheStatusArgumentIsEmpty() {
        Task taskToCreate = TaskUtils.newTaskToCreate();
        Task createdTask = TaskUtils.newTaskCreated().withStatus(TaskStatus.PENDENTE);

        when(repository.save(taskToCreate.withStatus(TaskStatus.PENDENTE)))
                .thenReturn(createdTask);

        Task response = service.create(taskToCreate, Optional.empty());

        assertThat(response)
                .isNotNull()
                .isEqualTo(createdTask);

        assertThat(response.getStatus())
                .isNotNull()
                .isEqualTo(TaskStatus.PENDENTE);
    }
}