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

import com.dremio.cloud.comic.api.container.Types;
import com.dremio.cloud.comic.json.internal.parser.JsonParser;
import com.dremio.cloud.comic.json.serialization.JsonCodec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Supplier;

import static com.dremio.cloud.comic.json.spi.Parser.Event.END_ARRAY;
import static com.dremio.cloud.comic.json.spi.Parser.Event.START_ARRAY;

public class CollectionJsonCodec <A, L extends Collection<A>> implements JsonCodec<L> {

    private final JsonCodec<A> delegate;
    private final Supplier<L> collectionFactory;
    private final Type type;

    public CollectionJsonCodec(final JsonCodec<A> delegate, final Class<?> rawType, final Supplier<L> collectionFactory) {
        this.delegate = delegate;
        this.collectionFactory = collectionFactory;
        this.type = new Types.ParameterizedTypeImpl(rawType, delegate.type());
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public L read(final DeserializationContext context) throws IOException {
        final var reader = context.parser();
        if (!reader.hasNext()) {
            throw new IllegalStateException("No more element");
        }

        final var next = reader.next();
        if (next != START_ARRAY) {
            throw new IllegalStateException("Expected=START_ARRAY, but got " + next);
        }

        final var instance = collectionFactory.get();
        JsonParser.Event event;
        while (reader.hasNext() && (event = reader.next()) != END_ARRAY) {
            reader.rewind(event);
            instance.add(delegate.read(context));
        }

        return instance;
    }

    @Override
    public void write(final L value, final SerializationContext context) throws IOException {
        final var writer = context.writer();
        final var it = value.iterator();
        writer.write('[');
        while (it.hasNext()) {
            final var next = it.next();
            if (next == null) {
                writer.write("null");
            } else {
                delegate.write(next, context);
            }
            if (it.hasNext()) {
                writer.write(',');
            }
        }
        writer.write(']');
    }

}
