package com.ijunhai.util;

import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class LogBackConfigLoaderTest {

    @Test
    public void load() {
        try {
            LogBackConfigLoader.load("e:\\xxx.txt");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO出错啦");
            System.exit(0);
        } catch (JoranException e) {
//            e.printStackTrace();
            System.out.println("Jor出错啦");
            System.exit(0);
        }
    }
}