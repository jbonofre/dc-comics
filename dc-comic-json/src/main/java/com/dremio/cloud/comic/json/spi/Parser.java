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
package com.dremio.cloud.comic.json.spi;

import java.math.BigDecimal;

public interface Parser extends AutoCloseable {

    Event[] EVT_MAP = Event.values();

    @Override
    default void close() {
        // no-op
    }

    boolean hasNext();

    void rewind(Event event);

    Event next();

    String getString();

    void enforceNext(Event event);

    boolean isInArray();

    boolean isInObject();

    void skipObject();

    void skipArray();

    int getInt();

    long getLong();

    double getDouble();

    BigDecimal getBigDecimal();

    enum Event {
        START_ARRAY,
        START_OBJECT,
        KEY_NAME,
        VALUE_STRING,
        VALUE_NUMBER,
        VALUE_TRUE,
        VALUE_FALSE,
        VALUE_NULL,
        END_OBJECT,
        END_ARRAY
    }

}
