package tasks;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected boolean initialize;

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this(0, name, description, status);
    }

    public Task(String name, String description) {
        this(name, description, Status.NEW);
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.initialize = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isInitialized() {
        return initialize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s",
                id, TaskType.TASK, name, description, status);
    }
}
