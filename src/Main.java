import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task = new Task("Задача", "Описание", Status.NEW);
        Epic epic = new Epic("Эпик", "Описание");
        Subtask subTask1 = new Subtask("Подзадача-1", "Описание-1", 1);
        Subtask subTask2 = new Subtask("Подзадача-2", "Описание-2", 1);

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
