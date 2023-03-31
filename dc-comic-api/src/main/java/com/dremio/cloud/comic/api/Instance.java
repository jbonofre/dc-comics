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

import com.dremio.cloud.comic.api.container.ComicBean;

/**
 * A looked up instance (instantiated bean).
 * @param <T> the bean type.
 */
public interface Instance<T> extends AutoCloseable {

    /**
     * @return the bean this instance was created from if any, else {@code null}.
     */
    ComicBean<T> bean();

    /**
     * @return the instance itself.
     */
    T instance();

    /**
     * Release the instance (and potentially dependencies).
     */
    @Override
    default void close() {
        // no-op
    }

}
