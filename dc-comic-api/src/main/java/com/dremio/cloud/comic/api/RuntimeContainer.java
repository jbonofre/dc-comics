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
package com.dremio.cloud.comic.api;

import com.dremio.cloud.comic.api.container.Beans;
import com.dremio.cloud.comic.api.container.Contexts;
import com.dremio.cloud.comic.api.container.Listeners;
import com.dremio.cloud.comic.api.event.Emitter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

public interface RuntimeContainer extends Emitter, AutoCloseable {

    Beans getBeans();

    Contexts getContexts();

    Listeners getListeners();

    <T> Instance<T> lookup(Class<T> type);

    <T> Instance<T> lookup(Type type);

    <A, T> Instance<T> lookups(Class<A> type, Function<List<Instance<A>>, T> postProcessor);

    @Override
    void close();

}
