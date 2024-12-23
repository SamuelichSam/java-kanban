import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TasksHttpTaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TasksHttpTaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void HttpGetAllTasksTest() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task(0, "Задача-2", "Описание-2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(1));
        Task task3 = new Task(0, "Задача-3", "Описание-3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(2));

        manager.addNewTask(task);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> respTasks = gson.fromJson(response.body(), List.class);

        assertEquals(3, respTasks.size(), "Не верное количество задач");
    }

    @Test
    public void HttpGetTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task respTask = gson.fromJson(response.body(), Task.class);
        assertEquals("Задача-1", respTask.getName(), "Задача не совпадает с искомой");
    }

    @Test
    public void HttpAddTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Задача-1", "Описание-1");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача-1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void HttpUpdateTaskTest() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        Task updTask = new Task(task.getId(), "Задача-2", "Описание-2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(updTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Задача-2", manager.getTaskById(task.getId()).getName(), "Задача не обновилась");
    }

    @Test
    public void HttpDeleteTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(0, tasksFromManager.size(), "Задача не удалилась");
    }
}

