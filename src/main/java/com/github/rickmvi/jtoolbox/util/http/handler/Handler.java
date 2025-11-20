package com.github.rickmvi.jtoolbox.util.http.handler;

import com.github.rickmvi.jtoolbox.util.http.HttpContext;

@FunctionalInterface
public interface Handler {

    void handle(HttpContext ctx);

}
