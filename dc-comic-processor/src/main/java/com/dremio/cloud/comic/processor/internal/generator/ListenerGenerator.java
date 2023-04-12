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
package com.dremio.cloud.comic.processor.internal.generator;

import com.dremio.cloud.comic.api.RuntimeContainer;
import com.dremio.cloud.comic.api.container.ComicListener;
import com.dremio.cloud.comic.processor.internal.Elements;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.util.function.Supplier;

public class ListenerGenerator extends BaseGenerator implements Supplier<BaseGenerator.GeneratedClass> {

    private final String packageName;
    private final String className;
    private final String suffix;
    private final String method;
    private final VariableElement param;
    private final Element enclosing;

    public ListenerGenerator(final ProcessingEnvironment processingEnv, final Elements elements,
                             final String packageName, final String className, final String suffix, final String method,
                             final VariableElement param, final Element enclosing) {
        super(processingEnv, elements);
        this.packageName = packageName;
        this.className = className;
        this.suffix = suffix;
        this.method = method;
        this.param = param;
        this.enclosing = enclosing;
    }

    @Override
    public GeneratedClass get() {
        final var priority = findPriority(param);
        final var eventType = param.asType().toString();

        final var out = new StringBuilder();
        if (!packageName.isBlank()) {
            out.append("package ").append(packageName).append(";\n\n");
        }

        appendGenerationVersion(out);
        out.append("public class ")
                .append(className).append(suffix)
                .append(" implements ").append(ComicListener.class.getName())
                .append('<').append(eventType).append("> {\n");
        out.append("  @Override\n");
        out.append("  public void onEvent(final ").append(RuntimeContainer.class.getName()).append(" container, final ").append(eventType).append(" event) {\n");
        out.append("    try (final var instance = container.lookup(").append(enclosing.asType().toString()).append(".class)) {\n");
        out.append("      instance.instance().").append(method).append("(event);\n");
        out.append("    }\n");
        out.append("  }\n");
        out.append("\n");
        out.append("  @Override\n");
        out.append("  public Class<").append(eventType).append("> eventType() {\n");
        out.append("    return ").append(eventType).append(".class;\n");
        out.append("  }\n");
        if (priority != 1000) {
            out.append("\n");
            out.append("  @Override\n");
            out.append("  public int priority() {\n");
            out.append("    return ").append(priority).append(";\n");
            out.append("  }\n");
        }
        out.append("}\n\n");

        return new GeneratedClass((!packageName.isBlank() ? packageName + '.' : "") + className + suffix, out.toString());
    }


}
