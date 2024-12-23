import managers.FileBackedTaskManager;
import exceptions.ManagerSaveException;
import managers.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;
    private FileBackedTaskManager manager;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("Тест", ".csv");
            if (file == null) {
                throw new IllegalStateException("Файл не создан");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка создания файла", e);
        }
        return Managers.getFileBackedTaskManager(file);
    }


    @Test
    public void managerShouldSaveEmptyFile() {
        FileBackedTaskManager saveManager = new FileBackedTaskManager(file);

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
    public void managerShouldLoadFromFileSavedTask() throws IOException {
        Task task = new Task(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Epic epic = new Epic(1, "Эпик-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4));
        Subtask subtask = new Subtask(2, "Подзадача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(10), 1);
        List<String> lines = new ArrayList<>();
        lines.add("id,type,name,description,status,epic");
        lines.add(task.toString());
        lines.add(epic.toString());
        lines.add(subtask.toString());
        Files.write(file.toPath(), lines);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        loadedManager.addNewTask(new Task(10, "Задача-10", "Описание-10",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(6)));
        loadedManager.addNewEpic(new Epic(15, "Эпик-15", "Описание-15"));
        loadedManager.addNewSubtask(new Subtask(20, "Подзадача-20", "Описание-20", 1));
        loadedManager.addNewTask(new Task(0, "Задача-30", "Описание-30",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(1)));

        Assertions.assertEquals(task, loadedManager.getTaskById(0), "Задача не прочиталась из файла");
        Assertions.assertEquals(epic, loadedManager.getEpicById(1), "Задача не прочиталась из файла");
        Assertions.assertEquals(subtask, loadedManager.getSubtaskById(2),
                "Задача не прочиталась из файла");
        Assertions.assertEquals(task.getName(), loadedManager.getTaskById(0).getName(),
                "Название задачи не совпадает");
        Assertions.assertEquals(epic.getName(), loadedManager.getEpicById(1).getName(),
                "Название задачи не совпадает");
        Assertions.assertEquals(subtask.getName(), loadedManager.getSubtaskById(2).getName(),
               "Название задачи не совпадает");
    }

    @Test
    public void testFileSaveException() {
        Task task = new Task(0, "Задача-1", "Описание-1");

        Assertions.assertThrows(ManagerSaveException.class, () -> {
            file = Paths.get("File", ".txt").toFile();
            manager = new FileBackedTaskManager(file);
            manager.addNewTask(task);
        });
    }

    @Test
    public void testFileLoadingException() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            file = Paths.get("File", ".txt").toFile();
            manager = FileBackedTaskManager.loadFromFile(file);
        });
    }
}
