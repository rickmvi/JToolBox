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

/**
 * The {@code Http} class provides a fluent API for constructing and configuring HTTP requests.
 * It supports adding query parameters, form parameters, and different HTTP request methods
 * such as GET, POST, PUT, DELETE, PATCH, and OPTIONS. Additionally, this class allows customizing
 * the request body, URL encoding, and headers, facilitating a flexible and extensible request-building process.
 */
@SuppressWarnings("unused")
public class Http {

    /** The underlying HttpClient is static and shared across all instances for efficiency. */
    private static final HttpClient client;

    static {
        client = HttpClient.newBuilder().
                version(HttpClient.Version.HTTP_2).
                build();
    }

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

    private Http() {
        this.requestBuilder = HttpRequest.newBuilder();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Http create() {
        return new Http();
    }

    /**
     * Adds a query parameter to the HTTP request. This method allows you to specify
     * a key-value pair to be included as part of the query string in the URL. If the
     * provided key already exists in the query parameters, its value will be replaced
     * with the provided value.
     *
     * @param key The key of the query parameter. Must not be null.
     * @param value The value of the query parameter. Must not be null.
     * @return The current instance of {@code Http} to allow for method chaining.
     * @throws NullPointerException If either {@code key} or {@code value} is null.
     */
    public Http queryParam(@NotNull String key, @NotNull String value) {
        queryParams.put(key, value);
        return this;
    }

    /**
     * Adds multiple query parameters to the HTTP request. This method allows you to
     * specify a map containing key-value pairs representing query parameters to be
     * included as part of the query string in the URL. If any of the provided keys
     * already exist in the query parameters, their values will be replaced with the
     * provided values.
     *
     * @param params A map of query parameters, where each key is the name of a query
     * parameter and each value is the corresponding parameter value.
     * Must not be null.
     * @return The current instance of {@code Http} to allow for method chaining.
     * @throws NullPointerException If the {@code params} map is null.
     */
    public Http queryParams(@NotNull Map<String, String> params) {
        queryParams.putAll(params);
        return this;
    }

    /**
     * Builds a complete URL by appending query parameters to the given base URL.
     * The query parameters are sourced from the internal state of the class
     * and encoded to ensure they are properly formatted for use in a URL.
     * If there are no query parameters, the original base URL is returned.
     *
     * @param baseUrl The base URL to which query parameters should be appended. Must not be null.
     * @return A string representing the complete URL with query parameters encoded and appended.
     * @throws NullPointerException If {@code baseUrl} is null.
     */
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

    /**
     * Adds a single form parameter to the HTTP request. This method enables
     * specifying a key-value pair to be included in the request body as
     * form data. If the provided key already exists in the form parameters,
     * its value will be replaced with the provided value.
     *
     * @param key The key of the form parameter. Must not be null.
     * @param value The value of the form parameter. Must not be null.
     * @return The current instance of {@code Http} to allow for method chaining.
     * @throws NullPointerException If either {@code key} or {@code value} is null.
     */
    public Http formParam(@NotNull String key, @NotNull String value) {
        formParams.put(key, value);
        return this;
    }

    /**
     * Adds multiple form parameters to the HTTP request. This method allows you to
     * specify a map containing key-value pairs that represent the form parameters to
     * be included in the request body. The provided parameters will be merged with
     * any existing form parameters in the current request. If a key in the provided
     * map already exists in the form parameters, its value will be replaced.
     *
     * @param params A map of form parameters where each key represents the name of
     * a form parameter and each value represents the corresponding
     * parameter value. Must not be null.
     * @return The current instance of {@code Http} to allow for method chaining.
     * @throws NullPointerException If the {@code params} map is null.
     */
    public Http formParams(@NotNull Map<String, String> params) {
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
    public Http GET(@NotNull String url) {
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
    public Http POST(@NotNull String url) {
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
    public Http POST(@NotNull String url, @NotNull String body, @NotNull String contentType) {
        requestBuilder.uri(URI.create(buildUrl(url)))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", contentType);
        return this;
    }

    /**
     * Sends an HTTP POST request, automatically serializing the provided Java object
     * into a JSON body and setting the Content-Type header to "application/json".
     *
     * @param url The target URL for the HTTP POST request. Must not be null.
     * @param bodyObject The Java object to be serialized into the JSON request body. Must not be null.
     * @return The current instance of {@code Http} to allow for method chaining.
     * @throws NullPointerException If {@code url} or {@code bodyObject} is null.
     */
    public Http POST_JSON(@NotNull String url, @NotNull Object bodyObject) {
        // Usa a mesma Gson configuration do ApiResponse
        String jsonBody = ApiResponse.gson.toJson(bodyObject);

        requestBuilder.uri(URI.create(buildUrl(url)))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json");

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
    public Http PUT(@NotNull String url, @NotNull String body) {
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
    public Http DELETE(@NotNull String url) {
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
    public Http PATCH(@NotNull String url, @NotNull String body) {
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
    public Http OPTIONS(@NotNull String url) {
        requestBuilder.uri(URI.create(buildUrl(url)))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody());
        return this;
    }

    /**
     * Sets the Authorization header using the Basic scheme (username:password Base64 encoded).
     *
     * @param username The username for Basic Authentication.
     * @param password The password for Basic Authentication.
     * @return The current instance of {@code Http} to allow for method chaining.
     */
    public Http basicAuth(@NotNull String username, @NotNull String password) {
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        requestBuilder.header("Authorization", "Basic " + encodedAuth);
        return this;
    }

    /**
     * Sets the Authorization header using the Bearer token scheme.
     *
     * @param token The OAuth 2.0 Bearer token.
     * @return The current instance of {@code Http} to allow for method chaining.
     */
    public Http bearerToken(@NotNull String token) {
        requestBuilder.header("Authorization", "Bearer " + token);
        return this;
    }

    /**
     * Adds the Accept header with the value "application/json", indicating the client
     * expects a JSON response.
     *
     * @return The current instance of {@code Http} to allow for method chaining.
     */
    public Http acceptJson() {
        requestBuilder.header("Accept", "application/json");
        return this;
    }

    public Http header(@NotNull String key, @NotNull String value) {
        requestBuilder.header(key, value);
        return this;
    }

    public Http headers(@NotNull Map<String, String> headers) {
        headers.forEach(requestBuilder::header);
        return this;
    }

    public Http timeoutSeconds(long seconds) {
        requestBuilder.timeout(Duration.ofSeconds(seconds));
        return this;
    }

    public Http timeoutMillis(long millis) {
        requestBuilder.timeout(Duration.ofMillis(millis));
        return this;
    }

    public Http retry(int attempts, Duration delay) {
        this.retries = attempts;
        this.retryDelay = delay;
        return this;
    }

    public Http enableLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    public Http logLevel(LogLevel level) {
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

    /**
     * Sends the request asynchronously and executes success or failure actions upon completion.
     *
     * @param onSuccess Consumer called when the request completes successfully (status 2xx).
     * @param onFailure Consumer called when the request fails (network error, timeout, or exception during retry logic).
     */
    public void sendAsync(Consumer<ApiResponse> onSuccess, Consumer<Throwable> onFailure) {
        client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    log("Async request sent to " + resp.uri() + " with status " + getCode(resp));
                    return new ApiResponse(resp.statusCode(), resp.body());
                })
                .thenAccept(onSuccess)
                .exceptionally(e -> {
                    onFailure.accept(e.getCause() != null ? e.getCause() : e);
                    return null;
                });
    }

    public record ApiResponse(int statusCode, String body) {

        /** Gson instance shared by all ApiResponse records. */
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

        /**
         * Deserializes the response body (expected to be JSON) into the specified class type.
         *
         * @param clazz The class type to deserialize the JSON body into.
         * @return An instance of the specified class.
         */
        public <T> T asJson(Class<T> clazz) {
            return gson.fromJson(body, clazz);
        }

        /**
         * Serializes the specified class type (usually used with generics) back to a JSON string.
         * Note: This method is less useful here as it serializes the Class object itself,
         * but is kept for completeness as it was in the original code.
         *
         * @param clazz The class to serialize (not the object content).
         * @return A JSON representation of the class definition.
         */
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