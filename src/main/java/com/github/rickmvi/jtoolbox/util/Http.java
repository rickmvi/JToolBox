package com.github.rickmvi.jtoolbox.util;

import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.debug.AnsiColor;
import com.google.gson.Gson;
import com.github.rickmvi.jtoolbox.debug.Logger;
import com.github.rickmvi.jtoolbox.debug.log.LogLevel;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class HttpService {

    private final HttpClient client;
    private final HttpRequest.Builder requestBuilder;
    private final Map<String, String> queryParams = getMap().get();
    private final Map<String, String> formParams  = getMap().get();

    @Contract(pure = true)
    private static @NotNull Supplier<LinkedHashMap<String, String>> getMap() {
        return LinkedHashMap::new;
    }

    private int           retries = 0;
    private Duration   retryDelay = Duration.ofSeconds(1);
    private LogLevel     logLevel = LogLevel.INFO;
    private boolean enableLogging = true;

    private HttpService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.requestBuilder = HttpRequest.newBuilder();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull HttpService create() {
        return new HttpService();
    }

    public HttpService queryParam(@NotNull String key, @NotNull String value) {
        queryParams.put(key, value);
        return this;
    }

    public HttpService queryParams(@NotNull Map<String, String> params) {
        queryParams.putAll(params);
        return this;
    }

    private String buildUrl(String baseUrl) {
        if (queryParams.isEmpty()) return baseUrl;
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("?");
        queryParams.forEach((k, v) -> sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                .append("&"));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public HttpService formParam(@NotNull String key, @NotNull String value) {
        formParams.put(key, value);
        return this;
    }

    public HttpService formParams(@NotNull Map<String, String> params) {
        formParams.putAll(params);
        return this;
    }

    private @NotNull String buildFormData() {
        StringBuilder sb = new StringBuilder();
        formParams.forEach((k, v) -> sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                .append("&"));
        if (!sb.isEmpty()) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Sends an HTTP GET request to the specified URL. This method constructs the request URL,
     * including query parameters if present, sets the HTTP method to GET, and prepares the
     * request for execution. The method does not execute the request itself; it must be followed
     * by an appropriate method to send the request.
     *
     * @param url The target URL for the HTTP GET request. Must not be null.
     * @return The current instance of {@code HttpService} to enable method chaining.
     * @throws NullPointerException If the {@code url} parameter is null.
     */
    public HttpService GET(@NotNull String url) {
        requestBuilder.uri(URI.create(buildUrl(url))).GET();
        return this;
    }

    /**
     * Sends an HTTP POST request to the specified URL. This method constructs the request
     * URL and builds the appropriate HTTP request body. If form parameters are provided,
     * they will be encoded and included in the body of the request with the content type
     * set to "application/x-www-form-urlencoded". If no form parameters are present, the
     * request will be sent with an empty body.
     *
     * @param url The target URL for the HTTP POST request. Must not be null.
     * @return The current instance of {@code HttpService} to allow for method chaining.
     * @throws NullPointerException If the {@code url} parameter is null.
     */
    public HttpService POST(@NotNull String url) {
        String formData = buildFormData();
        If.isTrue(!formData.isEmpty(), () ->
                        requestBuilder
                                .uri(URI.create(buildUrl(url)))
                                .POST(HttpRequest.BodyPublishers.ofString(formData))
                                .header("Content-Type", "application/x-www-form-urlencoded"))
                .orElse(() -> requestBuilder.uri(URI.create(buildUrl(url))).POST(HttpRequest.BodyPublishers.noBody()));
        return this;
    }

    /**
     * Sends an HTTP POST request to the specified URL with the provided request body
     * and content type. The method builds the request URL, sets the request method to POST,
     * and adds the appropriate Content-Type header.
     *
     * @param url The target URL for the HTTP POST request. Must not be null.
     * @param body The request body to be included in the HTTP POST request. Must not be null.
     * @param contentType The MIME type of the request body, specified as the Content-Type header. Must not be null.
     * @return The current instance of {@code HttpService} to allow for method chaining.
     * @throws NullPointerException If {@code url}, {@code body}, or {@code contentType} is null.
     */
    public HttpService POST(@NotNull String url, @NotNull String body, @NotNull String contentType) {
        requestBuilder.uri(URI.create(buildUrl(url)))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", contentType);
        return this;
    }

    /**
     * Sends an HTTP PUT request to the specified URL with the provided request body.
     * The method constructs the full request URL, sets the HTTP method to PUT, and
     * sets the body of the request to the provided {@code body}.
     *
     * @param url The target URL for the HTTP PUT request. Must not be null.
     * @param body The request body to be included in the HTTP PUT request. Must not be null.
     * @return The current instance of {@code HttpService} to enable method chaining.
     * @throws NullPointerException If either {@code url} or {@code body} is null.
     */
    public HttpService PUT(@NotNull String url, @NotNull String body) {
        requestBuilder.uri(URI.create(buildUrl(url)))
                .PUT(HttpRequest.BodyPublishers.ofString(body));
        return this;
    }

    /**
     * Sends an HTTP DELETE request to the specified URL. This method constructs the
     * full request URL, sets the HTTP method to DELETE, and prepares the request
     * for execution. It does not execute the request itself but configures the
     * current request for subsequent sending.
     *
     * @param url The target URL for the HTTP DELETE request. Must not be null.
     * @return The current instance of {@code HttpService} to enable method chaining.
     * @throws NullPointerException If the {@code url} parameter is null.
     */
    public HttpService DELETE(@NotNull String url) {
        requestBuilder.uri(URI.create(buildUrl(url))).DELETE();
        return this;
    }

    /**
     * Sends an HTTP PATCH request to the specified URL with the provided request body.
     * This method constructs the full request URL, sets the HTTP method to PATCH,
     * and includes the specified body as the request payload.
     *
     * @param url The target URL for the HTTP PATCH request. Must not be null.
     * @param body The request body to be included in the HTTP PATCH request. Must not be null.
     * @return The current instance of {@code HttpService} to enable method chaining.
     * @throws NullPointerException If either {@code url} or {@code body} is null.
     */
    public HttpService PATCH(@NotNull String url, @NotNull String body) {
        requestBuilder.uri(URI.create(buildUrl(url)))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body));
        return this;
    }

    /**
     * Sends an HTTP OPTIONS request to the specified URL. This method constructs the
     * full request URL, sets the HTTP method to OPTIONS, and prepares the request for execution.
     * It does not execute the request but configures the current request for subsequent sending.
     *
     * @param url The target URL for the HTTP OPTIONS request. Must not be null.
     * @return The current instance of {@code HttpService} to enable method chaining.
     * @throws NullPointerException If the {@code url} parameter is null.
     */
    public HttpService OPTIONS(@NotNull String url) {
        requestBuilder.uri(URI.create(buildUrl(url)))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody());
        return this;
    }

    public HttpService header(@NotNull String key, @NotNull String value) {
        requestBuilder.header(key, value);
        return this;
    }

    public HttpService headers(@NotNull Map<String, String> headers) {
        headers.forEach(requestBuilder::header);
        return this;
    }

    public HttpService timeoutSeconds(long seconds) {
        requestBuilder.timeout(Duration.ofSeconds(seconds));
        return this;
    }

    public HttpService timeoutMillis(long millis) {
        requestBuilder.timeout(Duration.ofMillis(millis));
        return this;
    }

    public HttpService retry(int attempts, Duration delay) {
        this.retries = attempts;
        this.retryDelay = delay;
        return this;
    }

    public HttpService enableLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    public HttpService logLevel(LogLevel level) {
        this.logLevel = level;
        return this;
    }

    private void log(String msg) {
        if (enableLogging) Logger.log(logLevel, msg);
    }

    public ApiResponse send() {
        return sendRequest(0);
    }

    public ApiResponse sendRequest(int attempt) {
        return Try.ofThrowing(() -> client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()))
                .map(response -> {
                    log("Request sent to " + response.uri() + " with status " + getCode(response));
                    return new ApiResponse(response.statusCode(), response.body());
                })
                .recover(e -> {
                    If.Throws(attempt >= retries, () -> {
                        throw new RuntimeException("HTTP request failed after retries", e);
                    });
                    Try.runThrowing(() -> Thread.sleep(retryDelay.toMillis())).recover(throwable -> {
                        Thread.currentThread().interrupt();
                        return null;
                    }).orThrow();
                    return sendRequest(attempt + 1);
                })
                .orThrow();
    }

    private static String getCode(@NotNull HttpResponse<String> response) {
        ApiResponse apiResponse = new ApiResponse(response.statusCode(), response.body());
        return If.supplyTrue(apiResponse.isSuccess(), () ->
                         AnsiColor.GREEN.getCode() +
                         response.statusCode()         +
                         AnsiColor.RESET.getCode())
                .orElseGet(() ->
                        AnsiColor.BOLD.getCode() +
                        AnsiColor.RED.getCode()  +
                        response.statusCode()        +
                        AnsiColor.RESET.getCode());
    }

    public CompletableFuture<ApiResponse> sendAsync() {
        return client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    log("Async request sent to " + resp.uri() + " with status " + getCode(resp));
                    return new ApiResponse(resp.statusCode(), resp.body());
                });
    }

    public record ApiResponse(int statusCode, String body) {

        private static final Gson gson;

        static {
            gson = build();
        }

        @Contract(pure = true)
        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        @Contract(pure = true)
        public boolean isError() {
            return !isSuccess();
        }

        public <T> T asJson(Class<T> clazz) {
            return gson.fromJson(body, clazz);
        }

        public <T> @NotNull String toJson(Class<T> clazz) {
            return gson.toJson(clazz);
        }

        public void ifSuccess(Consumer<String> action) {
            if (isSuccess()) action.accept(body);
        }

        public void ifError(Consumer<Integer> action) {
            if (isError()) action.accept(statusCode);
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "ApiResponse{" +
                    "statusCode=" + statusCode +
                    ", body='" + body + '\'' +
                    ", success=" + isSuccess() +
                    '}';
        }

        @Contract(" -> new")
        private static @NotNull Gson build() {
            return new GsonBuilder()
                    .serializeNulls()
                    .create();
        }
    }

}
