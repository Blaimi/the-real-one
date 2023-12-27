package de.we2.am.therealone.manager;

import de.we2.am.therealone.dao.entity.RoomDO;
import de.we2.am.therealone.dao.entity.StoreyDO;
import de.we2.am.therealone.dao.repository.RoomRepository;
import de.we2.am.therealone.dao.repository.StoreyRepository;
import de.we2.am.therealone.exception.InvalidArgumentException;
import de.we2.am.therealone.exception.NotFoundException;
import de.we2.am.therealone.mapper.RoomMapper;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ConverterUtil;
import de.we2.am.therealone.web.request.room.RoomCreateRequest;
import de.we2.am.therealone.web.request.room.RoomPutRequest;
import de.we2.am.therealone.web.to.room.RoomTO;
import de.we2.am.therealone.web.to.room.RoomsTO;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class RoomManager {

    private static final String NAME_PATTERN = "^([a-zA-ZäöüÄÖÜß0-9-.]+ )*([a-zA-ZäöüÄÖÜß0-9-.]+)$";
    private static final Predicate<String> NAME_TEST = Pattern.compile(NAME_PATTERN).asMatchPredicate();

    private final RoomMapper roomMapper = new RoomMapper();
    private final Logger logger = LogManager.getLogger(RoomManager.class);
    @Inject
    private RoomRepository roomRepository;
    @Inject
    private StoreyRepository storeyRepository;

    @Transactional
    public RoomsTO getAll(UUID storeyId, boolean includeDeleted) {
        if (storeyId == null) {
            return getAll(roomRepository.findAll(), includeDeleted);
        }

        return getAll(roomRepository.findAllByStoreyId(storeyId), includeDeleted);
    }

    private RoomsTO getAll(Iterable<RoomDO> rooms, boolean includeDeleted) {
        List<RoomTO> result = new ArrayList<>();
        for (RoomDO roomDO : rooms) {
            if (!includeDeleted && roomDO.getDeletedAt() != null) {
                continue;
            }

            result.add(roomMapper.convertDOtoTO(roomDO));
        }

        return new RoomsTO(result);
    }

    @Transactional
    public RoomTO get(UUID id) {
        Optional<RoomTO> room = roomRepository.findById(id).map(roomMapper::convertDOtoTO);

        if (room.isEmpty()) {
            throw new NotFoundException("Requested room was not found", String.format("A room with the id '%s' does not exist", id), Constant.ROOM_OBJECT_TYPE, id);
        }

        return room.get();
    }

    @Transactional
    public RoomTO create(SecurityContext securityContext, RoomCreateRequest request) {
        validatedName(request.name());

        RoomDO roomDO = new RoomDO();
        roomDO.setId(UUID.randomUUID());
        roomDO.setName(request.name());

        roomDO.setStoreyDO(getAndValidatedStorey(request.storeyId()));
        roomRepository.save(roomDO);

        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Created new room")
                .with(Constant.KEY_OPERATION, "Create")
                .with(Constant.KEY_OBJECT_TYPE, Constant.ROOM_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, roomDO.getId());

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        logger.info(log);

        return roomMapper.convertDOtoTO(roomDO);
    }

    @Transactional
    public Pair<Response.Status, RoomTO> updateOrCreate(SecurityContext securityContext, UUID id, RoomPutRequest request) {
        if (request.getDeletedAt() != null && !RoomPutRequest.DEFAULT_DELETED_AT.equals(request.getDeletedAt())) {
            throw new InvalidArgumentException("Invalid argument deleted_at", String.format("deleted_at only supports null, got '%s'", request.getDeletedAt()), Constant.ROOM_OBJECT_TYPE, "deleted_at", request.getDeletedAt(), "null");
        }

        Optional<RoomDO> optionalRoom = roomRepository.findById(id);
        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_OBJECT_TYPE, Constant.ROOM_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, id);
        RoomDO roomDO;
        boolean created = false;
        if (optionalRoom.isEmpty()) {
            // Creating new room
            roomDO = new RoomDO();
            roomDO.setId(id);
            created = true;
            log.with(Constant.KEY_MESSAGE, "Create new room via put request");
            log.with(Constant.KEY_OPERATION, "Create");
        } else {
            roomDO = optionalRoom.get();
            log.with(Constant.KEY_MESSAGE, "Updated room");
            log.with(Constant.KEY_OPERATION, "Updated");
        }

        boolean recover = request.getDeletedAt() == null;

        if (roomDO.getDeletedAt() != null && !recover) {
            // Cannot change deleted room without recovering it
            throw new InvalidArgumentException("Missing argument deleted_at", "Cannot change delete room with out recovering it", Constant.ROOM_OBJECT_TYPE, "deleted_at", "", "null");
        } else if (roomDO.getDeletedAt() == null && recover) {
            // Cannot recover a none deleted room -> throw an error
            throw new InvalidArgumentException("Invalid argument deleted_at", String.format("Cannot recover none deleted room '%s'", id), Constant.ROOM_OBJECT_TYPE, "deleted_at", String.valueOf(request.getDeletedAt()), "null");
        }

        if (recover) {
            roomDO.setDeletedAt(null);
            log.with(Constant.KEY_MESSAGE, "Updated and recover room");
        }

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        roomDO.setName(request.getName());
        roomDO.setStoreyDO(getAndValidatedStorey(request.getStoreyId()));
        roomRepository.save(roomDO);

        logger.info(log);

        return Pair.of(created ? Response.Status.CREATED : Response.Status.OK, roomMapper.convertDOtoTO(roomDO));
    }

    @Transactional
    public void delete(SecurityContext securityContext, UUID id) {
        Optional<RoomDO> optionalRoom = roomRepository.findById(id);

        if (optionalRoom.isEmpty()) {
            throw new NotFoundException("Room not found", String.format("The Room with the id '%s' does not exist", id), Constant.ROOM_OBJECT_TYPE, id);
        }

        if (optionalRoom.map(RoomDO::getDeletedAt).isPresent()) {
            throw new NotFoundException("Room not found", String.format("The Room with the id '%s' was already deleted", id), Constant.ROOM_OBJECT_TYPE, id);
        }

        RoomDO roomDO = optionalRoom.get();

        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Deleted room")
                .with(Constant.KEY_OPERATION, "Delete")
                .with(Constant.KEY_OBJECT_TYPE, Constant.ROOM_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, roomDO.getId());

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        logger.info(log);

        roomDO.setDeletedAt(Instant.now());
        roomRepository.save(roomDO);
    }

    private StoreyDO getAndValidatedStorey(String storeyId) {
        UUID storeyUUID = ConverterUtil.convertArgumentId(storeyId, Constant.ROOM_OBJECT_TYPE, "storey_id");
        Optional<StoreyDO> storeyDO = storeyRepository.findById(storeyUUID);

        if (storeyDO.isEmpty()) {
            throw new InvalidArgumentException("Invalid argument storey id", String.format("No storey found with id '%s'", storeyId), Constant.ROOM_OBJECT_TYPE, "storey_id", storeyId, null);
        }

        if (storeyDO.get().getDeletedAt() != null) {
            throw new InvalidArgumentException("Invalid argument storey id", String.format("Cannot use deleted storey '%s'", storeyId), Constant.ROOM_OBJECT_TYPE, "storey_id", storeyId, null);
        }

        return storeyDO.get();
    }

    private void validatedName(String name) {
        if (name == null || !NAME_TEST.test(name)) {
            throw new InvalidArgumentException("Invalid argument name", String.format("'%s' does not match pattern '%s'", name, NAME_PATTERN), Constant.ROOM_OBJECT_TYPE, "name", name, NAME_PATTERN);
        }
    }
}
