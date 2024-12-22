import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
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

public class HttpHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.startServer();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void HttpAddAndGetHistoryTest() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача-1", "Описание-1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Epic epic = new Epic(1, "Эпик-1", "Описание-1");
        Subtask subtask = new Subtask(0, "Задача-2", "Описание-2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusDays(1), epic.getId());

        manager.addNewTask(task);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);

        URI tasksUrl = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest tasksReq = HttpRequest.newBuilder()
                .uri(tasksUrl)
                .GET()
                .build();
        HttpResponse<String> tasksResp = client.send(tasksReq, HttpResponse.BodyHandlers.ofString());

        URI subtasksUrl = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest subtasksReq = HttpRequest.newBuilder()
                .uri(subtasksUrl)
                .GET()
                .build();
        HttpResponse<String> subtasksResp = client.send(subtasksReq, HttpResponse.BodyHandlers.ofString());

        URI epicsUrl = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest epicsReq = HttpRequest.newBuilder()
                .uri(epicsUrl)
                .GET()
                .build();
        HttpResponse<String> epicsResp = client.send(epicsReq, HttpResponse.BodyHandlers.ofString());

        URI historyUrl = URI.create("http://localhost:8080/history");
        HttpRequest historyReq = HttpRequest.newBuilder()
                .uri(historyUrl)
                .GET()
                .build();
        HttpResponse<String> historyResp = client.send(historyReq, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, historyResp.statusCode());

        List<Task> hTasks = gson.fromJson(historyResp.body(), List.class);

        assertEquals(3, hTasks.size(), "Не верное количество задач");
    }
}
