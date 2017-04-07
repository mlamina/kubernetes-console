package com.github.mlamina.resources;

import com.github.mlamina.api.ExecuteCommandRequest;
import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.api.ParseCommandRequest;
import com.github.mlamina.api.ResponseError;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.CommandParser;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandResource {

    private final KubernetesClient client = new DefaultKubernetesClient();

    @POST
    @Path("/parse")
    public Response parse(@Valid ParseCommandRequest request) {
        CommandParser parser = new CommandParser(request.getCommand());
        return Response.ok(MetaResponse.success(parser.getTokens())).build();
    }

    @POST
    @Path("/execute")
    public Response execute(@Valid ExecuteCommandRequest request) {
        CommandParser parser = new CommandParser(request.getCommand());
        Optional<Command> commandOptional = parser.getCommand();
        if (commandOptional.isPresent())
            try {
                return Response.ok(MetaResponse.success(commandOptional.get().execute(client))).build();
            } catch (CommandParseException e) {
                return Response.ok(MetaResponse.error(new ResponseError(100, e.getMessage()))).build();
            }
        return Response.ok(MetaResponse.error(new ResponseError(101, "Input did not match any available command"))).build();
    }

}
