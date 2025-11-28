package com.github.rickmvi.jtoolbox.http.client;

import com.github.rickmvi.jtoolbox.http.status.StatusCode;
import com.github.rickmvi.jtoolbox.json.JsonX;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record ClientResponse(int statusCode, String body) {

    private static final Gson gson = JsonX.create().getGson();

    public @NotNull StatusCode status() {
        return StatusCode.fromCode(statusCode);
    }

    public boolean isSuccess() {
        return status().isSuccess();
    }

    public boolean isError() {
        return status().isError();
    }

    public <T> T asJson(Class<T> clazz) {
        return gson.fromJson(body, clazz);
    }

    public void ifSuccess(Consumer<ClientResponse> action) {
        if (isSuccess()) {
            action.accept(this);
        }
    }

    public void ifError(Consumer<ClientResponse> action) {
        if (isError()) {
            action.accept(this);
        }
    }

    @Override
    public @NotNull String toString() {
        return "Response[" + statusCode + " " + status().reason() + "]";
    }
}
