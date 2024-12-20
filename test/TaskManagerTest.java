import exceptions.ManagerSaveException;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected abstract T createTaskManager();

    @BeforeEach
    void inIt() {
        taskManager = createTaskManager();
    }

    @Test
    void addNewTaskShouldSaveTask() {
        Task task = new Task(0,"Задача-1", "Описание-1");
        Task expectedTask = new Task(0, "Задача-1", "Описание-1");

        Task savedTask = taskManager.addNewTask(task);

        Assertions.assertNotNull(savedTask, "Задача не сохранилась");
        Assertions.assertNotNull(savedTask.getId(), "Не сгенерировался id");
        Assertions.assertEquals(expectedTask, savedTask, "Задачи не совпадают");
    }

    @Test
    void taskEqualsById() {
        Task task = new Task(0, "Задача-1", "Описание-1");
        Task anotherTask = new Task(0, "Задача-2", "Описание-2");

        Task addedTask = taskManager.addNewTask(task);

        Assertions.assertEquals(addedTask, anotherTask, "Задачи не совпадают");
    }

    @Test
    void updateTaskShouldUpdateTaskById() {
        Task task = new Task(0, "Задача-1", "Описание-1");
        Task addedTask = taskManager.addNewTask(task);
        Task updatedTask = new Task(addedTask.getId(), "Задача-1 upd", "Описание-1 upd");
        Task expectedUpdatedTask =
                new Task(addedTask.getId(), "Задача-1 upd", "Описание-1 upd");

        taskManager.updateTask(updatedTask);

        Assertions.assertEquals(expectedUpdatedTask, updatedTask, "После обновления задачи не совпадают");
    }

    @Test
    void addNewSubtaskShouldSaveTask() {
        Epic epic = new Epic(0,"Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(1,"Задача-1", "Описание-1", 0);
        Subtask expectedSubtask = new Subtask(1, "Задача-1", "Описание-1", 0);
        Epic addedEpic = taskManager.addNewEpic(epic);

        Task addedSubtask = taskManager.addNewSubtask(subtask);

        Assertions.assertNotNull(addedSubtask, "Задача не сохранилась");
        Assertions.assertNotNull(addedSubtask.getId(), "Не сгенерировался id");
        Assertions.assertEquals(expectedSubtask, addedSubtask, "Задачи не совпадают");
    }

    @Test
    void subtaskShouldNotAddedIfEpicIsNull() {
        Epic epic = null;
        Subtask subtask = new Subtask(0, "Задача-1", "Описание-1", 0);
        boolean exception = false;

        try {
            taskManager.addNewSubtask(subtask);
        } catch (ManagerSaveException e) {
            exception = true;
        }

        Assertions.assertTrue(exception, "Подзадача не может быть создана без Эпика");
    }

    @Test
    void subTaskEqualsById() {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(1, "Задача-2", "Описание-2", 0);
        Subtask expectedSubtask =
                new Subtask(1, "Задача-2", "Описание-2", 0);
        Epic addedEpic = taskManager.addNewEpic(epic);

        Subtask addedSubtask = taskManager.addNewSubtask(subtask);

        Assertions.assertEquals(addedSubtask, expectedSubtask, "Задачи не совпадают");
    }

    @Test
    void addNewEpicShouldSaveEpic() {
        Epic epic = new Epic(0, "Задача-1", "Описание-1");
        Epic expectedEpic = new Epic(0, "Задача-1", "Описание-1");

        Task addedEpic = taskManager.addNewEpic(epic);

        Assertions.assertNotNull(addedEpic, "Задача не сохранилась");
        Assertions.assertNotNull(addedEpic.getId(), "Не сгенерировался id");
        Assertions.assertEquals(expectedEpic, addedEpic, "Задачи не совпадают");
    }

    @Test
    void epicEqualsById() {
        Epic epic = new Epic(0, "Задача-1", "Описание-1");
        Epic anotherEpic = new Epic(0, "Задача-2", "Описание-2");

        Epic addedEpic = taskManager.addNewEpic(epic);

        Assertions.assertEquals(addedEpic, anotherEpic, "Задачи не совпадают");
    }

    @Test
    void generateIdShouldNotConflictId() {
        Task task = new Task(0,"Задача-1", "Описание-1");
        Task anotherTask = new Task(0, "Задача-2", "Описание-2");
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Epic anotherEpic = new Epic(0, "Эпик-2", "Описание-2");
        Subtask subtask = new Subtask(0, "Подзадача-1", "Описание-1",  3);
        Subtask anotherSubtask = new Subtask(0, "Подзадача-2", "Описание-2", 3);

        Task addedTask = taskManager.addNewTask(task);
        Task addedAnotherTask = taskManager.addNewTask(anotherTask);
        Epic addedEpic = taskManager.addNewEpic(epic);
        Epic addedAnotherEpic = taskManager.addNewEpic(anotherEpic);
        Subtask addedSubtask = taskManager.addNewSubtask(subtask);
        Subtask addedAnotherSubtask = taskManager.addNewSubtask(anotherSubtask);

        Assertions.assertNotEquals(addedTask.getId(), addedAnotherTask.getId(), "Конфликт id");
        Assertions.assertNotEquals(addedEpic.getId(), addedAnotherEpic.getId(), "Конфликт id");
        Assertions.assertNotEquals(addedSubtask.getId(), addedAnotherSubtask.getId(), "Конфликт id");
    }

    @Test
    void deletedSubtasksShouldNotSaveOldId() {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(1, "Задача-2", "Описание-2", 0);
        Epic addedEpic = taskManager.addNewEpic(epic);
        Subtask addedSubtask = taskManager.addNewSubtask(subtask);

        taskManager.deleteSubtaskById(1);
        Subtask delSubtask = taskManager.getSubtaskById(1);

        Assertions.assertNull(delSubtask, "Подзадача при удалении сохранила ID");
    }

    @Test
    void epicShouldNotStayIrrelevantSubtasks() {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask1 = new Subtask(1, "Задача-2", "Описание-2",  0);
        Subtask subtask2 = new Subtask(2, "Задача-2", "Описание-2", 0);
        Subtask updSubtask2 = new Subtask(2, "Задача-2", "Описание-2", 0);
        Subtask updSubtask3 = new Subtask(2, "Задача-2", "Описание-2", 0);
        Epic addedEpic = taskManager.addNewEpic(epic);
        Subtask addedSubtask1 = taskManager.addNewSubtask(subtask1);
        Subtask addedSubtask2 = taskManager.addNewSubtask(subtask2);

        taskManager.updateSubtask(updSubtask2);
        taskManager.updateSubtask(updSubtask3);
        List<Subtask> subtasks = taskManager.getAllEpicSabtusks(0);

        Assertions.assertEquals(2, subtasks.size(), "Внутри эпика осталась неактуальная подзадача");
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

    @Test
    void sabtuskMustHaveEpic() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = taskManager.addNewSubtask(new Subtask(1, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.now(), 0));

        Assertions.assertEquals(epic.getId(), subtask1.getEpicId());
    }

    @Test
    void tasksMustOverlap() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = new Subtask(1, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(2), LocalDateTime.now(), 0);
        Subtask subtask2 = new Subtask(2, "Подзадача-2", "Описание-2", Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(1), 0);

        taskManager.addNewSubtask(subtask1);
        Assertions.assertThrows(ManagerSaveException.class, () -> taskManager.addNewSubtask(subtask2), "Задачи должны пересечься");

        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size(), "Должна быть одна задача");
    }

    @Test
    void tasksMustNotOverlap() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = new Subtask(1, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(2), LocalDateTime.now(), 0);
        Subtask subtask2 = new Subtask(2, "Подзадача-2", "Описание-2", Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(4), 0);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Assertions.assertEquals(2, taskManager.getPrioritizedTasks().size(), "Добавиться должны обе задачи");
    }

    @Test
    void testCalculateEpicTimes() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Subtask subtask1 = taskManager.addNewSubtask(new Subtask(1, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 12, 0, 0), 0));
        Subtask subtask2 = taskManager.addNewSubtask(new Subtask(2, "Подзадача-2", "Описание-2", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 14, 0, 0), 0));
        Subtask subtask3 = taskManager.addNewSubtask(new Subtask(3, "Подзадача-2", "Описание-2", Status.DONE, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 16, 0, 0), 0));

        Assertions.assertEquals(Duration.ofHours(3), epic.getDuration(), "Продолжительность эпика должна быть равна сумме продолжительности подзадач");
        Assertions.assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Время начала эпика должно совпадать с началом подзадачи");
        Assertions.assertEquals(subtask3.getStartTime().plus(subtask3.getDuration()), epic.getEndTime(), "Время начала эпика должно совпадать с началом подзадачи");
    }

    @Test
    void updatedTaskShouldNotHaveCrossingError() {
        Epic epic = taskManager.addNewEpic(new Epic(0, "Эпик-1", "Описание-1"));
        Task task = new Task(1, "Задача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 12, 0, 0));
        Task updatedTask = new Task(1, "Задача-1 upd", "Описание-1 upd", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 12, 0, 0));
        Subtask subtask = new Subtask(2, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 11, 27, 12, 0, 0), 0);
        Subtask updatedSubtask = new Subtask(2, "Подзадача-1 upd", "Описание-1 upd", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2024, 11, 27, 12, 0, 0), 0);
        taskManager.addNewTask(task);
        taskManager.addNewSubtask(subtask);

        taskManager.updateTask(updatedTask);
        taskManager.updateSubtask(updatedSubtask);
    }
}
