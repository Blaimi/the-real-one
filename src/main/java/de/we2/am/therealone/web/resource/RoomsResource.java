package de.we2.am.therealone.web.resource;

import de.we2.am.therealone.manager.RoomManager;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ConverterUtil;
import de.we2.am.therealone.web.request.room.RoomCreateRequest;
import de.we2.am.therealone.web.request.room.RoomPutRequest;
import de.we2.am.therealone.web.to.room.RoomTO;
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

@Path("rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomsResource {

    @Context
    private SecurityContext securityContext;
    @Inject
    private RoomManager roomManager;

    @GET
    public Response getRooms(@QueryParam("include_deleted") @DefaultValue("false") boolean includeDeleted, @QueryParam("storey_id") @DefaultValue("") String storeyId) {
        UUID storey;
        if ("".equals(storeyId)) {
            storey = null;
        } else {
            storey = ConverterUtil.convertId(storeyId, Constant.STOREY_OBJECT_TYPE);
        }

        return Response.ok(roomManager.getAll(storey, includeDeleted)).build();
    }

    @POST
    @RolesAllowed({"Admin", "Room-Create"})
    public Response postRoom(RoomCreateRequest request) {
        return Response.status(Response.Status.CREATED).entity(roomManager.create(securityContext, request)).build();
    }

    @GET
    @Path("{id}")
    public Response getRoom(@PathParam("id") String id) {
        return Response.ok(roomManager.get(ConverterUtil.convertId(id, Constant.ROOM_OBJECT_TYPE))).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Admin", "Room-Update"})
    public Response putRoom(@PathParam("id") String id, RoomPutRequest request) {
        Pair<Response.Status, RoomTO> pair = roomManager.updateOrCreate(securityContext, ConverterUtil.convertId(id, Constant.STOREY_OBJECT_TYPE), request);
        return Response.status(pair.getFirst()).entity(pair.getSecond()).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Admin", "Room-Delete"})
    public Response deleteRoom(@PathParam("id") String id) {
        roomManager.delete(securityContext, ConverterUtil.convertId(id, Constant.ROOM_OBJECT_TYPE));
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
