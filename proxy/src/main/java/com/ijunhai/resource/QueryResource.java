package com.ijunhai.resource;


import com.ijunhai.exception.Exceptions;
import com.ijunhai.model.ModelProcessor;
import com.ijunhai.model.QueryModel;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.ijunhai.constant.ProxyConstants.JSON_MAPPER;

@Path("/")
public class QueryResource {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("query")
    public String queryPost(String modelStr) throws Exception {
        try {
            //接收查询请求，将json格式的参数解析出来，生成QueryModel Bean
            QueryModel model = JSON_MAPPER.readValue(modelStr, QueryModel.class);
            //创建ModelProcessor对象，构造函数传入model对象
            ModelProcessor modelProcessor = new ModelProcessor(model);
            //执行查询
            List resultList = modelProcessor.process();
            //将resultList这个计算结果，生成一个json字符串返回
            return JSON_MAPPER.writeValueAsString(resultList);
        } catch (IOException e) {
            throw new Exceptions.JsonFormatException(e);
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("test")
    public String test() {
        return "server is ok";
    }

}
