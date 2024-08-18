import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int counterId = 1;

    private int generateNewId() {
        return counterId++;
    }

    public Task addNewTask(Task newTask) {
        int newId = generateNewId();
        newTask.setId(newId);
        tasks.put(newTask.getId(), newTask);
        return newTask;

    }

    public Task updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
            return updatedTask;
        } else {
            return null;
        }
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task deleteTaskById(Integer id) {
        Task task = tasks.get(id);
        tasks.remove(id);
        return task;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Subtask addNewSubtask(Subtask newSubtask) {
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic != null) {
            int newId = generateNewId();
            newSubtask.setId(newId);
            epic.addSubTaskId(newSubtask);
            subTasks.put(newSubtask.getId(), newSubtask);
            updateEpicStatus(epic);
        }
        return newSubtask;
    }

    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (subTasks.containsKey(updatedSubtask.getId())) {
            Subtask subtask = subTasks.get(updatedSubtask.getId());
            subTasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = epics.get(updatedSubtask.getEpicId());
            updateEpicStatus(epic);
        }
        return updatedSubtask;
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subTasks.get(subtaskId);
    }


    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Subtask deleteSubtaskById(int subtaskId) {

            Epic epic = epics.get(subTasks.get(subtaskId).getEpicId());
            epic.deleteSubtuskById(subtaskId);
            Subtask subtask = subTasks.get(subtaskId);
            subTasks.remove(subtaskId);
            updateEpicStatus(epic);
            return subtask;
    }

    public void deleteAllSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasksId();
            updateEpicStatus(epic);
        }
    }

    public Epic addNewEpic(Epic newEpic) {
        int newId = generateNewId();
        newEpic.setId(newId);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic updatedEpic = epics.get(epic.getId());
            updatedEpic.setName(epic.getName());
            updatedEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    public void updateEpicStatus(Epic epic) {
        int counterNew = 0;
        int counterDone = 0;

        if (epic.getSubtaskEpicId().isEmpty()) {
            epic.setStatus(Status.NEW);
        }
        ArrayList<Integer> subtaskId = epic.getSubtaskEpicId();
        for (Integer id : subtaskId) {
            if (subTasks.get(id).getStatus() == Status.NEW) {
                counterNew++;
            } else if (subTasks.get(id).getStatus() == Status.DONE) {
                counterDone++;
            }
        }
        if (subtaskId.size() == counterNew) {
            epic.setStatus(Status.NEW);
        } else if (subtaskId.size() == counterDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllEpicSabtusks(Epic epic) {
        ArrayList<Subtask> gSubtusks = new ArrayList<>();
        Epic gepic = epics.get(epic.getId());
        for (Integer subtaskId : gepic.getSubtaskEpicId()) {
            gSubtusks.add(subTasks.get(subtaskId));
        }
        return gSubtusks;
    }

    public Epic deleteEpicById(int epicId) {
        Epic dEpic = epics.get(epicId);
        ArrayList<Integer> dSubtusk = dEpic.getSubtaskEpicId();
        for (Integer subtaskId : dSubtusk) {
            subTasks.remove(subtaskId);
        }
        epics.remove(epicId);
        return dEpic;
    }

    public void deleteAllEpics() {
        epics.clear();
    }


}