package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
        this.status = Status.NEW;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(description, name, status);
        this.id = id;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "ID Подзадачи " + id + ". " + "ID Эпика " + epicId + ". " + "Имя " + name + ". " +
                "Описание " + description + ". " +
                "Статус " + status + "|||";
    }
}