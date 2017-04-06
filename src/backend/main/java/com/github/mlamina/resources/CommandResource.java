package com.github.mlamina.resources;

import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.api.ParseCommandRequest;
import com.github.mlamina.kubernetes.CommandParser;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandResource {

    @POST
    @Path("/parse")
    public Response parse(@Valid ParseCommandRequest request) {
        CommandParser parser = new CommandParser(request.getCommand());
        return Response.ok(MetaResponse.success(parser.getTokens())).build();
    }

}
