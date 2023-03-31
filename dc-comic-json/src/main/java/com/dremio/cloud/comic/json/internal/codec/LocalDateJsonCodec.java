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
package com.dremio.cloud.comic.json.internal.codec;

import com.dremio.cloud.comic.json.internal.JsonStrings;
import com.dremio.cloud.comic.json.serialization.JsonCodec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;

import static com.dremio.cloud.comic.json.spi.Parser.Event.VALUE_STRING;

public class LocalDateJsonCodec implements JsonCodec<LocalDate> {

    @Override
    public Type type() {
        return LocalDate.class;
    }

    @Override
    public LocalDate read(final DeserializationContext context) {
        final var parser = context.parser();
        if (!parser.hasNext() || parser.next() != VALUE_STRING) {
            throw new IllegalStateException("Expected VALUE_STRING");
        }
        return LocalDate.parse(parser.getString());
    }

    @Override
    public void write(final LocalDate value, final SerializationContext context) throws IOException {
        context.writer().write(JsonStrings.escape(value.toString()));
    }

}
