package com.dremio.cloud.comics.maven;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
    name = "generate-code",
    defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    threadSafe = true)
public class GenerateCodeMojo extends io.quarkus.maven.GenerateCodeMojo {}
