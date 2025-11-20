package com.github.rickmvi.jtoolbox.util.http;

import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.util.http.status.HttpStatus;
import com.github.rickmvi.jtoolbox.util.serializable.Serializer;
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
        String query = exchange.getRequestURI().getQuery();
        Map<String, List<String>> params = parseQueryParams(query);

        List<String> values = params.get(name);
        return (values == null || values.isEmpty()) ? null : values.getFirst();
    }

    public String @NotNull [] params(String name) {
        String query = exchange.getRequestURI().getQuery();
        Map<String, List<String>> params = parseQueryParams(query);

        List<String> values = params.getOrDefault(name, Collections.emptyList());
        return values.toArray(new String[0]);
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
        respond(HttpStatus.OK, "text/plain; charset=UTF-8", content);
    }

    public void json(Object data) {
        String json = Serializer.serialize(data);
        respond(HttpStatus.OK, "application/json; charset=UTF-8", json);
    }

    public void status(int statusCode, String content) {
        respond(HttpStatus.fromCode(statusCode), "text/plain; charset=UTF-8", content);
    }

    public void status(HttpStatus statusCode, String content) {
        respond(statusCode, "text/plain; charset=UTF-8", content);
    }

    public void status(HttpStatus statusCode, @NotNull HttpStatus content) {
        respond(statusCode, "text/plain; charset=UTF-8", content.getReasonPhrase());
    }

    public void status(int statusCode, @NotNull HttpStatus content) {
        respond(HttpStatus.fromCode(statusCode), "text/plain; charset=UTF-8", content.getReasonPhrase());
    }

    public void send(HttpStatus statusCode, String content) {
        String contentType = exchange.getResponseHeaders().getFirst("Content-Type") != null
                ? exchange.getResponseHeaders().getFirst("Content-Type")
                : "text/plain; charset=UTF-8";

        respond(statusCode, contentType, content);
    }

    public void redirect(String location) {
        exchange.getResponseHeaders().set("Location", location);
        respond(HttpStatus.FOUND, "text/plain; charset=UTF-8", "Redirecting to " + location);
    }

    private void respond(@NotNull HttpStatus status, String contentType, @NotNull String content) {
        try {
            byte[] responseBody = content.getBytes(StandardCharsets.UTF_8);
            Headers responseHeaders = exchange.getResponseHeaders();
            setContentTypeIfAbsent(responseHeaders, contentType);

            int contentLength = responseBody.length;
            exchange.sendResponseHeaders(status.getCode(), contentLength);

            try (OutputStream responseStream = exchange.getResponseBody()) {
                responseStream.write(responseBody);
            }
        } catch (IOException e) {
            Logger.error(
                    "Failed to respond with status code: {} and content length: {}",
                    e,
                    status,
                    content.length()
            );
        } finally {
            close();
        }
    }

    private void setContentTypeIfAbsent(@NotNull Headers responseHeaders, String contentType) {
        if (responseHeaders.getFirst("Content-Type") == null) responseHeaders.set("Content-Type", contentType);
    }

    @Override
    public void close() {
        exchange.close();
    }
}