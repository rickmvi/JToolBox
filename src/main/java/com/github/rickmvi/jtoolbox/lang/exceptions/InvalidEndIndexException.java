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
package com.github.rickmvi.jtoolbox.lang.exceptions;

import com.github.rickmvi.jtoolbox.lang.message.ErrorMessage;
import org.jetbrains.annotations.NotNull;

public class InvalidEndIndexException extends RuntimeException {

    public InvalidEndIndexException() {
        super(ErrorMessage.INVALID_END_INDEX.getMessage());
    }

    public InvalidEndIndexException(int startIndex, int endIndex) {
        super(ErrorMessage.END_MENOR_THAN_START.format(endIndex, startIndex));
    }

    public InvalidEndIndexException(int startIndex, int endIndex, @NotNull ErrorMessage errorMessage) {
        super(errorMessage.format(endIndex, startIndex));
    }

    public InvalidEndIndexException(String message) {
        super(message);
    }

    public InvalidEndIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEndIndexException(Throwable cause) {
        super(cause);
    }

    public InvalidEndIndexException(int index) {
        super(ErrorMessage.INVALID_END_INDEX.format(index));
    }
}
