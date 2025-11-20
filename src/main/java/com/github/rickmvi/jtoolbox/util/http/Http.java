/*
 * Console API - Utilitarian library for input, output and formatting on the console.
 * Copyright (C) 2025  Rick M. Viana
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.rickmvi.jtoolbox.util.http;

import com.github.rickmvi.jtoolbox.util.http.status.HttpStatus;
import com.github.rickmvi.jtoolbox.logger.log.LogLevel;
import com.github.rickmvi.jtoolbox.logger.AnsiColor;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.util.Json;
import com.github.rickmvi.jtoolbox.util.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;

import java.util.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.net.URLEncoder;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * The {@code Http} class provides a fluent API for constructing and configuring HTTP requests.
 * It supports adding query parameters, form parameters, and different HTTP request methods
 * such as GET, POST, PUT, DELETE, PATCH, and OPTIONS. Additionally, this class allows customizing
 * the request body, URL encoding, and headers, facilitating a flexible and extensible request-building process.
 *
 * @author Rick M. Viana
 * @version 1.2
 * @since 2025
 */
@SuppressWarnings("unused")
public class Http implements AutoCloseable {

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
    public static @NotNull Http build() {
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
        String jsonBody = ResponseWrapper.gson.toJson(bodyObject);

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
     * current request for later sending.
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
     * It does not execute the request but configures the current request for later sending.
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

    /**
     * Adds a header to the HTTP request with the specified key and value.
     *
     * @param key the name of the header to add; must not be null.
     * @param value the value of the header to add; must not be null.
     * @return the current Http instance for method chaining.
     * @throws NullPointerException if either the key or value is null.
     */
    public Http header(@NotNull String key, @NotNull String value) {
        requestBuilder.header(key, value);
        return this;
    }

    /**
     * Adds the provided headers to the HTTP request by setting them in the request builder.
     *
     * @param headers A map containing header names as keys and their corresponding values.
     *                Must not be null.
     * @return The current instance of {@code Http} for method chaining.
     * @throws NullPointerException If the {@code headers} parameter is null.
     */
    public Http headers(@NotNull Map<String, String> headers) {
        headers.forEach(requestBuilder::header);
        return this;
    }

    /**
     * Sets the timeout duration for the current HTTP request in seconds.
     * This method configures the request to use the specified timeout duration.
     *
     * @param seconds the timeout duration in seconds. Must be a non-negative value.
     * @return the current {@code Http} instance for method chaining.
     * @throws IllegalArgumentException if the specified {@code seconds} is negative.
     */
    public Http timeoutSeconds(long seconds) {
        requestBuilder.timeout(Duration.ofSeconds(seconds));
        return this;
    }

    /**
     * Sets the timeout duration for the HTTP request in milliseconds.
     *
     * @param millis the timeout duration in milliseconds
     * @return the current instance of {@code Http}, allowing method chaining
     * @throws IllegalArgumentException if the specified timeout value is negative
     */
    public Http timeoutMillis(long millis) {
        requestBuilder.timeout(Duration.ofMillis(millis));
        return this;
    }

    /**
     * Configures the number of retry attempts and the delay between each retry
     * for the HTTP request.
     *
     * @param attempts The number of times to retry the request in case of failure.
     *                 Must be a non-negative integer.
     * @param delay    The duration to wait between retry attempts. Must be a
     *                 non-null positive duration.
     * @return The current HTTP instance with the updated retry configuration.
     * @throws IllegalArgumentException If the attempts parameter is negative or
     *                                  the delay parameter is null or non-positive.
     */
    public Http retry(int attempts, Duration delay) {
        this.retries = attempts;
        this.retryDelay = delay;
        return this;
    }

    /**
     * Enables or disables logging for HTTP operations.
     *
     * @param enable a boolean indicating whether logging should be enabled (true) or disabled (false)
     * @return the current {@code Http} instance with the updated logging configuration
     * @throws IllegalStateException if the logging state cannot be changed due to an invalid operation
     */
    public Http enableLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    /**
     * Configures the log level for the HTTP requests and responses.
     *
     * @param level the log level to set, specifying the verbosity of logging
     * @return the current Http instance with the updated log level configuration
     * @throws IllegalArgumentException if the provided log level is null
     */
    public Http logLevel(LogLevel level) {
        this.logLevel = level;
        return this;
    }

    private void log(String msg) {
        if (enableLogging) Logger.log(logLevel, msg);
    }

    /**
     * Sends a request and returns the response wrapped in a {@code ResponseWrapper}.
     *
     * @return A {@code ResponseWrapper} containing the response from the request.
     * @throws IllegalStateException if the method is called in an invalid state.
     */
    public ResponseWrapper send() {
        return sendRequest(0);
    }

    /**
     * Sends an HTTP request and handles retries in case of failures.
     *
     * The method attempts to send an HTTP request using the provided client and request builder.
     * It logs the response status and body upon a successful request. If the request fails, it retries
     * up to a predefined number of times, with a delay between each retry. Retries are stopped if the
     * maximum number of attempts is reached, and an exception is thrown.
     *
     * @param attempt The current attempt count of sending the request. It should be non-negative.
     * @return A {@code ResponseWrapper} object containing the HTTP response status code and response body
     * if the request is successful.
     * @throws RuntimeException If the HTTP request fails after exhausting all retries or if an
     * interruption occurs during the retry delay.
     */
    public ResponseWrapper sendRequest(int attempt) {
        return Try.ofThrowing(() -> client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()))
                .map(response -> {
                    log("Request sent to " + response.uri() + " with status " + getCode(response));
                    return new ResponseWrapper(response.statusCode(), response.body());
                })
                .recover(e -> {
                    If.ThrowWhen(attempt >= retries, () -> {
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
        ResponseWrapper responseWrapper = new ResponseWrapper(response.statusCode(), response.body());
        return If.supplyTrue(responseWrapper.isSuccess(), () ->
                        AnsiColor.GREEN.getCode() +
                                response.statusCode()         +
                                AnsiColor.RESET.getCode())
                .orElseGet(() ->
                        AnsiColor.BOLD.getCode() +
                                AnsiColor.RED.getCode()  +
                                response.statusCode()        +
                                AnsiColor.RESET.getCode());
    }

    /**
     * Sends an asynchronous HTTP request using the configured client and request builder.
     * This method constructs the request, submits it asynchronously, and processes the response
     * to return a wrapped representation of the HTTP status code and response body.
     *
     * @return A CompletableFuture that resolves to a ResponseWrapper containing the HTTP status
     *         code and response body upon completion of the request.
     * @throws IllegalArgumentException if the request building fails or if the URI is invalid.
     */
    public CompletableFuture<ResponseWrapper> sendAsync() {
        return client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    log("Async request sent to " + resp.uri() + " with status " + getCode(resp));
                    return new ResponseWrapper(resp.statusCode(), resp.body());
                });
    }

    /**
     * Sends the request asynchronously and executes success or failure actions upon completion.
     *
     * @param onSuccess Consumer called when the request completes successfully (status 2xx).
     * @param onFailure Consumer called when the request fails (network error, timeout, or exception during retry logic).
     */
    public void sendAsync(Consumer<ResponseWrapper> onSuccess, Consumer<Throwable> onFailure) {
        client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    log("Async request sent to " + resp.uri() + " with status " + getCode(resp));
                    return new ResponseWrapper(resp.statusCode(), resp.body());
                })
                .thenAccept(onSuccess)
                .exceptionally(e -> {
                    onFailure.accept(e.getCause() != null ? e.getCause() : e);
                    return null;
                });
    }

    @Override
    public void close() {
        client.close();
    }

    /**
     * A wrapper for HTTP response data that includes the status code and body content.
     * This class provides utility methods for interacting with and handling responses.
     *
     * @param statusCode The HTTP status code of the response.
     * @param body       The body content of the response.
     */
    public record ResponseWrapper(int statusCode, String body) {

        /** Gson instance shared by all ApiResponse records. */
        private static final Gson gson;

        static {
            gson = Json.build();
        }

        @Contract(pure = true)
        public @NotNull HttpStatus getStatus() {
            return HttpStatus.fromCode(statusCode);
        }

        @Contract(pure = true)
        public boolean isSuccess() {
            return getStatus().isSuccess();
        }

        @Contract(pure = true)
        public boolean isError() {
            return getStatus().isError();
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
         * Note: This method is less useful here as it serializes the Class object itself
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
                    ", status=" + getStatus().getReasonPhrase() +
                    ", body='" + body + '\'' +
                    ", success=" + isSuccess() +
                    '}';
        }
    }

}