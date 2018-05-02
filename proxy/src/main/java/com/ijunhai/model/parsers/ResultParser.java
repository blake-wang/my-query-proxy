package com.ijunhai.model.parsers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultParser {
    private Map<String, List<Map<String, String>>> finalMetricMap;
    private Map<String, Map<String, String>> finalDimensionMap;
    private static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public ResultParser() {
        this.finalMetricMap = new HashMap<>();
        this.finalDimensionMap = new HashMap<>();
    }

}
