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
package com.dremio.cloud.comic.api.container;

import com.dremio.cloud.comic.api.RuntimeContainer;
import com.dremio.cloud.comic.api.lifecycle.Start;
import com.dremio.cloud.comic.api.lifecycle.Stop;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

public class Listeners {

    // configuration
    private final Map<Type, List<ComicListener<?>>> listeners = new HashMap<>();

    // runtime - sorted and hierarchy resolved
    private final Map<Type, List<ComicListener<?>>> runtimeListeners = new HashMap<>();

    public void doRegister(final ComicListener<?>... listeners) {
        Stream.of(listeners)
                .collect(groupingBy(ComicListener::eventType))
                .forEach((k, v) -> this.listeners.computeIfAbsent(k, it -> new ArrayList<>(v.size())).addAll(v));
    }

    public <E> void fire(final RuntimeContainer container, final E event) {
        doFire(container, event.getClass(), event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void doFire(final RuntimeContainer container, final Class<?> type, final Object event) {
        var listeners = runtimeListeners.get(type);
        if (listeners == null) {
            listeners = resolveListeners(type);
            if (type != Start.class && type != Stop.class) { // no need to cache there
                runtimeListeners.put(type, listeners);
            }
        }
        listeners.forEach(l -> ((ComicListener) l).onEvent(container, event));
    }

    // part of that can be moved to build time add Type[] types() to ComicListener
    // for now not needed and enable more runtime features but can be an option if too slow when abusing of the event bus
    private List<ComicListener<?>> resolveListeners(final Class<?> type) {
        List<ComicListener<?>> listeners;
        final var all = new HashSet<ComicListener<?>>();
        var current = type;
        while (current != null) {
            final var lookup = this.listeners.get(current);
            if (lookup != null) {
                all.addAll(lookup);
            }
            Stream.of(current.getGenericInterfaces())
                    .forEach(itf -> {
                        final var lookups = this.listeners.get(itf);
                        if (lookups != null) {
                            all.addAll(lookups);
                        }
                    });
            current = current.getSuperclass();
        }
        listeners = all.stream().sorted(comparing(ComicListener::priority)).toList();
        return listeners;
    }

    public boolean hasDirectListener(final Type type) {
        return listeners.get(type) != null;
    }

}
