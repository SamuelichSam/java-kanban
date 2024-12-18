package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer id, String name, String description, Status status, Duration duration, LocalDateTime startTime, Integer epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Integer epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d",
                id, TaskType.SUBTASK, name, description, status, duration, startTime, epicId);
    }
}