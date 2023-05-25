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
package com.dremio.cloud.comics.maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
    name = "build",
    defaultPhase = LifecyclePhase.PACKAGE,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    threadSafe = true)
public class BuildMojo extends io.quarkus.maven.BuildMojo {

  @Parameter(defaultValue = "false", property = "dc.comics.rename")
  boolean renaming = false;

  @Override
  public void doExecute() throws MojoExecutionException {
    super.doExecute();

    if (renaming) {
      try {
        String buildDirectory = mavenProject().getBuild().getDirectory();

        Path quarkusDirectory = Paths.get(buildDirectory, "quarkus-app");
        Path dcComicsDirectory = Paths.get(buildDirectory, "dc-comics-app");

        Files.move(quarkusDirectory, dcComicsDirectory, StandardCopyOption.REPLACE_EXISTING);

        Files.move(
            dcComicsDirectory.resolve("quarkus-run.jar"),
            dcComicsDirectory.resolve("dc-comics-run.jar"),
            StandardCopyOption.REPLACE_EXISTING);

      } catch (IOException ioException) {
        throw new MojoExecutionException(ioException);
      }
    }
  }
}
