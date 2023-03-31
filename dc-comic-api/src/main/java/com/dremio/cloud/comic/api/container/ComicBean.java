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

import com.dremio.cloud.comic.api.Instance;
import com.dremio.cloud.comic.api.RuntimeContainer;
import com.dremio.cloud.comic.api.scope.DefaultScoped;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public interface ComicBean<T> {

    /**
     * @return bean raw type (to match during the bean lookup).
     */
    Type type();

    /**
     * @param container container the bean is lookup up from.
     * @param dependents list you can add dependencies to release when the instance will be cleaned up.
     * @return the bean instance (with injections filled).
     */
    T create(RuntimeContainer container, List<Instance<?>> dependents);

    /**
     * @param container container the bean instance was looked up.
     * @param instance instance to destroy/clean up.
     */
    default void destroy(final RuntimeContainer container, final T instance) {
        // no-op
    }

    /**
     * @param container container the bean instance was looked up.
     * @param dependents list you can add dependencies to release when the instance will be cleaned up.
     * @param instance instance to fill injections to.
     */
    default void inject(final RuntimeContainer container, final List<Instance<?>> dependents, final T instance) {
        // no-op
    }

    /**
     * @return the priority of the bean in the case of a list injection.
     */
    default Class<?> scope() {
        return DefaultScoped.class;
    }

    /**
     * @return the priority of the bean in the case of a list injection.
     */
    default int priority() {
        return 1000;
    }

    /**
     * Bean metadata.
     * <p>
     * Strictly speaking it is an open storage about the bean, can really be anything.
     * <p>
     * A sample usage is to provide to context a subclass usable to wrap the actual instance.
     * Can also be used to add interceptors if you add an interceptor implementation support.
     *
     * @return enables to hold more information for integrations.
     */
    default Map<String, Object> data() {
        return Map.of();
    }
}
