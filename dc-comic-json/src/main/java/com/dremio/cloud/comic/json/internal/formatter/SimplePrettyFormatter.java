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
package com.dremio.cloud.comic.json.internal.formatter;

import com.dremio.cloud.comic.api.container.Types;
import com.dremio.cloud.comic.json.JsonMapper;
import com.dremio.cloud.comic.json.internal.JsonStrings;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

/**
 * This implementation simply loads back the object in memory using generic types
 * then goes through the object in a formatter manner.
 * <p>
 * This is not efficient but formatting is mainly intended for debug purposes so should be fine.
 */
public class SimplePrettyFormatter implements Function<String, String> {

    private final JsonMapper mapper;

    // config
    private int indentSize = 2;

    public SimplePrettyFormatter(final JsonMapper mapper) {
        this.mapper = mapper;
    }

    public SimplePrettyFormatter indentSize(final int indentSize) {
        this.indentSize = Math.max(0, indentSize);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String apply(final String json) {
        if (json.startsWith("[")) {
            final List<Object> data;
            try (final var reader = new StringReader(json)) {
                data = mapper.read(new Types.ParameterizedTypeImpl(List.class, Object.class), reader);
            }
            return formatList(data, 0);
        }
        if (json.startsWith("{")) {
            final Map<String, Object> map;
            try (final var reader = new StringReader(json)) {
                map = (Map<String, Object>) mapper.read(Object.class, reader);
            }
            return formatObject(map, 0);
        }
        return json; // primitives are already formatted
    }


    @SuppressWarnings("unchecked")
    private String format(final Object value, final int level) {
        if (value instanceof Map<?, ?> map) {
            return formatObject((Map<String, ?>) map, level);
        }
        if (value instanceof Collection<?> list) {
            return formatList(list, level);
        }

        // primitives are not intended since "inline" for objects

        if (value instanceof String s) {
            return JsonStrings.escape(s);
        }
        if (value instanceof BigDecimal bd) {
            return bd.toString();
        }
        if (value instanceof Boolean bool) {
            return String.valueOf(bool);
        }
        if (value == null) {
            return "null";
        }

        throw new IllegalArgumentException("Unsupported generic type: '" + value + "'");
    }

    private String formatObject(final Map<String, ?> map, final int level) {
        if (map.isEmpty()) {
            return "{}";
        }

        final var endIndentPrefix = level == 0 ? "" : " ".repeat(level * indentSize);
        final var indentPrefix = endIndentPrefix + " ".repeat(indentSize);
        return map.entrySet().stream()
                .map(entry -> indentPrefix + JsonStrings.escape(entry.getKey()) + ": " + format(entry.getValue(), level + 1))
                .collect(joining(",\n", "{\n", '\n' + endIndentPrefix + "}"));
    }

    private String formatList(final Collection<?> data, final int level) {
        if (data.isEmpty()) {
            return "[]";
        }

        final var endIndentPrefix = level == 0 ? "" : " ".repeat(level * indentSize);
        final var indentPrefix = endIndentPrefix + " ".repeat(indentSize);
        return data.stream()
                .map(it -> indentPrefix + format(it, level + 1))
                .collect(joining(",\n", "[\n", '\n' + endIndentPrefix + "]"));
    }

}
