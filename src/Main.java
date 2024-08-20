import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Задача-1", "Описание-1", Status.IN_PROGRESS);
        Task task2 = new Task("Задача-2", "Описание-2");
        Epic epic1 = new Epic("Эпик-1", "Описание эпика-1");
        Epic epic2 = new Epic("Эпик-2", "Описание эпика-2");
        Subtask subTask1 = new Subtask("Подзадача-1", "Описание подзадачи-1", 3);
        Subtask subTask2 = new Subtask("Подзадача-2", "Описание подзадачи-2", 3);
        Subtask subTask3 = new Subtask("Подзадача-3", "Описание подзадачи-3", 4);
        Subtask subTask4 = new Subtask("Подзадача-4", "Описание подзадачи-4", 3);

        // Проверка добавдения задач

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);
        manager.addNewSubtask(subTask3);
        manager.addNewSubtask(subTask4);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        // Проверка обнвления задач

        manager.updateTask(new Task(1, "Задача-1", "Новое описание", Status.DONE));
        manager.updateTask(new Task(2, "Задача-1", "Новое описание", Status.IN_PROGRESS));
        manager.updateEpic(new Epic(3, "Эпик-1", "Новое описание эпика-1"));
        manager.updateEpic(new Epic(4, "Эпик-2", "Новое описание эпика-2"));
        manager.updateSubtask(new Subtask(5, "Подзадача-1", "Описание подзадачи-1", Status.IN_PROGRESS, 3));
        manager.updateSubtask(new Subtask(6, "Подзадача-2", "Описание подзадачи-2", Status.IN_PROGRESS, 3));
        manager.updateSubtask(new Subtask(7, "Подзадача-3", "Описание подзадачи-3", Status.DONE, 4));
        manager.updateSubtask(new Subtask(8, "Подзадача-4", "Описание подзадачи-4", Status.DONE, 3));

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        System.out.println(manager.getEpicById(4)); // Проверка изменения статуса эпика, после измения статуса подзадачи
        System.out.println(manager.getEpicById(3)); // Проверка статуса эпика при завершении одной из задач

        // Проверка удаления задач

        manager.deleteTaskById(1);
        manager.deleteEpicById(4);
        manager.deleteSubtaskById(5);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
    }
}
