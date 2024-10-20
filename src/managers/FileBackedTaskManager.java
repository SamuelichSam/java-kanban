package managers;

import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,description,status,epic");
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            for (int i = 1; i < lines.size(); i++) {
                Task strTask = fromString(lines.get(i));

                if (strTask instanceof Epic) {
                    Epic epic = new Epic(strTask.getId(), strTask.getName(), strTask.getDescription());
                    epic.setStatus(strTask.getStatus());
                    manager.addNewEpic(epic);
                } else if (strTask instanceof Subtask) {
                    Subtask subtask = new Subtask(strTask.getName(), strTask.getDescription(), ((Subtask) strTask).getEpicId());
                    subtask.setId(strTask.getId());
                    subtask.setStatus(strTask.getStatus());
                    manager.addNewSubtask(subtask);
                } else {
                    Task task = new Task(strTask.getName(), strTask.getDescription(), strTask.getStatus());
                    task.setId(strTask.getId());
                    manager.addNewTask(task);
                }
            }

            for (Subtask subtask : manager.subTasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubTaskId(subtask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задачи из файла");
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        String description = fields[3];
        Status status = Status.valueOf(fields[4]);

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task task = super.addNewTask(newTask);
        save();
        return task;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        Subtask subtask = super.addNewSubtask(newSubtask);
        save();
        return subtask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        Epic epic = super.addNewEpic(newEpic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task updTask = super.updateTask(updatedTask);
        save();
        return updTask;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        Subtask updSubtask = super.updateSubtask(updatedSubtask);
        save();
        return updSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updEpic = super.updateEpic(epic);
        save();
        return updEpic;
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

}
