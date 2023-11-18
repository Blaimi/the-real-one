package de.we2.am.therealone.web.resource;

import de.we2.am.therealone.web.to.status.StatusTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;

@Path("status")
@Produces(MediaType.APPLICATION_JSON)
public class StatusResource {

    @GET
    public Response getStatus() {
        List<String> authors = Arrays.asList("Alex", "Marvin");

        return Response.ok(new StatusTO(authors, "2.0.0")).build();
    }
}
