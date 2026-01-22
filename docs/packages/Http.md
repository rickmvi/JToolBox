# HTTP Module Overview

The JToolbox HTTP module provides a robust foundation for building HTTP-based applications, both client- and server-side. It abstracts common operations, simplifies routing, request and response handling, and provides utilities for status codes, methods, and protocols.

Key features:
- Flexible, extensible HTTP client utilities
- Embedded HTTP server with routing and request context
- Typed handling of methods, status codes and protocols
- Customizable handlers and adapters
- Support for streaming-friendly handlers and multiple protocols

## New Implementations

### ClientRequest
Represents an HTTP request initiated by a client. Allows configuration of method, headers, body, and supports synchronous or asynchronous execution.

**Example:**
```java
ClientRequest request = new ClientRequest("https://api.example.com");
request.setMethod(Method.GET);
ClientResponse response = request.execute();
```

### ClientResponse
Encapsulates the response returned by a client request. Provides access to status, body, headers and helpers for handling returned data.

**Example:**
```java
String body = response.getBody();
int status = response.getStatusCode();
```

### HttpContext
Manages the context of a server-side request, including the incoming request, response helpers, session data and utilities for manipulating the HTTP flow.

**Example:**
```java
public void handle(HttpContext ctx) {
    String param = ctx.getQueryParam("id");
    ctx.send("Received ID: " + param);
}
```

### HttpProtocol
Enumerates supported HTTP protocols (HTTP/1.0, HTTP/1.1, HTTP/2), simplifying configuration and connection validation.

### Method
Enumerates supported HTTP methods (GET, POST, PUT, DELETE, etc.), providing type-safety and validation for operations.

### Route
Defines server routes by associating paths and methods to specific handlers.

**Example:**
```java
server.route(Method.GET, "/status", ctx -> ctx.send("OK"));
```

### Server
Implements the HTTP server, managing routes, contexts, lifecycle and startup/shutdown operations.

**Example:**
```java
Server server = new Server(8080);
server.route(Method.GET, "/", ctx -> ctx.send("Welcome!"));
server.start();
```

### StatusCode
Enumerates and documents HTTP status codes to make responses explicit and easy to validate.

### Handler
Functional interface for handling requests, used in routes and middleware.

**Example:**
```java
Handler handler = ctx -> ctx.send("Hello World");
```

## Recent Improvements
- Added `ClientRequest` and `ClientResponse` for client-side HTTP operations
- Improved `HttpContext` flexibility and helper methods
- Expanded `Method`, `StatusCode` and `HttpProtocol` enumerations
- Server now supports dynamic routing and multiple handler styles
- Functional and pluggable handler APIs, plus adapter utilities

## Complete Example
```java
Server server = new Server(8080);
server.route(Method.GET, "/hello", ctx -> ctx.send("Hello HTTP!"));
server.start();
```

# HTTP package (server + client) overview

This document explains how to use the JToolbox HTTP components: the embedded server, handler models, and the client utilities. It focuses on the streaming-friendly server API (ProtocolHandler) introduced to make the server customizable and efficient for streaming responses.

## Key concepts

- Server: Embedded HTTP server built on the JDK `com.sun.net.httpserver.HttpServer`. Create and register routes using fluent methods (e.g. `Server.port(8080).get(...)`).

- Handler (legacy): `com.github.rickmvi.jtoolbox.http.handler.Handler` — the original functional interface that receives an `HttpContext` and is used by existing routes. The legacy API remains supported.

- ProtocolHandler (streaming-friendly): `com.github.rickmvi.jtoolbox.http.handler.ProtocolHandler` — the new streaming handler signature:

  ```java
  void handle(Request request, Response response) throws Exception;
  ```

  Use it when you want to stream data out gradually (for example, server-sent events, logs, or chunked responses).

- Request: server-side read-only view of the incoming request (method, URI, path, query params, headers, input stream).

- Response: streaming response writer (status, headers, and `OutputStream` via `bodyStream()`); implements `AutoCloseable` so handlers can use try-with-resources and ensure the underlying exchange is closed reliably.

- HttpContext: convenience wrapper around the `HttpExchange` that provides helpers such as `body()`, `json(...)`, `text(...)` and existing convenience response methods. It is still available and used by the legacy handler style.

