package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager);
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGet(exchange);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            sendOkAndBack(exchange, gson.toJson(taskManager.getHistory()));
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }
}
