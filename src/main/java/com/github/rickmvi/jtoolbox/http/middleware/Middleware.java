package com.github.rickmvi.jtoolbox.http.middleware;

import com.github.rickmvi.jtoolbox.http.HttpContext;
import com.github.rickmvi.jtoolbox.util.Try;

/**
 * Interface para middlewares que podem interceptar e processar requests.
 * Middlewares podem:
 * - Modificar o contexto antes do handler
 * - Validar e rejeitar requests
 * - Adicionar headers, logging, etc.
 * - Chamar next() para continuar a cadeia ou não chamar para interromper
 */
@FunctionalInterface
public interface Middleware {

    /**
     * Processa o request. Deve chamar next.run() para continuar a cadeia.
     *
     * @param ctx Contexto HTTP
     * @param next Próximo middleware/handler na cadeia
     * @throws Exception se houver erro no processamento
     */
    void handle(HttpContext ctx, Runnable next) throws Exception;

    /**
     * Combina este middleware com outro
     */
    default Middleware andThen(Middleware after) {
        return (ctx, next) -> handle(ctx, () -> {
            Try.runThrowing(() -> after.handle(ctx, next)).onFailure(Throwable::printStackTrace);
        });
    }
}