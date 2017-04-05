package com.github.mlamina.resources;

import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.api.ParseCommandRequest;
import com.github.mlamina.api.ParseCommandResponse;
import com.google.common.collect.Lists;

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
    public Response add(@Valid ParseCommandRequest request) {
        // TODO: Verify command syntax
        // TODO: Figure out autocompletion for command
        ParseCommandResponse response = new ParseCommandResponse();
        response.setValid(true);
        response.setPossibleCompletions(Lists.newArrayList());
        return Response.ok(MetaResponse.success(response)).build();
    }

}
