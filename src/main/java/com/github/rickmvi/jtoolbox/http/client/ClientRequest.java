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

public class ClientRequest {

    private final HttpRequest.Builder builder = HttpRequest.newBuilder();
    private final Map<String, String> queryParams = new LinkedHashMap<>();
    private HttpProtocol protocol = HttpProtocol.HTTP_2;

    private int retries = 0;
    private Duration retryDelay = Duration.ofSeconds(1);
    private boolean enableLogging = true;
    private final LogLevel logLevel = LogLevel.INFO;

    public ClientRequest protocol(HttpProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public ClientRequest uri(String baseUrl) {
        builder.uri(URI.create(buildUrl(baseUrl)));
        return this;
    }

    public ClientRequest query(String key, String value) {
        queryParams.put(key, value);
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
        if (sb.charAt(sb.length() - 1) == '&') sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public ClientRequest GET() {
        builder.GET();
        return this;
    }

    public ClientRequest POST(HttpRequest.BodyPublisher bodyPublisher) {
        builder.POST(bodyPublisher);
        return this;
    }

    public ClientRequest POST_JSON(Object body) {
        String json = JsonX.toJson(body);
        builder.header("Content-Type", "application/json");
        return POST(HttpRequest.BodyPublishers.ofString(json));
    }

    public ClientRequest POST_FORM(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                .append("&"));
        if (!sb.isEmpty()) sb.deleteCharAt(sb.length() - 1);

        builder.header("Content-Type", "application/x-www-form-urlencoded");
        return POST(HttpRequest.BodyPublishers.ofString(sb.toString()));
    }

    public ClientRequest PUT(HttpRequest.BodyPublisher bodyPublisher) {
        builder.PUT(bodyPublisher);
        return this;
    }

    public ClientRequest DELETE() {
        builder.DELETE();
        return this;
    }

    public ClientRequest header(String key, String value) {
        builder.header(key, value);
        return this;
    }

    public ClientRequest timeout(Duration duration) {
        builder.timeout(duration);
        return this;
    }

    public ClientRequest retry(int maxRetries, Duration delay) {
        this.retries = maxRetries;
        this.retryDelay = delay;
        return this;
    }

    public ClientRequest verbose(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    public ClientResponse send() {
        return sendInternal(0);
    }

    private ClientResponse sendInternal(int attempt) {
        HttpClient client = HttpClient.newBuilder()
                .version(protocol.getJavaNetVersion())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        return Try.ofThrowing(() -> {
            HttpResponse<String> resp = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            logResponse(resp);
            return new ClientResponse(resp.statusCode(), resp.body());
        }).recover(e -> {
            if (attempt < retries) {
                Try.runThrowing(() -> Thread.sleep(retryDelay.toMillis())).orThrow();
                return sendInternal(attempt + 1);
            }
            throw new RuntimeException("Request failed after " + retries + " retries", e);
        }).orThrow();
    }

    public CompletableFuture<ClientResponse> sendAsync() {
        HttpClient client = HttpClient.newBuilder()
                .version(protocol.getJavaNetVersion())
                .build();

        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    logResponse(resp);
                    return new ClientResponse(resp.statusCode(), resp.body());
                });
    }

    private void logResponse(HttpResponse<?> resp) {
        if (!enableLogging) return;
        String color = (resp.statusCode() >= 200 && resp.statusCode() < 300) ? AnsiColor.GREEN.getCode() : AnsiColor.RED.getCode();
        Logger.log(logLevel, "REQ [" + resp.request().method() + "] " + resp.uri() + " -> " + color + resp.statusCode() + AnsiColor.RESET.getCode());
    }

}
