package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected Integer counterId = 0;

    private Integer generateNewId() {
        return counterId++;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean checkCrossing(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        return prioritizedTasks.stream()
                .anyMatch(e ->
                        !(e.getStartTime().isAfter(endTime) || e.getEndTime().isBefore(startTime))
                );
    }

    @Override
    public Task addNewTask(Task newTask) {
        if (checkCrossing(newTask)) {
            throw new ManagerSaveException("Задача пересекается по времени с уже существующими");
        }
        Integer newId = generateNewId();
        if (!newTask.isInitialized()) {
            newTask.setId(newId);
        }
        if (!newTask.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.add(newTask);
        }

        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        if (checkCrossing(updatedTask)) {
            throw new ManagerSaveException("Задача пересекается по времени с уже существующими");
        }
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
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeIf(task -> tasks.containsKey(task.getId()));
        tasks.clear();
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        Integer newId = generateNewId();
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic == null) {
            return null;
        }
        if (!newSubtask.isInitialized()) {
            newSubtask.setId(newId);
        }
        epic.addSubTask(newSubtask);

        if (!newSubtask.getStartTime().equals(LocalDateTime.MIN)) {
            if (checkCrossing(newSubtask)) {
                throw new ManagerSaveException("Подзадача пересекается по времени с уже существующими");
            }
            prioritizedTasks.add(newSubtask);
        }
        subTasks.put(newSubtask.getId(), newSubtask);
        updateEpicStatus(epic);
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (checkCrossing(updatedSubtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с уже существующими");
        }
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
    public void deleteSubtaskById(Integer subtaskId) {
        Epic epic = epics.get(subTasks.get(subtaskId).getEpicId());
        epic.deleteSubtask(getSubtaskById(subtaskId));
        subTasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeIf(subtask -> subTasks.containsKey(subtask.getId()));
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.deleteAllSubtasks();
            updateEpicStatus(epic);
        });
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        Integer newId = generateNewId();
        if (!newEpic.isInitialized()) {
            newEpic.setId(newId);
        }
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
        Epic epic = getEpicById(epicId);
        return epic.getEpicSubtasks();
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        Epic dEpic = epics.remove(epicId);
        for (Subtask subtask : dEpic.getEpicSubtasks()) {
            deleteSubtaskById(subtask.getId());
        }
        historyManager.remove(epicId);
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(epics.get(id));
        });
        subTasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        });
        epics.clear();
        subTasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void updateEpicStatus(Epic epic) {
        int counterNew = 0;
        int counterDone = 0;

        if (epic.getEpicSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        }
        List<Subtask> subtaskId = epic.getEpicSubtasks();
        for (Subtask task : subtaskId) {
            if (task.getStatus() == Status.NEW) {
                counterNew++;
            } else if (task.getStatus() == Status.DONE) {
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