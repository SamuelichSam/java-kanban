package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    Task getTaskById(Integer id);

    Subtask getSubtaskById(Integer subtaskId);

    Epic getEpicById(Integer epicId);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllEpicSabtusks(Integer epicId);

    List<Task> getPrioritizedTasks();

    Task addNewTask(Task newTask);

    Subtask addNewSubtask(Subtask newSubtask);

    Epic addNewEpic(Epic newEpic);

    Task updateTask(Task updatedTask);

    Subtask updateSubtask(Subtask updatedSubtask);

    Epic updateEpic(Epic epic);

    void deleteAllSubtasks();

    void deleteAllTasks();

    void deleteTaskById(Integer id);

    void deleteEpicById(Integer epicId);

    void deleteSubtaskById(Integer subtaskId);

    void deleteAllEpics();



}
