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
    void taskShouldNotSaveOldHistoryVersion() {
        Task task = new Task(0, "Задача-1", "Описание-1", Status.NEW);
        Task updTask = new Task(0, "Задача-1upd", "Описание-upd1", Status.IN_PROGRESS);
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        taskManager.addNewTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.updateTask(updTask);
        taskManager.getTaskById(updTask.getId());
        List<Task> history = taskManager.getHistory();
        Task inHistoryTask = history.get(0);

        Assertions.assertEquals(updTask, inHistoryTask, "В истории сохранился не последний просмотр задачи");
    }

    @Test
    void testHistoryManagerCorrectHistory() {
        Task task = new Task(0, "Задача-1", "Описание-1", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        Task inHistoryTask = history.getFirst();

        Assertions.assertEquals(task, inHistoryTask, "Задачи не совпадают");
    }

    @Test
    void historySouldNotSaveDouble() {
        Task task = new Task(0, "Задача-1", "Описание-1", Status.NEW);
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addNewTask(task);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(1, history.size(), "В истории сохранились повторные просмотры задачи");
    }
}