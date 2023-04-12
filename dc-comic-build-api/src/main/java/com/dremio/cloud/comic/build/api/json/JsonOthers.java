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
package com.dremio.cloud.comic.build.api.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a record member as the sink for unmapped data by other members.
 * Attribute must be a {@code Map<String, Object>}.
 * Map values will be {@code Object} friendly, i.e. compatible with {@link com.dremio.cloud.comic.json.internal.codec.ObjectJsonCodec}:
 * <ul>
 *     <li>{@link Object}</li>
 *     <li>{@link String}</li>
 *     <li>{@link Boolean}</li>
 *     <li>{@link java.math.BigDecimal}</li>
 *     <li>{@link java.util.List<Object>}</li>
 *     <li>{@link java.util.Map<String, Object>}</li>
 * </ul>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
public @interface JsonOthers {
}
