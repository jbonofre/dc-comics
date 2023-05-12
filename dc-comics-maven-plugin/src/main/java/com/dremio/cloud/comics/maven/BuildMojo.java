package com.dremio.cloud.comics.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Mojo(name = "build", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class BuildMojo extends io.quarkus.maven.BuildMojo {

    @Parameter(defaultValue = "false", property = "dc.comics.rename.skip")
    boolean skipDcComicsRename = false;

    @Override
    public void doExecute() throws MojoExecutionException {
        super.doExecute();

        if (!skipDcComicsRename) {
            try {
                String buildDirectory = mavenProject().getBuild().getDirectory();

                Path quarkusDirectory = Paths.get(buildDirectory, "quarkus-app");
                Path dcComicsDirectory = Paths.get(buildDirectory, "dc-comics-app");

                Files.move(quarkusDirectory, dcComicsDirectory, StandardCopyOption.REPLACE_EXISTING);

                Files.move(dcComicsDirectory.resolve("quarkus-run.jar"), dcComicsDirectory.resolve("dc-comics-run.jar"), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException ioException) {
                throw new MojoExecutionException(ioException);
            }
        }
    }

}
