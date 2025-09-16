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
package com.github.rickmvi.jtoolbox.utils.primitives.internal;

import org.jetbrains.annotations.Contract;

/**
 * @author Rick M. Viana
 * @since 1.1
 */
public class CharValidator {

    @Contract(pure = true)
    public boolean equals(char x, char y) {
        return x == y;
    }

    @Contract(pure = true)
    public boolean notEquals(char x, char y) {
        return !equals(x, y);
    }
    
    @Contract(pure = true)
    public boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    @Contract(pure = true)
    public boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    @Contract(pure = true)
    public boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }
    
    @Contract(pure = true)
    public boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
    
    @Contract(pure = true)
    public boolean isUpperCase(char c) {
        return c >= 'A' && c <= 'Z';
    }
    
    @Contract(pure = true)
    public boolean isLowerCase(char c) {
        return c >= 'a' && c <= 'z';
    }
    
    @Contract(pure = true)
    public boolean isUpperCaseOrDigit(char c) {
        return isUpperCase(c) || isDigit(c);
    }
    
    @Contract(pure = true)
    public boolean isLowerCaseOrDigit(char c) {
        return isLowerCase(c) || isDigit(c);
    }
}
