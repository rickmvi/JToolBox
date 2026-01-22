package com.github.rickmvi.jtoolbox.http.client;

import com.github.rickmvi.jtoolbox.http.status.StatusCode;
import com.github.rickmvi.jtoolbox.json.JsonX;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Response do cliente HTTP com métodos utilitários melhorados
 */
public class ClientResponse {

    private static final Gson gson = JsonX.create().getGson();

    private final int statusCode;
    private final String body;
    private final HttpHeaders headers;

    public ClientResponse(int statusCode, String body, HttpHeaders headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public ClientResponse(int statusCode, String body) {
        this(statusCode, body, null);
    }

    // ========== Status ==========

    public int statusCode() {
        return statusCode;
    }

    public @NotNull StatusCode status() {
        return StatusCode.fromCode(statusCode);
    }

    public boolean isSuccess() {
        return status().isSuccess();
    }

    public boolean isError() {
        return status().isError();
    }

    public boolean isClientError() {
        return status().isClientError();
    }

    public boolean isServerError() {
        return status().isServerError();
    }

    public boolean isRedirection() {
        return status().isRedirection();
    }

    public boolean is(int code) {
        return statusCode == code;
    }

    public boolean is(StatusCode status) {
        return statusCode == status.getCode();
    }

    // ========== Body ==========

    public String body() {
        return body;
    }

    public Optional<String> bodyOpt() {
        return body != null && !body.isEmpty() ? Optional.of(body) : Optional.empty();
    }

    public <T> T asJson(Class<T> clazz) {
        if (body == null || body.isEmpty()) {
            throw new IllegalStateException("Response body is empty");
        }
        return gson.fromJson(body, clazz);
    }

    public <T> Optional<T> asJsonOpt(Class<T> clazz) {
        try {
            return Optional.of(asJson(clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // ========== Headers ==========

    public HttpHeaders headers() {
        return headers;
    }

    public Optional<String> header(String name) {
        return headers != null ? headers.firstValue(name) : Optional.empty();
    }

    public List<String> headers(String name) {
        return headers != null ? headers.allValues(name) : List.of();
    }

    public String contentType() {
        return header("Content-Type").orElse(null);
    }

    public long contentLength() {
        return header("Content-Length")
                .map(Long::parseLong)
                .orElse(-1L);
    }

    // ========== Functional Methods ==========

    public ClientResponse ifSuccess(Consumer<ClientResponse> action) {
        if (isSuccess()) {
            action.accept(this);
        }
        return this;
    }

    public ClientResponse ifError(Consumer<ClientResponse> action) {
        if (isError()) {
            action.accept(this);
        }
        return this;
    }

    public ClientResponse ifStatus(int code, Consumer<ClientResponse> action) {
        if (statusCode == code) {
            action.accept(this);
        }
        return this;
    }

    public ClientResponse ifStatus(StatusCode status, Consumer<ClientResponse> action) {
        return ifStatus(status.getCode(), action);
    }

    public ClientResponse ifStatusMatches(Predicate<Integer> predicate, Consumer<ClientResponse> action) {
        if (predicate.test(statusCode)) {
            action.accept(this);
        }
        return this;
    }

    public <T> T map(Function<ClientResponse, T> mapper) {
        return mapper.apply(this);
    }

    public <T> Optional<T> mapOpt(Function<ClientResponse, T> mapper) {
        try {
            return Optional.ofNullable(mapper.apply(this));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // ========== Validation ==========

    public ClientResponse ensureSuccess() {
        if (!isSuccess()) {
            throw new HttpResponseException(
                    "Expected success status but got " + statusCode + ": " + status().reason(),
                    this
            );
        }
        return this;
    }

    public ClientResponse ensureStatus(int expectedCode) {
        if (statusCode != expectedCode) {
            throw new HttpResponseException(
                    "Expected status " + expectedCode + " but got " + statusCode,
                    this
            );
        }
        return this;
    }

    public ClientResponse ensureStatus(StatusCode expectedStatus) {
        return ensureStatus(expectedStatus.getCode());
    }

    // ========== Exception ==========

    public static class HttpResponseException extends RuntimeException {
        private final ClientResponse response;

        public HttpResponseException(String message, ClientResponse response) {
            super(message);
            this.response = response;
        }

        public ClientResponse getResponse() {
            return response;
        }

        public int getStatusCode() {
            return response.statusCode();
        }
    }

    // ========== Object Methods ==========

    @Override
    public @NotNull String toString() {
        return "Response[" + statusCode + " " + status().reason() + ", body=" +
                (body != null ? body.substring(0, Math.min(50, body.length())) : "null") + "]";
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP Response:\n");
        sb.append("  Status: ").append(statusCode).append(" ").append(status().reason()).append("\n");

        if (headers != null) {
            sb.append("  Headers:\n");
            headers.map().forEach((name, values) ->
                    sb.append("    ").append(name).append(": ").append(values).append("\n")
            );
        }

        sb.append("  Body: ").append(body != null ? body : "(empty)").append("\n");
        return sb.toString();
    }
}