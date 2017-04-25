package com.github.mlamina.resources;

import com.github.mlamina.api.*;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.CommandParser;
import com.github.mlamina.kubernetes.commands.CommandExecutionException;
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
        return Response.ok(MetaResponse.list(parser.getTokens(), MetaData.TYPE_TOKEN)).build();
    }

    @POST
    @Path("/execute")
    public Response execute(@Valid ExecuteCommandRequest request) {
        CommandParser parser = new CommandParser(request.getCommand());
        Optional<Command> commandOptional = parser.getCommand();
        if (commandOptional.isPresent())
            try {
                return Response.ok(commandOptional.get().execute(client)).build();
            } catch (CommandParseException e) {
                return Response.ok(MetaResponse.error(new ResponseError(ResponseError.CODE_COMMAND_PARSING_FAILED, e.getMessage()))).build();
            } catch (CommandExecutionException e) {
                return Response.ok(MetaResponse.error(new ResponseError(ResponseError.CODE_COMMAND_EXECUTION_FAILED, e.getMessage()))).build();
            }
        return Response.ok(MetaResponse.error(new ResponseError(ResponseError.CODE_COMMAND_NOT_FOUND, "Input did not match any available command"))).build();
    }

}
