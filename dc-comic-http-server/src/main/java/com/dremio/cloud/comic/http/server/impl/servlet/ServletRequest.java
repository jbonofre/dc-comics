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
package com.dremio.cloud.comic.http.server.impl.servlet;

import com.dremio.cloud.comic.http.server.api.Cookie;
import com.dremio.cloud.comic.http.server.api.Request;
import com.dremio.cloud.comic.http.server.impl.flow.ServletInputStreamSubscription;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

import static java.util.Collections.list;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class ServletRequest implements Request {

    private final HttpServletRequest delegate;
    private String uri;
    private List<Cookie> cookies;

    public ServletRequest(final HttpServletRequest delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T unwrap(final Class<T> type) {
        if (type == HttpServletRequest.class || type == jakarta.servlet.ServletRequest.class) {
            return type.cast(delegate);
        }
        return Request.super.unwrap(type);
    }

    @Override
    public String scheme() {
        return delegate.getScheme();
    }

    @Override
    public String method() {
        return delegate.getMethod();
    }

    @Override
    public String path() {
        if (uri == null) {
            uri = delegate.getRequestURI().substring(delegate.getContextPath().length());
        }
        return uri;
    }

    @Override
    public String query() {
        return delegate.getQueryString();
    }

    @Override
    public Flow.Publisher<ByteBuffer> body() {
        // not yet the best reactive impl but likely as good as servlet
        return subscriber -> {
            try {
                subscriber.onSubscribe(new ServletInputStreamSubscription(delegate.getInputStream(), subscriber));
            } catch (final IOException e) {
                subscriber.onError(e);
            }
        };
    }

    @Override
    public Stream<Cookie> cookies() {
        if (cookies == null) {
            cookies = Stream.ofNullable(delegate.getCookies())
                    .flatMap(Stream::of)
                    .<Cookie>map(ServletCookie::new)
                    .toList();
        }
        return cookies.stream();
    }

    @Override
    public String parameter(final String name) {
        return delegate.getParameter(name);
    }

    @Override
    public Map<String, String[]> parameters() {
        return delegate.getParameterMap();
    }

    @Override
    public String header(final String name) {
        return delegate.getHeader(name);
    }

    @Override
    public Map<String, List<String>> headers() {
        return list(delegate.getHeaderNames()).stream()
                .collect(toMap(identity(), k -> list(delegate.getHeaders(k))));
    }

    @Override
    public <T> T attribute(final String key, final Class<T> type) {
        return type.cast(delegate.getAttribute(key));
    }

    @Override
    public <T> void setAttribute(final String key, final T value) {
        delegate.setAttribute(key, value);
    }

}
