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
package com.dremio.cloud.comic.json.serialization;

import com.dremio.cloud.comic.json.spi.Parser;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.function.Function;

public interface JsonCodec<A> {

    Type type();

    A read(DeserializationContext context) throws IOException;

    void write(A value, SerializationContext writer) throws IOException;

    class SerializationContext {
        private final Writer writer;
        private final Function<Class<?>, JsonCodec<?>> codecLookup;

        public SerializationContext(final Writer writer, final Function<Class<?>, JsonCodec<?>> codecLookup) {
            this.writer = writer;
            this.codecLookup = codecLookup;
        }

        public Writer writer() {
            return writer;
        }

        @SuppressWarnings("unchecked")
        public <A> JsonCodec<A> codec(final Class<A> clazz) {
            return (JsonCodec<A>) codecLookup.apply(clazz);
        }
    }

    class DeserializationContext {
        private final Parser parser;
        private final Function<Class<?>, JsonCodec<?>> codecLookup;

        public DeserializationContext(final Parser parser, final Function<Class<?>, JsonCodec<?>> codecLookup) {
            this.parser = parser;
            this.codecLookup = codecLookup;
        }

        public Parser parser() {
            return parser;
        }

        @SuppressWarnings("unchecked")
        public <A> JsonCodec<A> codec(final Class<A> clazz) {
            return (JsonCodec<A>) codecLookup.apply(clazz);
        }
    }

}
