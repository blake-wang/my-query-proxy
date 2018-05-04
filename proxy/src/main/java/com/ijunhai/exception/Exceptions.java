package com.ijunhai.exception;

import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;

import javax.ws.rs.ext.Provider;

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

    public static class JsonFormatException extends AbstractException {

        public JsonFormatException(Throwable e) {
            super(e);
        }
    }

    @Provider
    public static class JsonFormatExceptionMapper extends AbstractExceptionMapper<JsonFormatException> {

        @Override
        protected ExceptionEnum getExceptionEnum() {
            return ExceptionEnum.JSON_ERROR;
        }
    }

}
