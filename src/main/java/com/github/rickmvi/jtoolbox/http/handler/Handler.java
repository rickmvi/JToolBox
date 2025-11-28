package com.github.rickmvi.jtoolbox.http.handler;

import com.github.rickmvi.jtoolbox.http.HttpContext;

@FunctionalInterface
public interface Handler {

    void handle(HttpContext ctx);

}
