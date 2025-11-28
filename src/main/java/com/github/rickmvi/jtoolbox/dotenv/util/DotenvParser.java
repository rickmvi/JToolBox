package com.github.rickmvi.jtoolbox.dotenv.util;

import com.github.rickmvi.jtoolbox.dotenv.DotenvEntry;
import com.github.rickmvi.jtoolbox.dotenv.exceptions.DotenvException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code DotenvParser} class is responsible for parsing ".env" files and
 * extracting key-value pairs encapsulated in {@code DotenvEntry} objects. It supports
 * handling missing files and malformed entries based on configurations provided at
 * instantiation.
 */
public class DotenvParser {

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("^\\s*$");
    private static final Pattern DOTENV_ENTRY_PATTERN = Pattern.compile(
            "^\\s*([\\w.\\-]+)\\s*(=)\\s*(['][^']*[']|[\"][^\"]*[\"]|[^#]*)?\\s*(#.*)?$"
    );

    private static final String FAILED_TO_READ_ENV_FILE_MESSAGE = "Failed to read .env file";
    private static final String MALFORMED_ENTRY_MESSAGE = "Malformed entry: %s";
    private static final String UNMATCHED_QUOTES_MESSAGE = "Malformed entry, unmatched quotes: %s";

    private final DotenvReader reader;
    private final boolean throwIfMissing;
    private final boolean throwIfMalformed;

    public DotenvParser(DotenvReader reader, boolean throwIfMissing, boolean throwIfMalformed) {
        this.reader = reader;
        this.throwIfMissing = throwIfMissing;
        this.throwIfMalformed = throwIfMalformed;
    }

    public List<DotenvEntry> parse() {
        List<String> lines = readLines();
        List<DotenvEntry> entries = new ArrayList<>();
        StringBuilder currentEntry = new StringBuilder();

        for (String line : lines) {
            handleLine(line, currentEntry, entries);
        }

        return entries;
    }

    private void handleLine(String line, StringBuilder currentEntry, List<DotenvEntry> entries) {
        if (shouldSkipLine(line, currentEntry)) {
            return;
        }

        currentEntry.append(line);

        Optional<DotenvEntry> parsedEntry = parseEntry(currentEntry.toString());

        if (parsedEntry.isEmpty()) {
            handleMalformedEntry(currentEntry.toString());
            currentEntry.setLength(0);
            return;
        }

        DotenvEntry entry = parsedEntry.get();

        if (isMultilineEntry(entry.value())) {
            currentEntry.append("\n");
            return;
        }

        if (!isValidQuotedString(entry.value())) {
            handleUnmatchedQuotes(currentEntry.toString());
            currentEntry.setLength(0);
            return;
        }

        entries.add(createFinalEntry(entry));
        currentEntry.setLength(0);
    }

    private List<String> readLines() {
        try {
            return reader.read();
        } catch (DotenvException e) {
            return handleDotenvException(e);
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_READ_ENV_FILE_MESSAGE, e);
        }
    }

    private boolean shouldSkipLine(String line, StringBuilder currentEntry) {
        if (!currentEntry.isEmpty()) {
            return false;
        }
        return isBlank(line) || isWhiteSpace(line) || isComment(line);
    }

    private Optional<DotenvEntry> parseEntry(String text) {
        Matcher matcher = DOTENV_ENTRY_PATTERN.matcher(text);

        if (!matcher.matches() || matcher.groupCount() < 3) {
            return Optional.empty();
        }

        String key = matcher.group(1);
        String value = matcher.group(3);

        return Optional.of(new DotenvEntry(key, value));
    }

    private DotenvEntry createFinalEntry(DotenvEntry entry) {
        String processedValue = QuotedStringProcessor.stripQuotes(entry.value());
        return new DotenvEntry(entry.key(), processedValue);
    }

    private boolean isMultilineEntry(String value) {
        return QuotedStringProcessor.isQuoted(value)
                && !QuotedStringProcessor.endsWithQuote(value);
    }

    private boolean isValidQuotedString(String value) {
        return QuotedStringProcessor.isValid(value);
    }

    private void handleMalformedEntry(String entry) {
        if (throwIfMalformed) {
            throw new DotenvException(String.format(MALFORMED_ENTRY_MESSAGE, entry));
        }
    }

    private void handleUnmatchedQuotes(String entry) {
        if (throwIfMalformed) {
            throw new DotenvException(String.format(UNMATCHED_QUOTES_MESSAGE, entry));
        }
    }

    private List<String> handleDotenvException(DotenvException e) {
        if (throwIfMissing) {
            throw new RuntimeException(FAILED_TO_READ_ENV_FILE_MESSAGE, e);
        }
        return List.of();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static boolean isWhiteSpace(String s) {
        return WHITE_SPACE_PATTERN.matcher(s).matches();
    }

    private static boolean isComment(String s) {
        return s.startsWith("#") || s.startsWith("//");
    }

    private static class QuotedStringProcessor {
        private static final String QUOTE = "\"";
        private static final Pattern QUOTE_PATTERN = Pattern.compile(QUOTE);

        static boolean isValid(String input) {
            if (input == null) {
                return false;
            }

            String trimmed = input.trim();

            if (!trimmed.startsWith(QUOTE) && !trimmed.endsWith(QUOTE)) {
                return true;
            }

            if (trimmed.length() == 1 || !trimmed.startsWith(QUOTE) || !trimmed.endsWith(QUOTE)) {
                return false;
            }

            return !hasUnescapedQuoteInContent(trimmed);
        }

        private static boolean hasUnescapedQuoteInContent(String quotedString) {
            String content = quotedString.substring(1, quotedString.length() - 1);
            Matcher matcher = QUOTE_PATTERN.matcher(content);

            while (matcher.find()) {
                int quoteIndex = matcher.start();
                if (quoteIndex == 0 || content.charAt(quoteIndex - 1) != '\\') {
                    return true;
                }
            }
            return false;
        }

        static String stripQuotes(String input) {
            if (input == null) {
                return null;
            }

            String trimmed = input.trim();
            return isQuoted(trimmed)
                    ? trimmed.substring(1, trimmed.length() - 1)
                    : trimmed;
        }

        static boolean isQuoted(String s) {
            return s != null
                    && s.length() > 1
                    && s.startsWith(QUOTE)
                    && s.endsWith(QUOTE);
        }

        static boolean endsWithQuote(String s) {
            return s != null && s.endsWith(QUOTE);
        }
    }
}