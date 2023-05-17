/*
 * Copyright © 2023 - Dremio - https://www.dremio.com
 *
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
package com.dremio.cloud.comics.examples.first;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Greeting {

  @Inject Tracer tracer;

  @ConfigProperty(name = "foo")
  String foo;

  private final Logger logger = LoggerFactory.getLogger(Greeting.class);

  public void startup(@Observes StartupEvent startupEvent) {
    Span span =
        tracer
            .spanBuilder("My custom span")
            .setAttribute("my.attr", "attr")
            .setParent(Context.current().with(Span.current()))
            .setSpanKind(SpanKind.INTERNAL)
            .startSpan();
    span.addEvent("Starting ...");
    logger.info("Hello world ! I'm " + foo);
    span.addEvent("I'm done");
    span.end();
  }

  @WithSpan
  public void shutdown(@Observes ShutdownEvent shutdownEvent) {
    logger.info("Bye bye world !");
  }
}
