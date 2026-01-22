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

/**
 * Context wrapper para HttpExchange com funcionalidades aprimoradas.
 * Thread-safe e com suporte a path parameters, cookies, e validações.
 */
public class HttpContext implements AutoCloseable {
    private static final String QUERY_PAIR_DELIMITER          = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER     = "=";
    private static final String CONTENT_TYPE_TEXT_PLAIN       = "text/plain; charset=UTF-8";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json; charset=UTF-8";
    private static final String CONTENT_TYPE_HTML             = "text/html; charset=UTF-8";

    private final HttpExchange exchange;

    // Cache por instância (thread-safe)
    private Map<String, List<String>> queryParamsCache;
    private Map<String, String>       pathParamsCache;
    private String                    bodyCache;
    private Map<String, Object>       attributes;
    private boolean                   responseSent = false;

    public HttpContext(HttpExchange exchange) {
        this.exchange   = exchange;
        this.attributes = new HashMap<>();
    }

    // ========== Request Info ==========

    public String protocol() {
        return exchange.getProtocol();
    }

    public String method() {
        return exchange.getRequestMethod();
    }

    public String path() {
        return exchange.getRequestURI().getPath();
    }

    public String url() {
        return exchange.getRequestURI().toString();
    }

    public String host() {
        return header("Host");
    }

    public String userAgent() {
        return header("User-Agent");
    }

    public String contentType() {
        return header("Content-Type");
    }

    // ========== Headers ==========

    public Headers requestHeaders() {
        return exchange.getRequestHeaders();
    }

    public Headers responseHeaders() {
        return exchange.getResponseHeaders();
    }

    public void header(String name, String value) {
        exchange.getResponseHeaders().set(name, value);
    }

    public String header(String name) {
        return exchange.getRequestHeaders().getFirst(name);
    }

    public List<String> headers(String name) {
        return exchange.getRequestHeaders().get(name);
    }

    public boolean hasHeader(String name) {
        return exchange.getRequestHeaders().containsKey(name);
    }

    // ========== Query Parameters ==========

    public @Nullable String query(String name) {
        List<String> values = getQueryParams().get(name);
        return (values == null || values.isEmpty()) ? null : values.getFirst();
    }

    public String query(String name, String defaultValue) {
        String value = query(name);
        return value != null ? value : defaultValue;
    }

    public String @NotNull [] queryArray(String name) {
        List<String> values = getQueryParams().getOrDefault(name, Collections.emptyList());
        return values.toArray(new String[0]);
    }

    public Map<String, List<String>> queryParams() {
        return Collections.unmodifiableMap(getQueryParams());
    }

    private @NotNull Map<String, List<String>> getQueryParams() {
        if (queryParamsCache == null) {
            String query = exchange.getRequestURI().getQuery();
            queryParamsCache = parseQueryParams(query);
        }
        return queryParamsCache;
    }

    private @NotNull Map<String, List<String>> parseQueryParams(@Nullable String query) {
        if (query == null || query.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<String, List<String>> parameters = new LinkedHashMap<>();
        String[] pairs = query.split(QUERY_PAIR_DELIMITER);

        for (String pair : pairs) {
            String[] parts = pair.split(QUERY_KEY_VALUE_DELIMITER, 2);
            if (parts.length != 2) continue;

            String key = urlDecode(parts[0]);
            String value = urlDecode(parts[1]);

            parameters.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        return parameters;
    }

    // ========== Path Parameters (será preenchido pelo Router) ==========

    public void setPathParams(Map<String, String> params) {
        this.pathParamsCache = params;
    }

    public @Nullable String pathParam(String name) {
        return pathParamsCache != null ? pathParamsCache.get(name) : null;
    }

    public String pathParam(String name, String defaultValue) {
        String value = pathParam(name);
        return value != null ? value : defaultValue;
    }

    public Map<String, String> pathParams() {
        return pathParamsCache != null
                ? Collections.unmodifiableMap(pathParamsCache)
                : Collections.emptyMap();
    }

    // ========== Request Body ==========

    public String body() {
        if (bodyCache == null) {
            try (Scanner scan = new Scanner(exchange.getRequestBody(), StandardCharsets.UTF_8)
                    .useDelimiter("\\A")) {
                bodyCache = scan.hasNext() ? scan.next() : "";
            }
        }
        return bodyCache;
    }

    public <T> T body(Class<T> clazz) {
        return Serializer.deserialize(body(), clazz);
    }

    public Optional<String> bodyOpt() {
        String b = body();
        return b.isEmpty() ? Optional.empty() : Optional.of(b);
    }

    // ========== Cookies ==========

    public @Nullable String cookie(String name) {
        String cookieHeader = header("Cookie");
        if (cookieHeader == null) return null;

        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2 && parts[0].equals(name)) {
                return parts[1];
            }
        }
        return null;
    }

