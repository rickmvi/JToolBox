package com.github.rickmvi.jtoolbox.http;

import java.net.http.HttpClient;

public enum HttpProtocol {

    HTTP_1_1(HttpClient.Version.HTTP_1_1),
    HTTP_2(HttpClient.Version.HTTP_2);

    private final HttpClient.Version version;

    HttpProtocol(HttpClient.Version version) {
        this.version = version;
    }

    public HttpClient.Version getJavaNetVersion() {
        return version;
    }

}
