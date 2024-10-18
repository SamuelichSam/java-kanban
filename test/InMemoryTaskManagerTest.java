import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void inIt() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTaskShouldSaveTask() {
        Task task = new Task("Задача-1", "Описание-1");
        Task expectedTask = new Task(0, "Задача-1", "Описание-1", Status.NEW);

        Task savedTask = taskManager.addNewTask(task);

        Assertions.assertNotNull(savedTask, "Задача не сохранилась");
        Assertions.assertNotNull(savedTask.getId(), "Не сгенерировался id");
        Assertions.assertEquals(expectedTask, savedTask, "Задачи не совпадают");
    }

    @Test
    void taskEqualsById() {
        Task task = new Task("Задача-1", "Описание-1");
        Task anotherTask = new Task(0, "Задача-2", "Описание-2", Status.IN_PROGRESS);

        Task addedTask = taskManager.addNewTask(task);

        Assertions.assertEquals(addedTask, anotherTask, "Задачи не совпадают");
    }

    @Test
    void updateTaskShouldUpdateTaskById() {
        Task task = new Task("Задача-1", "Описание-1");
        Task addedTask = taskManager.addNewTask(task);
        Task updatedTask = new Task(addedTask.getId(), "Задача-1 upd", "Описание-1 upd", Status.NEW);
        Task expectedUpdatedTask =
                new Task(addedTask.getId(), "Задача-1 upd", "Описание-1 upd", Status.NEW);

        Task actualUpdatedTask = taskManager.updateTask(updatedTask);

        Assertions.assertEquals(expectedUpdatedTask, actualUpdatedTask, "После обновления задачи не совпадают");
    }

    @Test
    void addNewSubtaskShouldSaveTask() {
        Epic epic = new Epic("Эпик-1", "Описание-1");
        Subtask subtask = new Subtask("Задача-1", "Описание-1", 0);
        Subtask expectedSubtask = new Subtask(1, "Задача-1", "Описание-1", Status.NEW, 0);
        Epic addedEpic = taskManager.addNewEpic(epic);

        Task addedSubtask = taskManager.addNewSubtask(subtask);

        Assertions.assertNotNull(addedSubtask, "Задача не сохранилась");
        Assertions.assertNotNull(addedSubtask.getId(), "Не сгенерировался id");
        Assertions.assertEquals(expectedSubtask, addedSubtask, "Задачи не совпадают");
    }

    @Test
    void subtaskShouldNotAddedIfEpicIsNull() {
        Epic epic = null;
        Subtask subtask = new Subtask("Задача-1", "Описание-1", 0);

        Task addedSubtask = taskManager.addNewSubtask(subtask);

        Assertions.assertNull(addedSubtask, "Подзадача не может быть создана без Эпика");
    }

    @Test
    void subTaskEqualsById() {
        Epic epic = new Epic("Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(0, "Задача-2", "Описание-2", Status.NEW, 0);
        Subtask expectedSubtask = new Subtask(0, "Задача-2", "Описание-2", Status.IN_PROGRESS, 0);
        Epic addedEpic = taskManager.addNewEpic(epic);

        Subtask addedSubtask = taskManager.addNewSubtask(subtask);

        Assertions.assertEquals(addedSubtask.getId(), expectedSubtask.getId(), "Задачи не совпадают");
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
        Task task = new Task("Задача-1", "Описание-1", Status.NEW);
        Task anotherTask = new Task(0, "Задача-2", "Описание-2", Status.NEW);

        Task addedTask = taskManager.addNewTask(task);
        Task addedAnotherTask = taskManager.addNewTask(anotherTask);

        Assertions.assertNotEquals(addedTask.getId(), addedAnotherTask.getId(), "Конфликт id");
    }

    @Test
    void deletedSubtasksShouldNotSaveOldId() {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(1, "Задача-2", "Описание-2", Status.NEW, 0);
        Epic addedEpic = taskManager.addNewEpic(epic);
        Subtask addedSubtask = taskManager.addNewSubtask(subtask);

        taskManager.deleteSubtaskById(1);
        Subtask delSubtask = taskManager.getSubtaskById(1);

        Assertions.assertNull(delSubtask, "Подзадача при удалении сохранила ID");
    }

    @Test
    void epicShouldNotStayIrrelevantSubtasks() {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask1 = new Subtask(1, "Задача-2", "Описание-2", Status.NEW, 0);
        Subtask subtask2 = new Subtask(2, "Задача-2", "Описание-2", Status.NEW, 0);
        Subtask updSubtask2 = new Subtask(2, "Задача-2", "Описание-2", Status.DONE, 0);
        Epic addedEpic = taskManager.addNewEpic(epic);
        Subtask addedSubtask1 = taskManager.addNewSubtask(subtask1);
        Subtask addedSubtask2 = taskManager.addNewSubtask(subtask2);

        taskManager.updateSubtask(updSubtask2);
        List<Subtask> subtasks = taskManager.getAllEpicSabtusks(0);

        Assertions.assertEquals(1, subtasks.size(), "Внутри эпика осталась неактуальная подзадача");
    }
}