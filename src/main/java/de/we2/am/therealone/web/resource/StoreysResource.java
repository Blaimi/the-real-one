package de.we2.am.therealone.web.resource;

import de.we2.am.therealone.manager.StoreyManager;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ConverterUtil;
import de.we2.am.therealone.web.request.storey.StoreyCreateRequest;
import de.we2.am.therealone.web.request.storey.StoreyPutRequest;
import de.we2.am.therealone.web.to.storey.StoreyTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.data.util.Pair;

import java.util.UUID;

@Path("storeys")
@Produces(MediaType.APPLICATION_JSON)
public class StoreysResource {

    @Context
    private SecurityContext securityContext;
    @Inject
    private StoreyManager storeyManager;

    @GET
    public Response getStoreys(@QueryParam("include_deleted") @DefaultValue("false") boolean includeDeleted, @QueryParam("building_id") @DefaultValue("") String buildingId) {
        UUID building;
        if ("".equals(buildingId)) {
            building = null;
        } else {
            building = ConverterUtil.convertId(buildingId, Constant.BUILDING_OBJECT_TYPE);
        }

        return Response.ok(storeyManager.getAll(building, includeDeleted)).build();
    }

    @POST
    @RolesAllowed({"Admin", "Storey-Create"})
    public Response postStorey(StoreyCreateRequest request) {
        return Response.status(Response.Status.CREATED).entity(storeyManager.create(securityContext, request)).build();
    }

    @GET
    @Path("{id}")
    public Response getStorey(@PathParam("id") String id) {
        return Response.ok(storeyManager.get(ConverterUtil.convertId(id, Constant.STOREY_OBJECT_TYPE))).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Admin", "Storey-Update"})
    public Response putStorey(@PathParam("id") String id, StoreyPutRequest request) {
        Pair<Response.Status, StoreyTO> pair = storeyManager.updateOrCreate(securityContext, ConverterUtil.convertId(id, Constant.STOREY_OBJECT_TYPE), request);
        return Response.status(pair.getFirst()).entity(pair.getSecond()).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Admin", "Storey-Delete"})
    public Response deleteStorey(@PathParam("id") String id) {
        storeyManager.delete(securityContext, ConverterUtil.convertId(id, Constant.STOREY_OBJECT_TYPE));
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
