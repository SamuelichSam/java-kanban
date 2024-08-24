package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> hystory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (hystory.size() >= 10) {
            hystory.removeFirst();
        }
        hystory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return hystory;
    }
}
