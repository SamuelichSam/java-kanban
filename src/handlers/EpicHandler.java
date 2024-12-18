package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;

public class EpicHandler extends BaseHttpHandler {

    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager);
        this.gson = gson;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
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
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] splitStrings) throws IOException, NotFoundException {
        String answer;
        if (splitStrings.length == 2) {
            try {
                answer = gson.toJson(taskManager.getAllEpics());
                sendOkAndBack(exchange, answer);
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        } else if (splitStrings.length == 3) {
            try {
                Epic epic = taskManager.getEpicById(Integer.parseInt(splitStrings[2]));
                answer = gson.toJson(epic);
                sendOkAndBack(exchange, answer);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (splitStrings.length == 4) {
            try {
                Integer id = Integer.parseInt(splitStrings[2]);
                answer = gson.toJson(taskManager.getAllEpicSabtusks(id));
                sendOkAndBack(exchange, answer);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, NotFoundException {
        InputStream inputStream = exchange.getRequestBody();
        Epic epic = gson.fromJson(inputStream.toString(), Epic.class);
        if (epic.getId() == null) {
            taskManager.addNewEpic(epic);
            sendOk(exchange, gson.toJson(epic));
        } else {
            try {
                taskManager.updateEpic(epic);
                sendOk(exchange, gson.toJson(epic));
            } catch (Exception e) {
                sendNotAcceptable(exchange);
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String[] splitStrings) throws IOException, NotFoundException {
        try {
            Integer id = Integer.parseInt(splitStrings[2]);
            taskManager.deleteEpicById(id);
            sendOkAndBack(exchange, "Задача " + taskManager.getEpicById(id) + " удалена");
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}
