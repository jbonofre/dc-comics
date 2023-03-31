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
package com.dremio.cloud.comic.json;

import com.dremio.cloud.comic.api.container.Types;
import com.dremio.cloud.comic.json.internal.JsonMapperImpl;
import com.dremio.cloud.comic.json.pretty.PrettyJsonMapper;
import com.dremio.cloud.comic.json.serialization.JsonCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.dremio.cloud.comic.json.spi.Parser.Event.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonMapperTest {

    private final List<JsonCodec<?>> jsonCodecs = List.of(new JsonCodec<Simple>() {
        @Override
        public Type type() {
            return Simple.class;
        }

        @Override
        public Simple read(final DeserializationContext context) {
            final var reader = context.parser();
            assertEquals(START_OBJECT, reader.next());
            try {
                assertEquals(KEY_NAME, reader.next());
                assertEquals("name", reader.getString());
                assertEquals(VALUE_STRING, reader.next());
                return new Simple(reader.getString());
            } finally {
                assertEquals(END_OBJECT, reader.next());
            }
        }

        @Override
        public void write(final Simple value, final SerializationContext ctx) throws IOException {
            ctx.writer().write("{\"name\":\"" + value.name() + "\"}");
        }
    });

    @Test
    void prettyMapper() {
        try (final var mapper = new PrettyJsonMapper(new JsonMapperImpl(jsonCodecs, key -> Optional.empty()))) {
            assertEquals("""
                    {
                      "name": "formatted"
                    }""", mapper.toString(new Simple("formatted")));
        }
    }

    @Test
    void directCodec() throws IOException {
        final var json = "{\"name\":\"hello\"}";
        try (final var mapper = new JsonMapperImpl(jsonCodecs, key -> Optional.empty());
             final var reader = new StringReader(json)) {

            final var simple = mapper.read(Simple.class, reader);
            assertEquals("hello", simple.name());

            try (final var out = new StringWriter()) {
                mapper.write(simple, out);
                out.flush();
                assertEquals(json, out.toString());
            }
        }
    }

    @Test
    void listCodec() throws IOException {
        final var json = "[{\"name\":\"hello\"},{\"name\":\"second\"}]";
        try (final var mapper = new JsonMapperImpl(jsonCodecs, key -> Optional.empty());
             final var reader = new StringReader(json)) {

            final var simple = mapper.read(new Types.ParameterizedTypeImpl(List.class, Simple.class), reader);
            assertEquals("[Simple[name=hello], Simple[name=second]]", simple.toString());

            try (final var out = new StringWriter()) {
                mapper.write(simple, out);
                out.flush();
                assertEquals(json, out.toString());
            }
        }
    }

    @Test
    void mapCodec() throws IOException {
        final var json = "{\"first\":{\"name\":\"hello\"},\"second\":{\"name\":\"2\"}}";
        try (final var mapper = new JsonMapperImpl(jsonCodecs, key -> Optional.empty());
             final var reader = new StringReader(json)) {

            final var simple = mapper.read(new Types.ParameterizedTypeImpl(Map.class, String.class, Simple.class), reader);
            assertEquals("{first=Simple[name=hello], second=Simple[name=2]}", simple.toString());

            try (final var out = new StringWriter()) {
                mapper.write(simple, out);
                out.flush();
                assertEquals(json, out.toString());
            }
        }
    }

    @Test
    void listString() throws IOException {
        final var json = "[\"first\",\"second\"]";
        try (final var mapper = new JsonMapperImpl(List.of(), key -> Optional.empty());
             final var reader = new StringReader(json)) {

            final var simple = mapper.read(new Types.ParameterizedTypeImpl(List.class, String.class), reader);
            assertEquals("[first, second]", simple.toString());

            try (final var out = new StringWriter()) {
                mapper.write(simple, out);
                out.flush();
                assertEquals(json, out.toString());
            }
        }
    }

    public record Simple(String name) {
    }

}
