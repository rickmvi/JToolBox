package com.github.rickmvi.jtoolbox.http.route;

import com.github.rickmvi.jtoolbox.http.Method;
import com.github.rickmvi.jtoolbox.http.handler.Handler;

public record Route(Method method, String path, Handler handler) {
}
