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
package com.github.rickmvi.jtoolbox.file.util;

import com.github.rickmvi.jtoolbox.console.IO;

import java.io.IOException;
import java.nio.file.*;

/**
 * <h2>FileWatcher</h2>
 *
 * A utility for monitoring changes in directories using Java’s {@link WatchService}.
 * Supports CREATE, DELETE, and MODIFY events.
 *
 * <h3>Example:</h3>
 * <pre>{@code
 * FileWatcher watcher = new FileWatcher("data/logs");
 * watcher.startWatching();
 * }</pre>
 *
 * <p>Note: This class runs synchronously. Use a separate thread or executor for non-blocking behavior.</p>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 2025
 */
public class FileWatcher {

    private final Path pathToWatch;

    public FileWatcher(String directoryPath) {
        this.pathToWatch = Paths.get(directoryPath);
    }

    public void startWatching() throws IOException, InterruptedException {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            pathToWatch.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            IO.println("[FileWatcher] Monitoring: " + pathToWatch);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    IO.println("[" + event.kind() + "] " + event.context());
                }
                key.reset();
            }
        }
    }
}
