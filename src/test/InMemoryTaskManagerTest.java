package test;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

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
    void TaskEqualsById() {
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
        Subtask subtask = new Subtask("Задача-1", "Описание-1", 1);
        Subtask expectedSubtask = new Subtask(0, "Задача-1", "Описание-1", Status.NEW, 1);

        Task addedTask = taskManager.addNewTask(subtask);

        Assertions.assertNotNull(addedTask, "Задача не сохранилась");
        Assertions.assertNotNull(addedTask.getId(), "Не сгенерировался id");
        Assertions.assertEquals(expectedSubtask, addedTask, "Задачи не совпадают");

    }

    @Test
    void subTaskEqualsById() {
        Epic epic = new Epic("Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(0, "Задача-2", "Описание-2", Status.NEW, 0);
        Subtask expectedSubtask = new Subtask(1, "Задача-2", "Описание-2", Status.IN_PROGRESS, 0);

        Epic addedEpic = taskManager.addNewEpic(epic);
        Subtask addedSubtask = taskManager.addNewSubtask(subtask);
        System.out.println(addedEpic);

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
        Task task = new Task(4, "Задача-1", "Описание-1", Status.NEW);
        Task expectedTask = new Task(4, "Задача-1", "Описание-1", Status.NEW);

        Task addedTask = taskManager.addNewTask(task);

        Assertions.assertNotEquals(expectedTask.getId(), addedTask.getId(), "Конфликт id");
    }
}