package com.github.rickmvi.jtoolbox.ioc.context;

import com.github.rickmvi.jtoolbox.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for scanning packages for classes. This class supports locating all classes
 * within a specified package using reflection.
 * <p>
 * The main method, {@code scan(String basePackage)}, scans the given package and its subpackages
 * to return a set of classes found in the specified location.
 * <p>
 * This utility is intended to be used with Java class files located within the application's classpath.
 */
public class PackageScanner {

    public static Set<Class<?>> scan(@NotNull String basePackage) {
        Set<Class<?>> classes = new HashSet<>();
        String path = basePackage.replace('.', '/');

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);

            if (resource == null) {
                Logger.warn("Package not found: {}", basePackage);
                return classes;
            }

            File directory = new File(resource.getFile());

            if (directory.exists()) {
                scanDirectory(directory, basePackage, classes);
            }

        } catch (Exception e) {
            Logger.error("Error scanning package {}: {}", basePackage, e.getMessage());
        }

        return classes;
    }

    private static void scanDirectory(File directory, String packageName, Set<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' +
                        file.getName().substring(0, file.getName().length() - 6);

                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    Logger.debug("Could not load class: {}", className);
                } catch (NoClassDefFoundError ignored) {}
            }
        }
    }
}