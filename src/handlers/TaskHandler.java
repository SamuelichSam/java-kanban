package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import managers.TaskManager;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {

    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager);
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] splitStrings = path.split("/");

            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange, splitStrings);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, splitStrings);
                    break;
                default:
                    sendMethodNotAllowed(exchange);
                    break;
            }

        }

    private void handleGet(HttpExchange exchange, String[] splitStrings) throws IOException, NotFoundException {
        String answer;
        if (splitStrings.length == 2) {
            try {
                answer = gson.toJson(taskManager.getAllTasks());
                sendOkAndBack(exchange, answer);
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        } else if (splitStrings.length == 3) {
            try {
                Task task = taskManager.getTaskById(Integer.parseInt(splitStrings[2]));
                answer = gson.toJson(task);
                sendOkAndBack(exchange, answer);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, NotFoundException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        if (task.getId() == null || taskManager.getTaskById(task.getId()) == null) {
            taskManager.addNewTask(task);
            sendOk(exchange, "Задача добавлена");
        } else {
            try {
                taskManager.updateTask(task);
                sendOk(exchange, "Задача обновлена");
            } catch (Exception e) {
                sendNotAcceptable(exchange);
            }
        }
    }


    private void handleDelete(HttpExchange exchange, String[] splitStrings) throws IOException, NotFoundException {
        try {
            Integer id = Integer.parseInt(splitStrings[2]);
            taskManager.deleteTaskById(id);
            sendOkAndBack(exchange, "Задача удалена");
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}



