package com.github.mlamina.resources;

import com.github.mlamina.api.*;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.CommandParser;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;

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
        return Response.ok(MetaResponse.list(parser.getTokens(), MetaData.LIST_TYPE_TOKEN)).build();
    }

    @POST
    @Path("/execute")
    public Response execute(@Valid ExecuteCommandRequest request) {
        CommandParser parser = new CommandParser(request.getCommand());
        Optional<Command> commandOptional = parser.getCommand();
//        client.pods().load("").getLog()
        if (commandOptional.isPresent())
            try {
                return Response.ok(commandOptional.get().execute(client)).build();
            } catch (CommandParseException e) {
                return Response.ok(MetaResponse.error(new ResponseError(100, e.getMessage()))).build();
            }
        return Response.ok(MetaResponse.error(new ResponseError(101, "Input did not match any available command"))).build();
    }

}
