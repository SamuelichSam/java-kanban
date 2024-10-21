package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskEpicId = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void addSubTaskId(Subtask subTask) {
        subtaskEpicId.add(subTask.getId());
    }

    public List<Integer> getSubtaskEpicId() {
        return new ArrayList<>(subtaskEpicId);
    }

    public void deleteSubtuskById(Integer id) {
        subtaskEpicId.remove(id);
    }

    public void deleteAllSubtasksId() {
        subtaskEpicId.clear();
    }

    @Override
    public String toString() {
        return "ID Эпика " + id + ". " + "ID Подзадачи" + subtaskEpicId + ". " + "Имя " + name + ". " +
                "Описание " + description + ". " +
                "Статус " + status + "|||";


    }
}
