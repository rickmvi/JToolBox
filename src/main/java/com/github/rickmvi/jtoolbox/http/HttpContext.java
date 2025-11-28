package com.github.rickmvi.jtoolbox.http;

import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.http.status.StatusCode;
import com.github.rickmvi.jtoolbox.serializable.Serializer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public record HttpContext(HttpExchange exchange) implements AutoCloseable {
    private static final String QUERY_PAIR_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain; charset=UTF-8";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json; charset=UTF-8";

    private static Map<String, List<String>> cachedQueryParams;

    public String protocol() {
        return exchange.getProtocol();
    }

    public Headers requestHeaders() {
        return exchange.getRequestHeaders();
    }

    public void header(String name, String value) {
        exchange.getResponseHeaders().set(name, value);
    }

    public String header(String name) {
        return exchange.getRequestHeaders().getFirst(name);
    }

    public @Nullable String param(String name) {
        List<String> values = getQueryParams().get(name);
        return (values == null || values.isEmpty()) ? null : values.getFirst();
    }

    public String @NotNull [] params(String name) {
        List<String> values = getQueryParams().getOrDefault(name, Collections.emptyList());
        return values.toArray(new String[0]);
    }

    private @NotNull Map<String, List<String>> getQueryParams() {
        if (cachedQueryParams == null) {
            String query = exchange.getRequestURI().getQuery();
            cachedQueryParams = parseQueryParams(query);
        }
        return cachedQueryParams;
    }

    private @NotNull Map<String, List<String>> parseQueryParams(@Nullable String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> parameters = new LinkedHashMap<>();
        String[] pairs = query.split(QUERY_PAIR_DELIMITER);
        for (String pair : pairs) {
            String[] parts = pair.split(QUERY_KEY_VALUE_DELIMITER, 2);
            if (parts.length != 2) {
                continue;
            }
            String key = parts[0];
            String value = parts[1];
            parameters
                    .computeIfAbsent(key, ignored -> new ArrayList<>())
                    .add(value);
        }
        return parameters;
    }

    public String path() {
        return exchange.getRequestURI().getPath();
    }

    public String body() {
        try (Scanner scan = new Scanner(exchange.getRequestBody(), StandardCharsets.UTF_8).useDelimiter("\\A")) {
            return scan.hasNext() ? scan.next() : "";
        }
    }

    public <T> T body(Class<T> clazz) {
        return Serializer.deserialize(body(), clazz);
    }

    public String method() {
        return exchange.getRequestMethod();
    }

    public void text(String content) {
        respond(StatusCode.OK, CONTENT_TYPE_TEXT_PLAIN, content);
    }

    public void json(Object data) {
        String json = Serializer.serialize(data);
        respond(StatusCode.OK, CONTENT_TYPE_APPLICATION_JSON, json);
    }

    public void status(int statusCode, String content) {
        respond(StatusCode.fromCode(statusCode), CONTENT_TYPE_TEXT_PLAIN, content);
    }

    public void status(StatusCode statusCode, String content) {
        respond(statusCode, CONTENT_TYPE_TEXT_PLAIN, content);
    }

    public void status(StatusCode statusCode, @NotNull StatusCode content) {
        respond(statusCode, CONTENT_TYPE_TEXT_PLAIN, content.reason());
    }

    public void status(int statusCode, @NotNull StatusCode content) {
        respond(StatusCode.fromCode(statusCode), CONTENT_TYPE_TEXT_PLAIN, content.reason());
    }

    public void send(StatusCode statusCode, String content) {
        String contentType = getExistingContentType();
        respond(statusCode, contentType, content);
    }

    public void redirect(String location) {
        exchange.getResponseHeaders().set("Location", location);
        respond(StatusCode.FOUND, CONTENT_TYPE_TEXT_PLAIN, "Redirecting to " + location);
    }

    private String getExistingContentType() {
        String existingContentType = exchange.getResponseHeaders().getFirst("Content-Type");
        return existingContentType != null ? existingContentType : CONTENT_TYPE_TEXT_PLAIN;
    }

    private void respond(@NotNull StatusCode status, String contentType, @NotNull String content) {
        byte[] responseBody = content.getBytes(StandardCharsets.UTF_8);
        try {
            Headers responseHeaders = exchange.getResponseHeaders();
            setContentTypeIfAbsent(responseHeaders, contentType);
            exchange.sendResponseHeaders(status.getCode(), responseBody.length);
            try (OutputStream responseStream = exchange.getResponseBody()) {
                responseStream.write(responseBody);
            }
        } catch (IOException e) {
            handleResponseException(e, status);
        } finally {
            close();
        }
    }

    private void handleResponseException(IOException e, StatusCode status) {
        String msg = e.getMessage();
        if (msg == null || (!msg.contains("Broken pipe") && !msg.contains("Connection reset"))) {
            Logger.error("Failed to respond to request at {}: {}", path(), e.getMessage());
            return;
        }
        Logger.warn("Client disconnected abruptly ({} {}).", status.getCode(), path());
    }

    private void setContentTypeIfAbsent(@NotNull Headers responseHeaders, String contentType) {
        if (responseHeaders.getFirst("Content-Type") == null) {
            responseHeaders.set("Content-Type", contentType);
        }
    }

    @Override
    public void close() {
        exchange.close();
    }
}