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
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

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

public class TasksHttpSubtaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TasksHttpSubtaskTest() throws IOException {
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
    public void HttpGetAllSubtasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(),0);
        Subtask subtask2 = new Subtask(0, "Задача-2", "Описание-2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(1), 0);
        Subtask subtask3 = new Subtask(0, "Задача-3", "Описание-3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(2), 0);

        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> respSubtasks = gson.fromJson(response.body(), List.class);

        assertEquals(3, respSubtasks.size(), "Не верное количество задач");
    }

    @Test
    public void HttpGetSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(),0);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask respSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals("Задача-1", respSubtask.getName(), "Задача не совпадает с искомой");
    }

    @Test
    public void HttpAddSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        manager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(),0);
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача-1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void HttpUpdateTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(),0);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);

        Subtask updSubtask = new Subtask(subtask.getId(), "Задача-2", "Описание-2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        String taskJson = gson.toJson(updSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Задача-2", manager.getSubtaskById(subtask.getId()).getName(), "Задача не обновилась");
    }

    @Test
    public void HttpDeleteTaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(),0);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();
        assertEquals(0, tasksFromManager.size(), "Задача не удалилась");
    }
}

