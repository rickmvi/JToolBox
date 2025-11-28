package com.github.rickmvi.jtoolbox.dotenv;

import com.github.rickmvi.jtoolbox.dotenv.exceptions.DotenvException;
import com.github.rickmvi.jtoolbox.dotenv.util.DotenvParser;
import com.github.rickmvi.jtoolbox.dotenv.util.DotenvReader;

import java.util.*;

import static java.util.stream.Collectors.*;

public class DotenvBuilder {

    private String filename = ".env";

    private String directory = "./";

    private boolean systemProperties = false;

    private boolean throwIfMissing = false;

    private boolean throwIfMalformed = true;

    public DotenvBuilder() {
    }

    public DotenvBuilder filename(final String filename) {
        this.filename = filename;
        return this;
    }

    public DotenvBuilder directory(final String directory) {
        this.directory = directory;
        return this;
    }

    public DotenvBuilder systemProperties() {
        this.systemProperties = true;
        return this;
    }

    public DotenvBuilder throwIfMissing() {
        this.throwIfMissing = true;
        return this;
    }

    public DotenvBuilder throwIfMalformed() {
        this.throwIfMalformed = true;
        return this;
    }

    public DotenvBuilder ignoreIfMissing() {
        this.throwIfMissing = false;
        return this;
    }

    public DotenvBuilder ignoreIfMalformed() {
        this.throwIfMalformed = false;
        return this;
    }

    public Dotenv load() throws DotenvException {
        final var reader = configureParser();
        final List<DotenvEntry> entries = reader.parse();
        if (systemProperties) {
            entries.forEach(entry -> System.setProperty(entry.key(), entry.value()));
        }

        return new DotenvImpl(entries);
    }

    private DotenvParser configureParser() {
        return new DotenvParser(
               new DotenvReader(directory, filename),
               throwIfMissing, throwIfMalformed);
    }

    static class DotenvImpl implements Dotenv {
        private final Map<String, String> envVars;
        private final Map<String, String> systemEnvironment;
        private final Set<DotenvEntry>    allEntries;
        private final Set<DotenvEntry>    fileEntries;

        public DotenvImpl(final List<DotenvEntry> entriesFromFile) {
            final Map<String, String> envVarsInFile =
                    entriesFromFile.stream()
                            .collect(toMap(DotenvEntry::key, DotenvEntry::value, (a, b) -> b));

            this.systemEnvironment = System.getenv();
            this.envVars = new HashMap<>(envVarsInFile);
            this.envVars.putAll(systemEnvironment);

            this.allEntries = entriesToDotenvSet(this.envVars);
            this.fileEntries = entriesToDotenvSet(envVarsInFile);
        }

        private static Set<DotenvEntry> entriesToDotenvSet(final Map<String, String> entries) {
            return entries.entrySet()
                    .stream()
                    .map(entry -> new DotenvEntry(entry.getKey(), entry.getValue()))
                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
        }

        @Override
        public Set<DotenvEntry> entries() {
            return allEntries;
        }

        @Override
        public Set<DotenvEntry> entries(final Dotenv.Filter filter) {
            return filter == null ? allEntries : fileEntries;
        }

        @Override
        public String get(final String key) {
            final String value = systemEnvironment.get(key);
            return value == null ? envVars.get(key) : value;
        }

        @Override
        public String get(String key, String defaultValue) {
            final String value = this.get(key);
            return value == null ? defaultValue : value;
        }
    }
}
