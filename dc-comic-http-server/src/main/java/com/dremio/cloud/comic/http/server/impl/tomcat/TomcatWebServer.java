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
package com.dremio.cloud.comic.http.server.impl.tomcat;

import com.dremio.cloud.comic.http.server.api.WebServer;
import com.dremio.cloud.comic.http.server.impl.servlet.ComicServlet;
import jakarta.servlet.annotation.HandlesTypes;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.coyote.AbstractProtocol;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.Registry;

import java.io.CharArrayWriter;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.Optional.ofNullable;

// inspired from @apache/openwebbeans
public class TomcatWebServer implements WebServer {

    protected final TomcatWebServerConfiguration configuration;
    protected final Tomcat tomcat;

    public TomcatWebServer(final TomcatWebServerConfiguration configuration) {
        this.configuration = configuration;

        if (configuration.isDisableRegistry()) {
            Registry.disableRegistry();
        }

        tomcat = createTomcat();

        final var context = createContext();
        tomcat.getHost().addChild(context);

        final var state = context.getState();
        if (state == LifecycleState.STOPPED || state == LifecycleState.FAILED) {
            try {
                close();
            } catch (final RuntimeException re) {
                // no-op
            }
            throw new IllegalStateException("Context didn't start");
        }
        if (configuration.getPort() == 0) {
            configuration.setPort(getPort());
        }
    }

    public Tomcat tomcat() {
        return tomcat;
    }

    public int getPort() {
        return ((AbstractProtocol<?>) tomcat.getConnector().getProtocolHandler()).getLocalPort();
    }

