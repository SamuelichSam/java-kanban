import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
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
        Task task = new Task(0, "Задача-1", "Описание-1");
        Task updTask = new Task(0, "Задача-1upd", "Описание-upd1");
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
        Task task = new Task(0, "Задача-1", "Описание-1");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        Task inHistoryTask = history.getFirst();

        Assertions.assertEquals(task, inHistoryTask, "Задачи не совпадают");
    }

    @Test
    void historyShouldNotContainsDeletedTasks() {
        Task task = new Task(0, "Задача-1", "Описание-1");
        Epic epic = new Epic(1, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(2, "Задача-2", "Описание-2", 1);
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);

        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.deleteTaskById(0);
        taskManager.deleteSubtaskById(2);
        taskManager.deleteEpicById(1);

        Assertions.assertEquals(0, taskManager.getHistory().size(), "В истории сохранились удалённые задачи");
    }

    @Test
    void historySouldNotSaveDouble() {
        Task task = new Task(0, "Задача-1", "Описание-1");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addNewTask(task);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(1, history.size(), "В истории сохранились повторные просмотры задачи");
    }

    @Test
    void emptyHistory() {
        Task task = new Task(0, "Задача-1", "Описание-1");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        taskManager.addNewTask(task);

        Assertions.assertEquals(historyManager.getHistory().size(), 0);
    }

    @Test
    void removeFromStartOfHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(0, "Задача-1", "Описание-1");
        Task task2 = new Task(1, "Задача-2", "Описание-2");
        Task task3 = new Task(2, "Задача-3", "Описание-3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(0);
        List<Task> tasks = historyManager.getTasks();

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(task2, tasks.get(0));
    }

    @Test
    void removeFromMiddleOfHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(0, "Задача-1", "Описание-1");
        Task task2 = new Task(1, "Задача-2", "Описание-2");
        Task task3 = new Task(2, "Задача-3", "Описание-3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        List<Task> tasks = historyManager.getTasks();

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(task1, tasks.get(0));
        Assertions.assertEquals(task3, tasks.get(1));
    }

    @Test
    void removeFromEndOfHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(0, "Задача-1", "Описание-1");
        Task task2 = new Task(1, "Задача-2", "Описание-2");
        Task task3 = new Task(2, "Задача-3", "Описание-3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);
        List<Task> tasks = historyManager.getTasks();

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(task1, tasks.get(0));
        Assertions.assertEquals(task2, tasks.get(1));
    }
}