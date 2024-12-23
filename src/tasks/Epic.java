package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Subtask> epicSubtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(Integer id, String name, String description, Status status, Duration duration, LocalDateTime startTime,
                LocalDateTime endTime) {
        super(id, name, description, status, duration, startTime);
        this.endTime = endTime;
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubTask(Subtask subTask) {
        epicSubtasks.add(subTask);
    }

    public List<Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void deleteSubtask(Subtask subtask) {
        epicSubtasks.remove(subtask);
    }

    public void deleteAllSubtasks() {
        epicSubtasks.clear();
    }

    @Override
    public Duration getDuration() {
        Duration duration = Duration.ZERO;
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getDuration() != null) {
                duration = duration.plus(subtask.getDuration());
            }
        }
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime startTime = null;
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStartTime() != null && startTime == null) {
                startTime = subtask.getStartTime();
            } else if (subtask.getStartTime() != null && subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
        }
        return startTime;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime = null;
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getEndTime() != null && endTime == null) {
                endTime = subtask.getEndTime();
            } else if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                id, TaskType.EPIC, name, description, status, duration, startTime, endTime);
    }
}
