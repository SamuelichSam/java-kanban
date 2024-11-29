import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Epic epic = new Epic(1, "Эпик", "Описание");
        Task task = new Task(0, "Задача", "Описание", Status.DONE, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 16, 0, 0));
        Subtask subTask1 = new Subtask(2, "Подзадача-1", "Описание-1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 12, 0, 0), 1);
        Subtask subTask2 = new Subtask(3, "Подзадача-2", "Описание-2", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2024, 11, 26, 14, 0, 0), 1);

        manager.addNewTask(task);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        System.out.println(manager.getEpicById(1));

        manager.getEpicById(1);
        manager.getTaskById(0);
        manager.getSubtaskById(2);
        manager.getSubtaskById(3);

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Task task : manager.getAllEpicSabtusks(1)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
