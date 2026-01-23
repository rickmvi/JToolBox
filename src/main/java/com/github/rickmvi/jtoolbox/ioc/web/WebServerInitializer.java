package com.github.rickmvi.jtoolbox.ioc.web;

import com.github.rickmvi.jtoolbox.http.server.Server;
import com.github.rickmvi.jtoolbox.ioc.annotations.web.EnableWebServer;
import com.github.rickmvi.jtoolbox.ioc.context.ApplicationContext;
import com.github.rickmvi.jtoolbox.logger.Logger;
import lombok.Getter;

/**
 * Initializes and configures a web server for the application.
 * This class handles the lifecycle of the web server, including its creation,
 * configuration, and startup. It integrates with the provided application context
 * and configuration data to set up the server with optional features like CORS.
 */
public class WebServerInitializer {

    private final ApplicationContext context;
    private final EnableWebServer    config;

    @Getter
    private Server                   server;

    public WebServerInitializer(ApplicationContext context, EnableWebServer config) {
        this.context = context;
        this.config = config;
    }

    public void initialize() {
        Logger.debug("[WEB] Creating web server bean");

        server = createServer();

        configureMvc();

        startServer();
    }

    private Server createServer() {
        Server server = Server.port(config.port())
                .host(config.host());

        if (config.enableCors()) {
            server.enableCors(config.corsOrigin());
        }

        // Futuramente: registrar o servidor como bean no contexto
        // context.registerSingleton("webServer", server);

        return server;
    }

    private void configureMvc() {
        Logger.debug("[WEB] Configuring MVC");

        WebMvcConfigurer configurer = new WebMvcConfigurer(context, server);
        configurer.configure();
    }

    private void startServer() {
        Logger.debug("[WEB] Starting server");

        server.start();

        // Registra hook para parar o servidor quando o contexto fechar
        // Futuramente isso seria automático via lifecycle do bean
    }

}