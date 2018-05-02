package com.ijunhai.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
    private static Properties properties = new Properties();

    static {
        try {
            properties.load(Properties.class.getClassLoader().getResourceAsStream("query-proxy.properties"));
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static String get(String key) {
        return (String) properties.get(key);
    }

    public static Integer getInt(String key) {
        try {
            return Integer.parseInt(get(key));//这里转换的时候，有可能出现类型转换异常，所以要抓一下异常
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Long getLong(String key) {
        try {
            return Long.parseLong(get(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Object get(String key, Object defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}
