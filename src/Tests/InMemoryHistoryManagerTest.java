package Tests;

import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

class InMemoryHistoryManagerTest {

    @Test
    public void testHistoryManagerCorrectHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(0, "Задача-1", "Описание-1", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        Task inHistoryTask = history.getFirst();

        Assertions.assertEquals(task, inHistoryTask, "Задачи не совпадают");
    }

}