package com.github.rickmvi.jtoolbox.http.client;

import com.github.rickmvi.jtoolbox.http.HttpProtocol;
import com.github.rickmvi.jtoolbox.json.JsonX;
import com.github.rickmvi.jtoolbox.logger.AnsiColor;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.logger.log.LogLevel;
import com.github.rickmvi.jtoolbox.util.Try;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Cliente HTTP melhorado com:
 * - Builder pattern fluente
 * - Retry automático
 * - Interceptors
 * - Logging configurável
 * - Suporte completo a REST
 */
public class ClientRequest {

    private final HttpRequest.Builder builder = HttpRequest.newBuilder();
    private final Map<String, String> queryParams = new LinkedHashMap<>();
    private HttpProtocol protocol = HttpProtocol.HTTP_2;

    private String baseUrl;
    private int retries = 0;
    private Duration retryDelay = Duration.ofSeconds(1);
    private boolean enableLogging = true;
    private LogLevel logLevel = LogLevel.INFO;
    private Consumer<HttpRequest> requestInterceptor;
    private Consumer<HttpResponse<String>> responseInterceptor;

    // ========== Configuration ==========

    public ClientRequest protocol(HttpProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public ClientRequest uri(String url) {
        this.baseUrl = url;
        builder.uri(URI.create(buildUrl(url)));
        return this;
    }

    public ClientRequest timeout(Duration duration) {
        builder.timeout(duration);
        return this;
    }

    public ClientRequest retry(int maxRetries) {
        return retry(maxRetries, Duration.ofSeconds(1));
    }

    public ClientRequest retry(int maxRetries, Duration delay) {
        this.retries = maxRetries;
        this.retryDelay = delay;
        return this;
    }

    public ClientRequest logging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    public ClientRequest logLevel(LogLevel level) {
        this.logLevel = level;
        return this;
    }

    // ========== Interceptors ==========

    public ClientRequest onRequest(Consumer<HttpRequest> interceptor) {
        this.requestInterceptor = interceptor;
        return this;
    }

    public ClientRequest onResponse(Consumer<HttpResponse<String>> interceptor) {
        this.responseInterceptor = interceptor;
        return this;
    }

    // ========== Query Parameters ==========

    public ClientRequest query(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    public ClientRequest query(String key, Object value) {
        return query(key, String.valueOf(value));
    }

    public ClientRequest queries(Map<String, String> params) {
        queryParams.putAll(params);
        return this;
    }

    private String buildUrl(String baseUrl) {
        if (queryParams.isEmpty()) return baseUrl;

        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(baseUrl.contains("?") ? "&" : "?");

        queryParams.forEach((k, v) ->
                sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                        .append("&")
        );

        if (sb.charAt(sb.length() - 1) == '&') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    // ========== Headers ==========

    public ClientRequest header(String key, String value) {
        builder.header(key, value);
        return this;
    }

    public ClientRequest headers(Map<String, String> headers) {
        headers.forEach(builder::header);
        return this;
    }

    public ClientRequest accept(String mediaType) {
        return header("Accept", mediaType);
    }

    public ClientRequest acceptJson() {
        return accept("application/json");
    }

    public ClientRequest contentType(String mediaType) {
        return header("Content-Type", mediaType);
    }

    public ClientRequest bearerAuth(String token) {
        return header("Authorization", "Bearer " + token);
    }

    public ClientRequest basicAuth(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = java.util.Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return header("Authorization", "Basic " + encoded);
    }

    // ========== HTTP Methods ==========

    public ClientRequest GET() {
        builder.GET();
        return this;
    }

    public ClientRequest POST(HttpRequest.BodyPublisher bodyPublisher) {
        builder.POST(bodyPublisher);
        return this;
    }

    public ClientRequest POST(String body) {
        return POST(HttpRequest.BodyPublishers.ofString(body));
    }

    public ClientRequest POST_JSON(Object body) {
        String json = JsonX.toJson(body);
        contentType("application/json");
        return POST(HttpRequest.BodyPublishers.ofString(json));
    }

    public ClientRequest POST_FORM(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) ->
                sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                        .append("&")
        );

        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }

        contentType("application/x-www-form-urlencoded");
        return POST(HttpRequest.BodyPublishers.ofString(sb.toString()));
    }