    @Override
    public synchronized void close() {
        if (tomcat == null) {
            return;
        }
        try {
            final var server = tomcat.getServer();
            tomcat.stop();
            tomcat.destroy();
            if (server != null) { // give a change to stop the utility executor otherwise it just leaks and stop later
                final var utilityExecutor = server.getUtilityExecutor();
                if (utilityExecutor != null) {
                    try {
                        if (!utilityExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
                            LogFactory.getLog(getClass()).warn("Can't stop tomcat utility executor in 1mn, giving up");
                        }
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (final LifecycleException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public void await() {
        tomcat.getServer().await();
    }

    protected Tomcat createTomcat() {
        final var tomcat = newTomcat();
        tomcat.setBaseDir(configuration.getBase());
        tomcat.setPort(configuration.getPort());

        final var host = new StandardHost();
        host.setAutoDeploy(false);
        // note needed to stick to tomcat but neat to enable in customizers: host.setFailCtxIfServletStartFails(true);
        host.setName(configuration.getDefaultHost());
        tomcat.getEngine().addChild(host);

        if (configuration.getCompression() != null && !configuration.getCompression().isBlank()) {
            tomcat.getConnector().setProperty("compression", configuration.getCompression());
        }

        if (configuration.getTomcatCustomizers() != null) {
            configuration.getTomcatCustomizers().forEach(c -> c.accept(tomcat));
        }
        onTomcat(tomcat);

        try {
            tomcat.init();
        } catch (final LifecycleException e) {
            try {
                tomcat.destroy();
            } catch (final LifecycleException ex) {
                // no-op
            }
            throw new IllegalStateException(e);
        }
        try {
            tomcat.start();
        } catch (final LifecycleException e) {
            close();
            throw new IllegalStateException(e);
        }
        return tomcat;
    }

    protected StandardContext createContext() {
        final var ctx = createBaseContext(newContext(), configuration);

        if (!configuration.getEndpoints().isEmpty() &&
                configuration.getComicServletMapping() != null &&
                !"-".equals(configuration.getComicServletMapping())) {
            ctx.addServletContainerInitializer((ignored, servletContext) -> {
                final var comic = servletContext.addServlet("comic", new ComicServlet(configuration.getEndpoints()));
                comic.setAsyncSupported(true);
                comic.setLoadOnStartup(1);
                comic.addMapping(configuration.getComicServletMapping());
            }, Set.of());
        }

        configuration.getInitializers().forEach(sci -> ctx.addServletContainerInitializer(
                sci, ofNullable(sci.getClass().getAnnotation(HandlesTypes.class))
                        .map(HandlesTypes::value)
                        .map(this::scanFor)
                        .orElseGet(Set::of)));

        if (configuration.isFastSessionId()) {
            final var mgr = new StandardManager();
            mgr.setSessionIdGenerator(new FastSessionIdGenerator());
            ctx.setManager(mgr);
        }

        if (configuration.getContextCustomizers() != null) {
            configuration.getContextCustomizers().forEach(c -> c.accept(ctx));
        }

        onContext(ctx);

        return ctx;
    }

    public static StandardContext createBaseContext(final StandardContext ctx,
                                                    final TomcatWebServerConfiguration configuration) {
        ctx.setLoader(new LaunchingClassLoaderLoader());
        ctx.setPath("");
        ctx.setName("");
        ctx.setFailCtxIfServletStartFails(true);
        // ctx.setJarScanner(newSkipScanner()); // we don't use scanning at all with this setup so just ignore useless optims for now

        if (!configuration.isSkipUtf8Setup()) {
            ctx.setRequestCharacterEncoding("UTF-8");
            ctx.setResponseCharacterEncoding("UTF-8");
        }

        ctx.addLifecycleListener(new Tomcat.FixContextListener());

        final var errorReportValve = new ErrorReportValve();
        errorReportValve.setShowReport(false);
        errorReportValve.setShowServerInfo(false);

        if (configuration.getAccessLogPattern() != null && !configuration.getAccessLogPattern().isBlank()) {
            final var logValve = new JULAccessLogValve();
            logValve.setPattern(configuration.getAccessLogPattern());
            if (configuration.getSkipAccessLogAttribute() != null && !configuration.getSkipAccessLogAttribute().isBlank()) {
                logValve.setCondition(configuration.getSkipAccessLogAttribute());
            }
            ctx.getPipeline().addValve(logValve);
        }

        ctx.getPipeline().addValve(errorReportValve);

        // no need of all these checks in general since we use a flat classpath
        ctx.setClearReferencesObjectStreamClassCaches(false);
        ctx.setClearReferencesThreadLocals(false);
        ctx.setClearReferencesRmiTargets(false);
        ctx.setClearReferencesHttpClientKeepAliveThread(false);
        ctx.setClearReferencesStopThreads(false);
        ctx.setClearReferencesStopTimerThreads(false);
        ctx.setSkipMemoryLeakChecksOnJvmShutdown(true);

        return ctx;
    }

    protected Tomcat newTomcat() {
        return new NoBaseDirTomcat();
    }

    protected StandardContext newContext() {
        return new NoWorkDirContext();
    }

    protected void onTomcat(final Tomcat tomcat) {
        // no-op
    }

    protected void onContext(final StandardContext context) {
        // no-op
    }

    // default does not scan anything but can be overriden if relevant
    protected Set<Class<?>> scanFor(final Class<?>... classes) {
        return Set.of();
    }

    public static class JULAccessLogValve extends AbstractAccessLogValve {
        private final Logger logger = Logger.getLogger("comic.webserver.tomcat.access.log");

        @Override
        protected void log(final CharArrayWriter message) {
            logger.info(message.toString());
        }
    }

    public static class NoBaseDirTomcat extends Tomcat {
        @Override
        protected void initBaseDir() {
            // no-op
        }
    }

    public static class NoWorkDirContext extends StandardContext {
        @Override
        protected void postWorkDirectory() {
            // no-op
        }
    }

    public static class FastSessionIdGenerator extends StandardSessionIdGenerator {
        @Override
        protected void getRandomBytes(final byte[] bytes) {
            ThreadLocalRandom.current().nextBytes(bytes);
        }
    }

}