    public void setCookie(String name, String value) {
        setCookie(name, value, null, null, null, false, false);
    }

    public void setCookie(String name, String value, Integer maxAge, String path,
                          String domain, boolean httpOnly, boolean secure) {
        StringBuilder cookie = new StringBuilder(name).append("=").append(value);

        if (maxAge != null) cookie.append("; Max-Age=").append(maxAge);
        if (path != null) cookie.append("; Path=").append(path);
        if (domain != null) cookie.append("; Domain=").append(domain);
        if (httpOnly) cookie.append("; HttpOnly");
        if (secure) cookie.append("; Secure");

        exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }

    // ========== Attributes (para compartilhar dados entre middlewares) ==========

    public void set(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) attributes.get(key);
    }

    public <T> T get(String key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }

    // ========== Response Methods ==========

    public void text(String content) {
        respond(StatusCode.OK, CONTENT_TYPE_TEXT_PLAIN, content);
    }

    public void text(String ... lines) {
        respond(StatusCode.OK, CONTENT_TYPE_TEXT_PLAIN, String.join("\n", lines));
    }

    public void html(String content) {
        respond(StatusCode.OK, CONTENT_TYPE_HTML, content);
    }

    public void json(Object data) {
        String json = Serializer.serialize(data);
        respond(StatusCode.OK, CONTENT_TYPE_APPLICATION_JSON, json);
    }

    public void status(int statusCode) {
        status(StatusCode.fromCode(statusCode));
    }

    public void status(StatusCode statusCode) {
        respond(statusCode, CONTENT_TYPE_TEXT_PLAIN, statusCode.reason());
    }

    public void status(int statusCode, String content) {
        respond(StatusCode.fromCode(statusCode), CONTENT_TYPE_TEXT_PLAIN, content);
    }

    public void status(StatusCode statusCode, String content) {
        respond(statusCode, CONTENT_TYPE_TEXT_PLAIN, content);
    }

    public void send(StatusCode statusCode, String content) {
        String contentType = getExistingContentType();
        respond(statusCode, contentType, content);
    }

    public void redirect(String location) {
        redirect(location, StatusCode.FOUND);
    }

    public void redirect(String location, StatusCode statusCode) {
        if (!statusCode.isRedirection()) {
            throw new IllegalArgumentException("Status code must be 3xx for redirect");
        }
        exchange.getResponseHeaders().set("Location", location);
        respond(statusCode, CONTENT_TYPE_TEXT_PLAIN, "Redirecting to " + location);
    }

    public void noContent() {
        respond(StatusCode.NO_CONTENT, CONTENT_TYPE_TEXT_PLAIN, "");
    }

    // ========== CORS Support ==========

    public void cors(String origin) {
        header("Access-Control-Allow-Origin", origin);
        header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        header("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    public void corsAll() {
        cors("*");
    }

    // ========== Internal Methods ==========

    private String getExistingContentType() {
        String existingContentType = exchange.getResponseHeaders().getFirst("Content-Type");
        return existingContentType != null ? existingContentType : CONTENT_TYPE_TEXT_PLAIN;
    }

    private void respond(@NotNull StatusCode status, String contentType, @NotNull String content) {
        if (responseSent) {
            Logger.warn("Response already sent for {}", path());
            return;
        }

        byte[] responseBody = content.getBytes(StandardCharsets.UTF_8);

        try {
            Headers responseHeaders = exchange.getResponseHeaders();
            setContentTypeIfAbsent(responseHeaders, contentType);
            exchange.sendResponseHeaders(status.getCode(), responseBody.length);

            try (OutputStream responseStream = exchange.getResponseBody()) {
                responseStream.write(responseBody);
            }

            responseSent = true;
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

    private String urlDecode(String value) {
        try {
            return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }

    @Override
    public void close() {
        exchange.close();
    }
}