    public ClientRequest PUT(HttpRequest.BodyPublisher bodyPublisher) {
        builder.PUT(bodyPublisher);
        return this;
    }

    public ClientRequest PUT(String body) {
        return PUT(HttpRequest.BodyPublishers.ofString(body));
    }

    public ClientRequest PUT_JSON(Object body) {
        String json = JsonX.toJson(body);
        contentType("application/json");
        return PUT(HttpRequest.BodyPublishers.ofString(json));
    }

    public ClientRequest PATCH(String body) {
        builder.method("PATCH", HttpRequest.BodyPublishers.ofString(body));
        return this;
    }

    public ClientRequest PATCH_JSON(Object body) {
        String json = JsonX.toJson(body);
        contentType("application/json");
        return PATCH(json);
    }

    public ClientRequest DELETE() {
        builder.DELETE();
        return this;
    }

    public ClientRequest HEAD() {
        builder.method("HEAD", HttpRequest.BodyPublishers.noBody());
        return this;
    }

    public ClientRequest OPTIONS() {
        builder.method("OPTIONS", HttpRequest.BodyPublishers.noBody());
        return this;
    }

    // ========== Execution ==========

    public ClientResponse send() {
        return sendInternal(0);
    }

    private ClientResponse sendInternal(int attempt) {
        HttpClient client = HttpClient.newBuilder()
                .version(protocol.getJavaNetVersion())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        return Try.ofThrowing(() -> {
            HttpRequest request = builder.build();

            // Request interceptor
            if (requestInterceptor != null) {
                requestInterceptor.accept(request);
            }

            if (enableLogging) {
                logRequest(request);
            }

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Response interceptor
            if (responseInterceptor != null) {
                responseInterceptor.accept(response);
            }

            if (enableLogging) {
                logResponse(response);
            }

            return new ClientResponse(response.statusCode(), response.body(), response.headers());

        }).recover(e -> {
            if (attempt < retries) {
                Logger.warn("Request failed (attempt {}/{}), retrying in {}ms...",
                        attempt + 1, retries, retryDelay.toMillis());

                Try.runThrowing(() -> Thread.sleep(retryDelay.toMillis())).orThrow();
                return sendInternal(attempt + 1);
            }

            Logger.error("Request failed after {} retries: {}", retries, e.getMessage());
            throw new HttpClientException("Request failed after " + retries + " retries", e);
        }).orThrow();
    }

    public CompletableFuture<ClientResponse> sendAsync() {
        HttpClient client = HttpClient.newBuilder()
                .version(protocol.getJavaNetVersion())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = builder.build();

        if (requestInterceptor != null) {
            requestInterceptor.accept(request);
        }

        if (enableLogging) {
            logRequest(request);
        }

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (responseInterceptor != null) {
                        responseInterceptor.accept(response);
                    }

                    if (enableLogging) {
                        logResponse(response);
                    }

                    return new ClientResponse(response.statusCode(), response.body(), response.headers());
                })
                .exceptionally(e -> {
                    Logger.error("Async request failed: {}", e.getMessage());
                    throw new HttpClientException("Async request failed", e);
                });
    }

    // ========== Logging ==========

    private void logRequest(HttpRequest request) {
        Logger.log(logLevel, "→ {} {}", request.method(), request.uri());
    }

    private void logResponse(HttpResponse<?> response) {
        int status = response.statusCode();
        String color = (status >= 200 && status < 300)
                ? AnsiColor.GREEN.getCode()
                : (status >= 400 ? AnsiColor.RED.getCode() : AnsiColor.YELLOW.getCode());

        Logger.log(logLevel, "← {} {} {}{}{}",
                response.request().method(),
                response.uri(),
                color,
                status,
                AnsiColor.RESET.getCode()
        );
    }

    // ========== Exception ==========

    public static class HttpClientException extends RuntimeException {
        public HttpClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}