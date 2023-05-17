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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class RestService {

  private static Logger logger = LoggerFactory.getLogger(RestService.class);

  @Context UriInfo uriInfo;

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    logger.info("hello");
    return "hello";
  }

  @GET
  @Path("/chain")
  @Produces(MediaType.TEXT_PLAIN)
  public String chain() {
    RestClient restClient =
        RestClientBuilder.newBuilder().baseUri(uriInfo.getBaseUri()).build(RestClient.class);
    return ("chain -> " + restClient.hello());
  }
}
