package com.ijunhai.exception;

import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;

public class Exceptions {
    public static void initExceptionMappers(ResourceConfig resourceConfig) {
        Reflections reflections = new Reflections(AbstractExceptionMapper.class.getPackage().getName());
        reflections.getSubTypesOf(AbstractExceptionMapper.class).forEach(clz -> resourceConfig.register(clz));

    }

    public enum ExceptionEnum {
        SERVER_ERROR(9999, "server error"),
        JSON_ERROR(10000, "json format error"),
        START_TIME_LOSS(10001, "loss start time"),
        DEMENSIONS_TYPE(10002, "returnDemessions cannot contain day/hour/minutes");

        private int code;
        private String error;

        ExceptionEnum(int code, String error) {
            this.code = code;
            this.error = error;
        }

        public int getCode() {
            return code;
        }

        public String getError() {
            return error;
        }
    }
}
