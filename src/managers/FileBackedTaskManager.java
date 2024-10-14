package managers;

import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    private static String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d",
                    subtask.getId(), TaskType.SUBTASK, subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(), TaskType.EPIC, task.getName(), task.getStatus(), task.getDescription());
        } else {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(), TaskType.TASK, task.getName(), task.getStatus(), task.getDescription());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            for (int i = 1; i < lines.size(); i++) {
                Task task = fromString(lines.get(i));

                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subTasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
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
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

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
        save();
        return super.addNewTask(newTask);
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        save();
        return super.addNewSubtask(newSubtask);
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        save();
        return super.addNewEpic(newEpic);
    }

    @Override
    public Task updateTask(Task updatedTask) {
        save();
        return super.updateTask(updatedTask);
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        save();
        return super.updateSubtask(updatedSubtask);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        save();
        return super.updateEpic(epic);
    }

    @Override
    public Task deleteTaskById(Integer id) {
        save();
        return super.deleteTaskById(id);
    }

    @Override
    public Subtask deleteSubtaskById(Integer subtaskId) {
        save();
        return super.deleteSubtaskById(subtaskId);
    }

    @Override
    public Epic deleteEpicById(Integer epicId) {
        save();
        return super.deleteEpicById(epicId);
    }

    @Override
    public void deleteAllTasks() {
        save();
        super.deleteAllTasks();
    }

    @Override
    public void deleteAllSubtasks() {
        save();
        super.deleteAllSubtasks();
    }

    @Override
    public void deleteAllEpics() {
        save();
        super.deleteAllEpics();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
