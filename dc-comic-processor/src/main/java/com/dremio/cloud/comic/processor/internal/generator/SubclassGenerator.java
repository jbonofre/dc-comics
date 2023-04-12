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

import com.dremio.cloud.comic.api.container.context.subclass.DelegatingContext;
import com.dremio.cloud.comic.processor.internal.Elements;
import com.dremio.cloud.comic.processor.internal.ParsedType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class SubclassGenerator extends BaseGenerator implements Supplier<BaseGenerator.GeneratedClass> {

    private static final String DELEGATING_CLASS_SUFFIX = "$ComicSubclass";

    private final String packageName;
    private final String className;
    private final TypeElement typeElement;

    public SubclassGenerator(final ProcessingEnvironment processingEnvironment,
                             final Elements elements,
                             final String packageName, final String className, final TypeElement te) {
        super(processingEnvironment, elements);
        this.packageName = packageName;
        this.className = className;
        this.typeElement = te;
    }

    @Override
    public GeneratedClass get() {
        final boolean useConstructorSuperNull = findNoArgConstructor().isEmpty();

        final var out = new StringBuilder();
        if (!packageName.isBlank()) {
            out.append("package ").append(packageName).append(";\n\n");
        }

        appendGenerationVersion(out);
        out.append("class ").append(className).append(DELEGATING_CLASS_SUFFIX).append(" extends ").append(className.replace('$', '.')).append(" {\n");
        out.append("  private final ").append(DelegatingContext.class.getName()).append("<").append(className.replace('$', '.')).append("> fusionContext;\n");
        out.append("\n");
        out.append("  ").append(className).append(DELEGATING_CLASS_SUFFIX)
                .append("(final ").append(DelegatingContext.class.getName()).append("<").append(className.replace('$', '.')).append("> context) {\n");
        if (useConstructorSuperNull) {
            out.append("    super(")
                    .append(selectConstructor(typeElement)
                            .map(it -> it.getParameters().stream()
                                    .map(p -> {
                                        try {
                                            final var type = ParsedType.of(p.asType());
                                            if (type.type() != ParsedType.Type.CLASS) {
                                                return "null";
                                            }
                                            return switch (type.className()) {
                                                case "boolean" -> "false";
                                                case "double" -> "0.";
                                                case "float" -> "0.f";
                                                case "long" -> "0L";
                                                case "integer" -> "0";
                                                case "short" -> "(short) 0";
                                                case "byte" -> "(byte) 0";
                                                default -> "null";
                                            };
                                        } catch (final RuntimeException re) {
                                            return "null";
                                        }
                                    })
                                    .collect(joining(", ")))
                            .orElse(""))
                    .append(");\n");
        }
        out.append("    this.fusionContext = context;\n");
        out.append("  }\n");
        out.append(elements.findMethods(typeElement)
                .filter(m -> {
                    final var modifiers = m.getModifiers();
                    final var name = m.getSimpleName().toString();
                    return !"<cinit>".equals(name) &&
                            !"<init>".equals(name) && // not the constructor - for now at least
                            !modifiers.contains(FINAL) &&
                            !modifiers.contains(STATIC) &&
                            !modifiers.contains(ABSTRACT) &&
                            !modifiers.contains(DEFAULT) /*more for later for interfaces*/ &&
                            // other methods can't be subclasses/overriden like this
                            (modifiers.contains(PUBLIC) || modifiers.contains(PROTECTED));
                })
                .map(m -> {
                    // note: if we start supporting interceptors,
                    //       then we would enhance the context API with a shouldIntercept("methodName"[, signature]) or alike
                    final var methodName = m.getSimpleName().toString();
                    final var args = m.getParameters().isEmpty() ?
                            "" :
                            m.getParameters().stream()
                                    .map(VariableElement::getSimpleName)
                                    .map(Name::toString)
                                    .collect(joining(", "));
                    return "@Override\n" +
                            visibilityFrom(m.getModifiers()) +
                            templateTypes(m) +
                            m.getReturnType() + " " + methodName + "(" +
                            m.getParameters().stream().map(p -> p.asType() + " " + p.getSimpleName()).collect(joining(", ")) +
                            ")" + exceptions(m) + " {\n" +
                            (m.getReturnType().getKind() == TypeKind.VOID ?
                                    "  this.fusionContext.instance()." + methodName + "(" + args + ");\n" :
                                    "  return this.fusionContext.instance()." + methodName + "(" + args + ");\n") +
                            "}\n";
                })
                .map(it -> it.indent(2))
                .collect(joining("\n", "\n", "")));
        out.append("}\n\n");

        return new GeneratedClass((packageName.isBlank() ? "" : (packageName + '.')) + className + DELEGATING_CLASS_SUFFIX, out.toString());
    }

    private Optional<ExecutableElement> findNoArgConstructor() {
        return findConstructors(typeElement)
                .filter(it -> it.getModifiers().contains(PROTECTED) || it.getModifiers().contains(PUBLIC))
                .filter(it -> it.getParameters().isEmpty())
                .findAny();
    }

}
