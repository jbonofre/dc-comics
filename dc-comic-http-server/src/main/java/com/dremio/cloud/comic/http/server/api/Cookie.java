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
package com.dremio.cloud.comic.http.server.api;

import com.dremio.cloud.comic.http.server.impl.servlet.ServletCookie;

public interface Cookie extends Unwrappable {

    String name();

    String value();

    String path();

    String domain();

    int maxAge();

    boolean secure();

    boolean httpOnly();

    interface Builder {
        Builder name(String name);

        Builder value(String value);

        Builder path(String path);

        Builder domain(String domain);

        Builder maxAge(int max);

        Builder secure(boolean secure);

        Builder httpOnly(boolean httpOnly);

        default Builder secure() {
            return secure(true);
        }

        default Builder httpOnly() {
            return httpOnly(true);
        }

        Cookie build();
    }

    static Cookie.Builder of() {
        return new ServletCookie.BuilderImpl();
    }

}
