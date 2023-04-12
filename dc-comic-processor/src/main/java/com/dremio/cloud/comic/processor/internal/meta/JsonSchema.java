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
package com.dremio.cloud.comic.processor.internal.meta;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;

public record JsonSchema(
        String ref, String id,
        String type, Boolean nullable,
        String format, String pattern,
        Object additionalProperties,
        Map<String, JsonSchema> properties,
        JsonSchema items) implements GenericObjectJsonSerializationLike {

    @Override
    public Map<String, Object> asMap() {
        return Stream.of(
                        id == null ? null : entry("$id", id),
                        ref == null ? null : entry("$ref", ref),
                        type == null ? null : entry("type", type),
                        format == null ? null : entry("format", format),
                        pattern == null ? null : entry("pattern", pattern),
                        additionalProperties == null ? null : entry("additionalProperties", additionalProperties),
                        nullable == null ? null : entry("nullable", nullable),
                        items == null ? null : entry("items", items.asMap()),
                        properties == null ? null : entry("properties", properties.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(toMap(Map.Entry::getKey, e -> e.getValue().asMap(), (a, b) -> a, LinkedHashMap::new))))
                .filter(Objects::nonNull)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
