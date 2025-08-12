package dev.branches.repository.specification;

import dev.branches.entity.Priority;
import dev.branches.entity.Task;
import dev.branches.entity.TaskStatus;
import dev.branches.entity.User;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecification {
    public static Specification<Task> taskOwnedBy(User user) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(user)) return null;

            return builder.equal(root.get("user"), user);
        };
    }

    public static Specification<Task> taskHasStatus(TaskStatus status) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(status)) return null;

            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Task> taskHasPriority(Priority priority) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(priority)) return null;

            return builder.equal(root.get("priority"), priority);
        };
    }

    public static Specification<Task> taskHasDueDateLessThanOrEqualTo(LocalDate dueDate) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(dueDate)) return null;

            return builder.lessThanOrEqualTo(root.get("dueDate"), dueDate);
        };
    }

    public static Specification<Task> taskHasDueDateGreaterThanOrEqualTo(LocalDate dueDate) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(dueDate)) return null;

            return builder.greaterThanOrEqualTo(root.get("dueDate"), dueDate);
        };
    }
}
