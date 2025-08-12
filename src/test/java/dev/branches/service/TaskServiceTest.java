package dev.branches.service;

import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.entity.User;
import dev.branches.repository.TaskRepository;
import dev.branches.utils.TaskUtils;
import dev.branches.utils.UserUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
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

    @Test
    @DisplayName("listAll returns all the requesting user tasks when successful")
    @Order(3)
    void listAll_ReturnsAllTheRequestingUserTasks_WhenSuccessful() {
        User userToGiven = taskList.getFirst().getUser();
        List<Task> userTasks = taskList.stream().filter(task -> task.getUser().equals(userToGiven)).toList();

        PageRequest pageRequest = PageRequest.of(0, userTasks.size());

        PageImpl<Task> expectedResponse = new PageImpl<>(userTasks, pageRequest, userTasks.size());

        when(repository.findAll(ArgumentMatchers.any(Specification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(expectedResponse);

        Page<Task> response = service.listAll(pageRequest, userToGiven, null, null, null, null);

        assertThat(response)
                .isNotNull();
        assertThat(response.getTotalElements())
                .isEqualTo(userTasks.size());
        assertThat(response.getSize())
                .isEqualTo(userTasks.size());
        assertThat(response.getContent())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(userTasks);
    }

    @Test
    @DisplayName("listAll returns an empty list when no tasks are found")
    @Order(4)
    void listAll_ReturnsEmptyList_WhenNoTasksAreFound() {
        User userWhoHasNoTasks = UserUtils.newUserList().get(1);

        PageRequest pageRequest = PageRequest.of(0, 10);

        PageImpl<Task> expectedResponse = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        when(repository.findAll(ArgumentMatchers.any(Specification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(expectedResponse);

        Page<Task> response = service.listAll(pageRequest, userWhoHasNoTasks, null, null, null, null);

        assertThat(response)
                .isNotNull();
        assertThat(response.getTotalElements())
                .isEqualTo(0);
        assertThat(response.getSize())
                .isEqualTo(10);
        assertThat(response.getContent())
                .isNotNull()
                .isEmpty();
    }
}