package com.github.rickmvi.jtoolbox.util.http.route;

import com.github.rickmvi.jtoolbox.util.http.Method;
import com.github.rickmvi.jtoolbox.util.http.handler.Handler;

public record Route(Method method, String path, Handler handler) {
}
