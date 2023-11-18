package de.we2.am.therealone.web.resource;

import de.we2.am.therealone.manager.BuildingsManager;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ConverterUtil;
import de.we2.am.therealone.web.request.building.BuildingCreateRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("buildings")
@Produces(MediaType.APPLICATION_JSON)
public class BuildingsResource {

    @Context
    private SecurityContext securityContext;
    @Inject
    private BuildingsManager buildingsManager;

    @GET
    public Response getBuildings(@QueryParam("include_deleted") @DefaultValue("false") boolean includeDeleted) {
        return Response.ok(buildingsManager.getAll(includeDeleted)).build();
    }

    @POST
    @RolesAllowed({"Admin", "Building-Create"})
    public Response postBuilding(BuildingCreateRequest request) {
        return Response.status(Response.Status.CREATED).entity(buildingsManager.create(securityContext, request)).build();
    }

    @GET
    @Path("{id}")
    public Response getBuilding(@PathParam("id") String id) {
        return Response.ok(buildingsManager.get(ConverterUtil.convertId(id, Constant.BUILDING_OBJECT_TYPE))).build();
    }
}
