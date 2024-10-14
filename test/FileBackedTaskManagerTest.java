import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File file;

    @BeforeEach
    public void inIt() throws IOException {
        file = File.createTempFile("Тест", ".csv");
    }

    @Test
    public void managerSaveAndLoadEmptyFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(loadedManager.getAllTasks().isEmpty(), "Фаил должен быть пустым");
        Assertions.assertTrue(loadedManager.getAllEpics().isEmpty(), "Фаил должен быть пустым");
        Assertions.assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Фаил должен быть пустым");
    }

    @Test
    public void managerShouldSaveAndLoadToFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Задача-1", "Описание-1");
        Epic epic = new Epic("Эпик-1", "Описание-1");
        Subtask subtask = new Subtask("Подзадача-1", "Описание-1", 1);
        Subtask subtask2 = new Subtask("Подзадача-2", "Описание-2", 1);
        manager.addNewTask(task);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);
        manager.addNewSubtask(subtask2);
        manager.save();

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(task, load.getTaskById(0), "Задача не записалась");
        Assertions.assertEquals(epic, load.getEpicById(1), "Эпик не записался");
        Assertions.assertEquals(subtask, load.getSubtaskById(2), "Подзадача-1 не записалась");
        Assertions.assertEquals(subtask2, load.getSubtaskById(3), "Подзадача-2 не записалась");
    }
}