- Adapters: `HandlerAdapters.fromProtocol(...)` adapts a `ProtocolHandler` into the legacy `Handler` so new handlers can be used without changing server internals.


## Quickstart (streaming ProtocolHandler)

Below is a minimal streaming server example. A `ProtocolHandler` receives a `Request` and a `Response` writer; the response body is obtained by `response.bodyStream()` and can be written to in a streaming fashion.

```java
import com.github.rickmvi.jtoolbox.http.server.Server;

public class SimpleProtocolServer {
    public static void main(String[] args) throws Exception {
        Server server = Server.port(8080);

        server.get("/stream", (req, resp) -> {
            resp.header("Content-Type", "text/plain; charset=UTF-8");
            try (var out = resp.bodyStream()) {
                for (int i = 1; i <= 5; i++) {
                    String line = "message " + i + "\n";
                    out.write(line.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    out.flush();
                    Thread.sleep(200);
                }
            }
        });

        server.get("/ping", (req, resp) -> {
            resp.header("Content-Type", "text/plain; charset=UTF-8");
            try (var out = resp.bodyStream()) {
                out.write("pong\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                out.flush();
            }
        });

        server.start();

        // keep main thread alive
        Thread.currentThread().join();
    }
}
```

Notes:
- `resp.bodyStream()` will trigger sending response headers with the currently-set status code. If you need to change the status code, call `resp.status(code)` before calling `bodyStream()`.
- `Response` implements `AutoCloseable`. Use try-with-resources (or close the writer) to ensure the exchange and streams are closed.


## Using the client helper (`Http`)

The existing `com.github.rickmvi.jtoolbox.http.Http` utility remains available. It builds and sends HTTP requests using `java.net.http.HttpClient`.

Example (legacy client usage):

```java
var resp = Http.build()
    .GET("http://localhost:8080/ping")
    .send();

System.out.println("status=" + resp.statusCode());
System.out.println(resp.body());
```

The project plan includes creating protocol-client types (Client/Request/Response) in the future so the client side can also be pluggable and adapter-backed.


## Running the example locally

Build the project classes and run the example main class.

```bash
# compile classes
./gradlew classes

# run example (use compiled classes)
java -cp build/classes/java/main com.github.rickmvi.jtoolbox.http.examples.SimpleProtocolServer
```

Open another terminal and test with `curl`:

```bash
# simple ping
curl -v http://localhost:8080/ping

# streaming endpoint (keep the connection open)
curl -N http://localhost:8080/stream
```


## Migration notes & compatibility

- Backward compatibility: existing `Handler` and `HttpContext` APIs are kept. `ProtocolHandler` is an additional option that is adapted into the legacy `Handler` at registration time, so you can adopt it incrementally.

- If you prefer to keep using `HttpContext` conveniences (e.g. `json()`, `body()`), they remain available in existing handlers. For new streaming-driven use cases, prefer `Request`/`Response` for clearer streaming semantics.

- Future work (planned):
  - Add server-side convenience helpers on `Response` such as `sendText(String)` and `sendJson(Object)` that do not require manual stream management.
  - Add protocol client types (`Client`, `Request`, `Response`) and adapters so `Http` can return the new `Response` interface and accept pluggable clients (e.g. OkHttp adapter).


## Troubleshooting

- If responses hang: ensure you call `resp.bodyStream()` (or close the response) so the server sends headers. When writing streaming responses, call `flush()` after writes to push data.

- If clients report broken pipe or connection reset: it's usually the client closing the connection early. Server logs will capture common cases and will not rethrow on normal client disconnects.


## Where to look in the codebase

- `com.github.rickmvi.jtoolbox.http.server.Server` — server builder and route registration.
- `com.github.rickmvi.jtoolbox.http.handler.ProtocolHandler` — streaming-friendly handler interface.
- `com.github.rickmvi.jtoolbox.http.server.adapter.ExchangeRequestAdapter` — maps `HttpExchange` -> `Request`.
- `com.github.rickmvi.jtoolbox.http.server.adapter.ExchangeResponseWriter` — implements `Response` and writes to `HttpExchange`.
- `com.github.rickmvi.jtoolbox.http.server.adapter.HandlerAdapters` — adapter from `ProtocolHandler` to legacy `Handler`.
- `com.github.rickmvi.jtoolbox.http.Http` — the existing client helper.

