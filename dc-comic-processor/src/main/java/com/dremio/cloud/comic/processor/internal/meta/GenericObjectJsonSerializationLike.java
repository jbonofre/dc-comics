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

import com.dremio.cloud.comic.json.internal.codec.ObjectJsonCodec;
import com.dremio.cloud.comic.json.serialization.JsonCodec;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public interface GenericObjectJsonSerializationLike {

    Map<String, Object> asMap();

    default String toJson() {
        final var jsonCodec = new ObjectJsonCodec();
        final var writer = new StringWriter();
        try (writer) {
            jsonCodec.write(asMap(), new JsonCodec.SerializationContext(writer, k -> {
                throw new IllegalArgumentException("unsupported type: '" + k + "'");
            }));
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return writer.toString();
    }

}
