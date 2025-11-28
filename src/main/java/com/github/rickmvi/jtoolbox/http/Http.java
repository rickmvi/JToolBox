/*
 * Console API - Utilitarian library for input, output and formatting on the console.
 * Copyright (C) 2025  Rick M. Viana
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.rickmvi.jtoolbox.http;

import com.github.rickmvi.jtoolbox.http.client.ClientRequest;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Http {

    private Http() {}

    public static @NotNull ClientRequest request(String url) {
        return new ClientRequest().uri(url);
    }

    public static ClientRequest get(String url) {
        return request(url).GET();
    }

    public static ClientRequest post(String url, Object jsonBody) {
        return request(url).POST_JSON(jsonBody);
    }
}