import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManagerTest {
    private File file;

    @BeforeEach
    public void inIt() throws IOException {
        file = File.createTempFile("Тест", ".csv");
    }

    @Test
    public void managerShouldSaveEmptyFile() {
        FileBackedTaskManager saveManager = new FileBackedTaskManager(file);
        saveManager.saveForTests();

        Assertions.assertTrue(saveManager.getAllTasks().isEmpty(), "Фаил должен быть пустым");
        Assertions.assertTrue(saveManager.getAllEpics().isEmpty(), "Фаил должен быть пустым");
        Assertions.assertTrue(saveManager.getAllSubtasks().isEmpty(), "Фаил должен быть пустым");
    }

    @Test
    public void managerShouldLoadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(loadedManager.getAllTasks().isEmpty(), "Фаил должен быть пустым");
        Assertions.assertTrue(loadedManager.getAllEpics().isEmpty(), "Фаил должен быть пустым");
        Assertions.assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Фаил должен быть пустым");
    }

    @Test
    public void managerShouldSaveToFile() {
        FileBackedTaskManager saveManager = new FileBackedTaskManager(file);
        Task task = new Task("Задача-1", "Описание-1");
        Epic epic = new Epic("Эпик-1", "Описание-1");
        Subtask subtask = new Subtask("Подзадача-1", "Описание-1", 1);
        Subtask subtask2 = new Subtask("Подзадача-2", "Описание-2", 1);
        saveManager.addNewTask(task);
        saveManager.addNewEpic(epic);
        saveManager.addNewSubtask(subtask);
        saveManager.addNewSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(task, loadedManager.getTaskById(0), "Задача не записалась");
        Assertions.assertEquals(epic, loadedManager.getEpicById(1), "Эпик не записался");
        Assertions.assertEquals(subtask, loadedManager.getSubtaskById(2), "Подзадача-1 не записалась");
        Assertions.assertEquals(subtask2, loadedManager.getSubtaskById(3), "Подзадача-2 не записалась");
    }

    @Test
    public void managerShouldLoadFromFileSavedTask() throws IOException {
        Task task = new Task(0, "Задача-1", "Описание-1", Status.NEW);
        Epic epic = new Epic(1, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(2, "Подзадача-1", "Описание-1", Status.NEW, 1);
        List<String> lines = new ArrayList<>();
        lines.add("id,type,name,status,description,epic");
        lines.add(task.toString());
        lines.add(epic.toString());
        lines.add(subtask.toString());
        Files.write(file.toPath(), lines);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(task, loadedManager.getTaskById(0), "Задача не прочиталась из файла");
        Assertions.assertEquals(epic, loadedManager.getEpicById(1), "Задача не прочиталась из файла");
        Assertions.assertEquals(subtask, loadedManager.getSubtaskById(2), "Задача не прочиталась из файла");
    }
}
