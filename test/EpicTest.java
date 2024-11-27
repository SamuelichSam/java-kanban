import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    void inIt() {
        taskManager = Managers.getDefault();
    }

    @Test
    void calculateStatusIfSubtasksAllNew() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = taskManager.addNewSubtask(new Subtask(1, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), 0));
        Subtask subtask2 = taskManager.addNewSubtask(new Subtask(2, "Подзадача-2", "Описание-2", Status.NEW, Duration.ofHours(2), LocalDateTime.now().plusDays(1), 0));

        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void calculateStatusIfSubtasksAllDone() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = taskManager.addNewSubtask(new Subtask(1, "Подзадача-1", "Описание-1", Status.DONE, Duration.ofHours(1), LocalDateTime.now(), 0));
        Subtask subtask2 = taskManager.addNewSubtask(new Subtask(2, "Подзадача-2", "Описание-2", Status.DONE, Duration.ofHours(2), LocalDateTime.now().plusDays(1), 0));

        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void calculateStatusIfSubtasksNewAndDone() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = taskManager.addNewSubtask(new Subtask(1, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), 0));
        Subtask subtask2 = taskManager.addNewSubtask(new Subtask(2, "Подзадача-2", "Описание-2", Status.DONE, Duration.ofHours(2), LocalDateTime.now().plusDays(1), 0));

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void calculateStatusIfSubtasksInProgress() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = taskManager.addNewSubtask(new Subtask(1, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), 0));
        Subtask subtask2 = taskManager.addNewSubtask(new Subtask(2, "Подзадача-2", "Описание-2", Status.IN_PROGRESS, Duration.ofHours(2), LocalDateTime.now().plusDays(1), 0));
        Subtask subtask3 = taskManager.addNewSubtask(new Subtask(3, "Подзадача-2", "Описание-2", Status.DONE, Duration.ofHours(2), LocalDateTime.now().plusDays(2), 0));

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}
