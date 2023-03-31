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

import com.dremio.cloud.comic.json.internal.parser.BufferProvider;
import com.dremio.cloud.comic.json.internal.parser.JsonParser;
import com.dremio.cloud.comic.json.serialization.JsonCodec;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapJsonCodecTest {

    @Test
    void mapPrimitive() throws IOException {
        final var codec = new MapJsonCodec<>(new IntegerJsonCodec());
        try (final var parser = parser("{\"k\":1,\"second\":22}")) {
            assertEquals(
                    Map.of("k", 1, "second", 22),
                    codec.read(new JsonCodec.DeserializationContext(parser, c -> null)));
        }
    }

    private JsonParser parser(final String string) {
        return new JsonParser(new StringReader(string), 16, new BufferProvider(16), true);
    }

}
