/*
 * Copyright (c) 2023 - Dremio - https://www.dremio.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.dremio.cloud.comic.testing.module;

import com.dremio.cloud.comic.api.RuntimeContainer;
import com.dremio.cloud.comic.api.container.ComicBean;
import com.dremio.cloud.comic.api.container.ComicListener;
import com.dremio.cloud.comic.api.container.ComicModule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ListingPredicateModule implements ComicModule {

    private final List<ComicBean<?>> beans = new ArrayList<>();
    private final List<ComicListener<?>> listeners = new ArrayList<>();

    public ListingPredicateModule(final Predicate<String> beanPredicate,
                                  final Predicate<String> listenerPredicate,
                                  final List<Path> dirs) {
        find(beanPredicate, listenerPredicate, dirs);
    }

    @Override
    public int priority() { // "last" - give some space in case of - to ensure we filter others before us to see all beans
        return Integer.MAX_VALUE - 100;
    }

    @Override
    public Stream<ComicBean<?>> beans() {
        return beans.stream();
    }

    @Override
    public Stream<ComicListener<?>> listeners() {
        try {
            return listeners.stream();
        } finally {
            // last method called in ContainerImpl so just cleanup
            beans.clear();
            listeners.clear();
        }
    }

    @Override
    public BiPredicate<RuntimeContainer, ComicBean<?>> beanFilter() {
        return (container, bean) -> {
            if (beans.contains(bean)) {
                return true;
            }
            // drop the beans we found but which were registered by another module
            beans.removeIf(discovered -> Objects.equals(discovered.getClass(), bean.getClass()));
            return true;
        };
    }

    @Override
    public BiPredicate<RuntimeContainer, ComicListener<?>> listenerFilter() {
        return (container, listener) -> {
            if (listeners.contains(listener)) {
                return true;
            }
            // drop the listeners we found but which were registered by another module
            listeners.removeIf(discovered -> Objects.equals(discovered.getClass(), listener.getClass()));
            return true;
        };
    }

    private void find(final Predicate<String> beanPredicate, final Predicate<String> listenerPredicate, final List<Path> dirs) {
        final var loader = Thread.currentThread().getContextClassLoader();
        dirs.forEach(dir -> {
            try {
                Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                        final var filename = file.getFileName().toString();
                        if (!filename.endsWith(".class")) {
                            return super.visitFile(file, attrs);
                        }

                        final var relative = dir.relativize(file).normalize().toString();
                        if (!relative.startsWith("META-INF")) {
                            try {
                                if (beanPredicate.test(filename)) {
                                    final var clazz = load(loader, toClassName(relative));
                                    if (clazz != null && ComicBean.class.isAssignableFrom(clazz)) {
                                        beans.add(clazz.asSubclass(ComicBean.class).getConstructor().newInstance());
                                    }
                                }
                                if (listenerPredicate.test(filename)) {
                                    final var clazz = load(loader, toClassName(relative));
                                    if (clazz != null && ComicListener.class.isAssignableFrom(clazz)) {
                                        listeners.add(clazz.asSubclass(ComicListener.class).getConstructor().newInstance());
                                    }
                                }
                            } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                                throw new IllegalStateException(e);
                            } catch (final InvocationTargetException e) {
                                throw new IllegalStateException(e.getTargetException());
                            }
                        }

                        return super.visitFile(file, attrs);
                    }
                });
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private Class<?> load(final ClassLoader loader, final String name) {
        try {
            final var clazz = loader.loadClass(name);
            clazz.getConstructor(); // ensure there is a constructor we can use
            return clazz;
        } catch (final ClassNotFoundException | NoSuchMethodException e) {
            return null;
        }
    }

    private String toClassName(final String resource) {
        return resource.substring(0, resource.length() - ".class".length()).replace(File.separatorChar, '.');
    }

}
