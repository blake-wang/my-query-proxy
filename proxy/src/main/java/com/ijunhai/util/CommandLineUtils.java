package com.ijunhai.util;

import jdk.nashorn.internal.runtime.ParserException;
import org.apache.commons.cli.*;

public class CommandLineUtils {

    private CommandLineUtils() {
    }

    //全局只有一个
    private final static CommandLineParser parser = new DefaultParser();

    public static CommandLine genCommandLine(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("p", "port", true, "service port");
        options.addOption("l", "logback", true, "logback config file");
        return parser.parse(options, args);
    }


}
