package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Subtask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer counterId = 0;

    private Integer generateNewId() {
        return counterId++;
    }

    @Override
    public Task addNewTask(Task newTask) {
        Integer newId = generateNewId();
        newTask.setId(newId);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
            return updatedTask;
        } else {
            return null;
        }
    }

    @Override
    public Task getTaskById(Integer id) {
        Task getedTask = tasks.get(id);
        if (getedTask == null) {
            return null;
        }
        historyManager.add(getedTask);
        return tasks.get(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task deleteTaskById(Integer id) {
        Task task = tasks.get(id);
        tasks.remove(id);
        historyManager.remove(id);
        return task;
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : getAllTasks()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic == null) {
            return null;
        }
        int newId = generateNewId();
        newSubtask.setId(newId);
        epic.addSubTaskId(newSubtask);
        subTasks.put(newSubtask.getId(), newSubtask);
        updateEpicStatus(epic);
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (subTasks.containsKey(updatedSubtask.getId())) {
            subTasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = epics.get(updatedSubtask.getEpicId());
            updateEpicStatus(epic);
        }
        if (updatedSubtask.getStatus() == Status.DONE) {
            deleteSubtaskById(updatedSubtask.getId());
        }
        return updatedSubtask;
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        Subtask foundSubtask = subTasks.get(subtaskId);
        if (foundSubtask == null) {
            return null;
        }
        historyManager.add(subTasks.get(subtaskId));
        return subTasks.get(subtaskId);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Subtask deleteSubtaskById(Integer subtaskId) {
        Epic epic = epics.get(subTasks.get(subtaskId).getEpicId());
        epic.deleteSubtuskById(subtaskId);
        Subtask subtask = subTasks.get(subtaskId);
        subTasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            for (Integer id : epic.getSubtaskEpicId()) {
                historyManager.remove(id);
            }
            epic.deleteAllSubtasksId();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        Integer newId = generateNewId();
        newEpic.setId(newId);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic updatedEpic = epics.get(epic.getId());
            updatedEpic.setName(epic.getName());
            updatedEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        Epic getedEpic = epics.get(epicId);
        if (getedEpic == null) {
            return null;
        }
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllEpicSabtusks(Integer epicId) {
        List<Subtask> gSubtusks = new ArrayList<>();
        Epic gepic = epics.get(epicId);
        for (Integer subtaskId : gepic.getSubtaskEpicId()) {
            gSubtusks.add(subTasks.get(subtaskId));
        }
        return gSubtusks;
    }

    @Override
    public Epic deleteEpicById(Integer epicId) {
        Epic dEpic = epics.get(epicId);
        List<Integer> dSubtusk = dEpic.getSubtaskEpicId();
        for (Integer subtaskId : dSubtusk) {
            subTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
        return dEpic;
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : getAllEpics()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : getAllSubtasks()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void updateEpicStatus(Epic epic) {
        int counterNew = 0;
        int counterDone = 0;

        if (epic.getSubtaskEpicId().isEmpty()) {
            epic.setStatus(Status.NEW);
        }
        List<Integer> subtaskId = epic.getSubtaskEpicId();
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
}