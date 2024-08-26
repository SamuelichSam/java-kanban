package test;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    public void Managers() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Assertions.assertNotNull(taskManager, "Объект не инициализирован");
        Assertions.assertNotNull(historyManager, "Объект не инициализирован");

    }
}