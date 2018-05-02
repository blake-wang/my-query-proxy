package com.ijunhai;

import ch.qos.logback.core.joran.spi.JoranException;
import com.ijunhai.exception.Exceptions;
import com.ijunhai.util.CommandLineUtils;
import com.ijunhai.util.LogBackConfigLoader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Proxy {

    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);

    public static void main(String[] args) throws ParseException, IOException, JoranException {
        CommandLine commandLine = CommandLineUtils.genCommandLine(args);

        if (commandLine.hasOption('l')) {
            String filePath = commandLine.getOptionValue('1');
            logger.info("logback file [{}]", filePath);
            LogBackConfigLoader.load(filePath);
        }

        int port = 8089;
        if (commandLine.hasOption('p')) {
            String portStr = commandLine.getOptionValue('p', "8089");
            port = Integer.parseInt(portStr);
        }
    }

    private void start(int port) {
        ResourceConfig config = new ResourceConfig();
        Exceptions.initExceptionMappers(config);
        config.packages("com.ijunhai.resource");

        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try {
            // {} 这个符号应该是个占位符
            logger.info("start proxy [port: {}] ...", port);
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }


    }
}
