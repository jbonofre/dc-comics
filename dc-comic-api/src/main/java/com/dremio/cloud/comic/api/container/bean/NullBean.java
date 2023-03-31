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

public class NullBean<A> implements ComicBean<A> {

    private final Type expectedType;

    public NullBean(final Type expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public Type type() {
        return expectedType;
    }

    @Override
    public A create(final RuntimeContainer container, final List<Instance<?>> dependents) {
        return null;
    }

}
