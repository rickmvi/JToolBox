package com.github.rickmvi.jtoolbox.http.route;

import com.github.rickmvi.jtoolbox.control.For;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a route pattern used for matching URL paths and extracting parameters in HTTP routing.
 * This class is capable of compiling a given string pattern into a regular expression and determining
 * if a provided path matches the pattern. It can also extract parameters from matched paths.
 */
public class RoutePattern {

    private final String originalPattern;
    private final Pattern compiledPattern;
    private final List<String> paramNames;
    private final boolean isWildcard;

    public RoutePattern(String pattern) {
        this.originalPattern = pattern;
        this.paramNames = new ArrayList<>();
        this.isWildcard = pattern.endsWith("/*");
        this.compiledPattern = compilePattern(pattern);
    }

    private Pattern compilePattern(String pattern) {
        if (pattern.length() > 1 && pattern.endsWith("/")) {
            pattern = pattern.substring(0, pattern.length() - 1);
        }

        StringBuilder regex = new StringBuilder("^");
        String[] segments   = pattern.split("/");

        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }

            regex.append("/");

            if (segment.equals("*")) {
                regex.append(".*");
                continue;
            }

            if (!segment.startsWith(":")) {
                regex.append(Pattern.quote(segment));
                continue;
            }

            String paramName = segment.substring(1);
            boolean optional = paramName.endsWith("?");

            if (optional) {
                paramName = paramName.substring(0, paramName.length() - 1);
            }

            paramNames.add(paramName);

            if (!optional) {
                regex.append("([^/]+)");
                continue;
            }

            regex.append("([^/]+)?");
        }

        regex.append("/?$");
        return Pattern.compile(regex.toString());
    }

    /**
     * Verifica se um path corresponde a este pattern
     */
    public boolean matches(String path) {
        // Normalize path
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return compiledPattern.matcher(path).matches();
    }

    /**
     * Extrai os path parameters de um path que corresponde ao pattern
     */
    public @NotNull Map<String, String> match(String path) {
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        Matcher matcher = compiledPattern.matcher(path);

        if (!matcher.matches()) {
            return Collections.emptyMap();
        }

        Map<String, String> params = new LinkedHashMap<>();

        for (int i = 0; i < paramNames.size(); i++) {
            String value = matcher.group(i + 1);
            if (value != null) {
                params.put(paramNames.get(i), value);
            }
        }

        return params;
    }

    public String pattern() {
        return originalPattern;
    }

    public List<String> parameterNames() {
        return Collections.unmodifiableList(paramNames);
    }

    @Override
    public String toString() {
        return originalPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutePattern that)) return false;
        return Objects.equals(originalPattern, that.originalPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalPattern);
    }
}