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
package com.dremio.cloud.comic.api.container.bean;

import com.dremio.cloud.comic.api.Instance;
import com.dremio.cloud.comic.api.RuntimeContainer;
import com.dremio.cloud.comic.api.container.ComicBean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class OptionalBean<A> implements ComicBean<Optional<A>> {

    private final ComicBean<A> delegating;

    public OptionalBean(final ComicBean<A> delegating) {
        this.delegating = delegating;
    }

    @Override
    public Type type() {
        return delegating.type();
    }

    @Override
    public Optional<A> create(final RuntimeContainer container, final List<Instance<?>> dependents) {
        return Optional.of(delegating.create(container, dependents));
    }

    @Override
    public void destroy(final RuntimeContainer container, final Optional<A> instance) {
        delegating.destroy(container, instance.orElseThrow());
    }

    @Override
    public Class<?> scope() {
        return delegating.scope();
    }

    @Override
    public int priority() {
        return delegating.priority();
    }

}
