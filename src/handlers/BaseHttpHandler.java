package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange h, String text, int rCode) throws IOException {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(rCode, resp.length);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
    }

    protected void sendOkAndBack(HttpExchange h, String text) throws IOException {
        sendText(h, text, 200);
        h.getResponseBody().close();
    }

    protected void sendOk(HttpExchange h, String text) throws IOException {
            sendText(h, text, 201);
            h.getResponseBody().close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendText(h, "Not Found", 404);
        h.getResponseBody().close();
    }

    protected void sendNotAcceptable(HttpExchange h) throws IOException {
        sendText(h, "Not Acceptable", 406);
        h.getResponseBody().close();
    }

    protected void sendInternalServerError(HttpExchange h) throws IOException {
        sendText(h, "Internal Server Error", 500);
        h.getResponseBody().close();
    }

    protected void sendMethodNotAllowed(HttpExchange h) throws IOException {
        sendText(h, "Method Not Allowed", 405);
        h.getResponseBody().close();
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        sendText(h, "Bad Request", 400);
        h.getResponseBody().close();
    }
}
