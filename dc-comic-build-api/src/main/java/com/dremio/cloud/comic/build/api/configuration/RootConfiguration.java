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
package com.dremio.cloud.comic.build.api.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a class as being an instantiable configuration.
 * <p>
 * For records all constructor (the most public one and with the most parameters) members are used.
 * <p>
 * You can use {@link Property} to tweak the lookup of the values in the context and document the configuration.
 * <p>
 * IMPORTANT: pojo are not (yet) supported so ensure to define a record.
 * <p>
 * The value are read using {@code com.dremio.cloud.comic.api.configuration.Configuration} API.
 * The key starts with the {@link RootConfiguration#value()} (or simple name of the class if not set) then properties are appended separated by dots.
 * For lists, it uses comma separated values for primitives but an indexed prefix for nested objects ({@code prefix.nestedList.0.nestedObjectMember=xxx}).
 * In this last case you need to set {@code prefix.nestedList.length=N} value to ensure the instantiator creates the right list.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RootConfiguration {

    /**
     * @return prefix name using properties syntax. If not set the simple class name is used.
     */
    String value() default "";
}
