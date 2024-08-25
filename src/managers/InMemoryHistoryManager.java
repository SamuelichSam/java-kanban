package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> hystory = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        hystory.add(task);
        if (hystory.size() >= 10) {
            hystory.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(hystory);
    }
}
