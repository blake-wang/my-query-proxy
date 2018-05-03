package com.ijunhai.resource;


import com.ijunhai.model.ModelProcessor;
import com.ijunhai.model.QueryModel;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;

import static com.ijunhai.constant.ProxyConstants.JSON_MAPPER;

@Path("/")
public class QueryResource {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("query")
    public String queryPost(String modelStr) {
        try {
            QueryModel model = JSON_MAPPER.readValue(modelStr, QueryModel.class);
            ModelProcessor modelProcessor = new ModelProcessor(model);
//            modelProcessor.process();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("test")
    public String test() {
        return "server is ok";
    }

}
