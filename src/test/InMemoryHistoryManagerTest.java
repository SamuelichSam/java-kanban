package test;

import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void inIt() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void historyManagerShouldSavePreVersionOfTask() {

        Task task = new Task(0, "Задача-1", "Описание-1", Status.NEW);
        Task updTask = new Task(0, "Задача-1upd", "Описание-upd1", Status.IN_PROGRESS);

        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addNewTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.addNewTask(updTask);
        taskManager.getTaskById(updTask.getId());
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(2, history.size(), "История не содержит двух версий задачи");
        Assertions.assertEquals(task, history.get(0), "Предыдущая версия задачи не найдена");
        Assertions.assertEquals(updTask, history.get(1), "Обновленная версия задачи не найдена");
    }

    @Test
    void testHistoryManagerCorrectHistory() {
        Task task = new Task(0, "Задача-1", "Описание-1", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        Task inHistoryTask = history.getFirst();

        Assertions.assertEquals(task, inHistoryTask, "Задачи не совпадают");
    }
}