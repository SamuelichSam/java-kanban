package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node<Task>> history = new HashMap<>();

    public class Node<T> {
        public T data;
        public Node<T> prev;
        public Node<T> next;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    private Node<Task> head;
    private Node<Task> tail;

    public Node<Task> linkLast(Task element) {
        Node<Task> oldTail = tail;
        tail = new Node<>(oldTail, element, null);

        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = tail;
        }
        return tail;
    }

    public List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node<Task> node = head;

        while (node != null) {
            tasksList.add(node.data);
            node = node.next;
        }
        return tasksList;
    }

    public void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        if (node == head) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
        } else if (node == tail) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            } else {
                head = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        history.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        Node<Task> node = history.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }
}