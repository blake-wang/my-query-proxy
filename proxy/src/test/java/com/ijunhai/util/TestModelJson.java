package com.ijunhai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijunhai.model.QueryModel;

import java.io.IOException;

public class TestModelJson {
    public final static ObjectMapper JSON_MAPPER = new ObjectMapper();
    public static void main(String[] args) throws IOException {
        String requestJson = "{\"conditions\":{\"osType\":[],\"gameId\":[],\"start\":\"2018-04-25\",\"end\":\"2018-05-01\"},\"orderByFields\":[\"day desc\"],\"granularity\":\"day\",\"limit\":\"\",\"returnDemensions\":[],\"metrics\":[{\"name\":\"active_nuv\"},{\"name\":\"active_uv\"},{\"name\":\"active_ouv\"},{\"name\":\"pay_uv\"},{\"name\":\"pay_amount\"},{\"name\":\"pay_nuv\"},{\"name\":\"nu_pay_amount\"},{\"name\":\"first_pay_uv\"},{\"name\":\"first_pay_amount\"}]}";

        QueryModel queryModel = JSON_MAPPER.readValue(requestJson, QueryModel.class);
        System.out.println(queryModel);


    }
}
