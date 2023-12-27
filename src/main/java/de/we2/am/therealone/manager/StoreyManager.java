package de.we2.am.therealone.manager;

import de.we2.am.therealone.dao.entity.BuildingDO;
import de.we2.am.therealone.dao.entity.StoreyDO;
import de.we2.am.therealone.dao.repository.BuildingRepository;
import de.we2.am.therealone.dao.repository.StoreyRepository;
import de.we2.am.therealone.exception.InvalidArgumentException;
import de.we2.am.therealone.exception.NoneDeletedObjectsException;
import de.we2.am.therealone.exception.NotFoundException;
import de.we2.am.therealone.mapper.StoreyMapper;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ConverterUtil;
import de.we2.am.therealone.web.request.building.BuildingPutRequest;
import de.we2.am.therealone.web.request.storey.StoreyCreateRequest;
import de.we2.am.therealone.web.request.storey.StoreyPutRequest;
import de.we2.am.therealone.web.to.room.RoomTO;
import de.we2.am.therealone.web.to.room.RoomsTO;
import de.we2.am.therealone.web.to.storey.StoreyTO;
import de.we2.am.therealone.web.to.storey.StoreysTO;
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
public class StoreyManager {

    private static final String NAME_PATTERN = "^([a-zA-ZäöüÄÖÜß0-9-.]+ )*([a-zA-ZäöüÄÖÜß0-9-.]+)$";
    private static final Predicate<String> NAME_TEST = Pattern.compile(NAME_PATTERN).asMatchPredicate();

    private final StoreyMapper storeyMapper = new StoreyMapper();
    private final Logger logger = LogManager.getLogger(StoreyManager.class);
    @Inject
    private StoreyRepository storeyRepository;
    @Inject
    private BuildingRepository buildingRepository;
    @Inject
    private RoomManager roomManager;

    @Transactional
    public StoreysTO getAll(UUID buildingId, boolean includeDeleted) {
        if (buildingId == null) {
            return getAll(storeyRepository.findAll(), includeDeleted);
        }

        return getAll(storeyRepository.findAllByBuildingId(buildingId), includeDeleted);
    }

    private StoreysTO getAll(Iterable<StoreyDO> storeys, boolean includeDeleted) {
        List<StoreyTO> result = new ArrayList<>();
        for (StoreyDO storeyDO : storeys) {
            if (!includeDeleted && storeyDO.getDeletedAt() != null) {
                continue;
            }

            result.add(storeyMapper.convertDOtoTO(storeyDO));
        }

        return new StoreysTO(result);
    }

    @Transactional
    public StoreyTO get(UUID id) {
        Optional<StoreyTO> storey = storeyRepository.findById(id).map(storeyMapper::convertDOtoTO);

        if (storey.isEmpty()) {
            throw new NotFoundException("Requested storey was not found", String.format("A storey with the id '%s' does not exist", id), Constant.STOREY_OBJECT_TYPE, id);
        }

        return storey.get();
    }

    @Transactional
    public StoreyTO create(SecurityContext securityContext, StoreyCreateRequest request) {
        validatedName(request.name());

        StoreyDO storeyDO = new StoreyDO();
        storeyDO.setId(UUID.randomUUID());
        storeyDO.setName(request.name());

        storeyDO.setBuildingDO(getAndValidatedBuilding(request.buildingId()));
        storeyRepository.save(storeyDO);

        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Created new storey")
                .with(Constant.KEY_OPERATION, "Create")
                .with(Constant.KEY_OBJECT_TYPE, Constant.STOREY_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, storeyDO.getId());

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        logger.info(log);

        return storeyMapper.convertDOtoTO(storeyDO);
    }

    @Transactional
    public Pair<Response.Status, StoreyTO> updateOrCreate(SecurityContext securityContext, UUID id, StoreyPutRequest request) {
        if (request.getDeletedAt() != null && !StoreyPutRequest.DEFAULT_DELETED_AT.equals(request.getDeletedAt())) {
            throw new InvalidArgumentException("Invalid argument deleted_at", String.format("deleted_at only supports null, got '%s'", request.getDeletedAt()), Constant.STOREY_OBJECT_TYPE, "deleted_at", request.getDeletedAt(), "null");
        }

        Optional<StoreyDO> optionalStorey = storeyRepository.findById(id);
        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_OBJECT_TYPE, Constant.STOREY_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, id);
        StoreyDO storeyDO;
        boolean created = false;
        if (optionalStorey.isEmpty()) {
            // Creating new storey
            storeyDO = new StoreyDO();
            storeyDO.setId(id);
            created = true;
            log.with(Constant.KEY_MESSAGE, "Create new storey via put request");
            log.with(Constant.KEY_OPERATION, "Create");
        } else {
            storeyDO = optionalStorey.get();
            log.with(Constant.KEY_MESSAGE, "Updated storey");
            log.with(Constant.KEY_OPERATION, "Updated");
        }

        boolean recover = request.getDeletedAt() == null;

        if (storeyDO.getDeletedAt() != null && !recover) {
            // Cannot change deleted storey without recovering it
            throw new InvalidArgumentException("Missing argument deleted_at", "Cannot change delete storey with out recovering it", Constant.STOREY_OBJECT_TYPE, "deleted_at", "", "null");
        } else if (storeyDO.getDeletedAt() == null && recover) {
            // Cannot recover a none deleted storey -> throw an error
            throw new InvalidArgumentException("Invalid argument deleted_at", String.format("Cannot recover none deleted storey '%s'", id), Constant.STOREY_OBJECT_TYPE, "deleted_at", String.valueOf(request.getDeletedAt()), "null");
        }

        if (recover) {
            storeyDO.setDeletedAt(null);
            log.with(Constant.KEY_MESSAGE, "Updated and recover storey");
        }

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        storeyDO.setName(request.getName());
        storeyDO.setBuildingDO(getAndValidatedBuilding(request.getBuildingId()));
        storeyRepository.save(storeyDO);

        logger.info(log);

        return Pair.of(created ? Response.Status.CREATED : Response.Status.OK, storeyMapper.convertDOtoTO(storeyDO));
    }

    @Transactional
    public void delete(SecurityContext securityContext, UUID id) {
        Optional<StoreyDO> optionalStorey = storeyRepository.findById(id);

        if (optionalStorey.isEmpty()) {
            throw new NotFoundException("Storey not found", String.format("The Storey with the id '%s' does not exist", id), Constant.STOREY_OBJECT_TYPE, id);
        }

        if (optionalStorey.map(StoreyDO::getDeletedAt).isPresent()) {
            throw new NotFoundException("Storey not found", String.format("The Storey with the id '%s' was already deleted", id), Constant.STOREY_OBJECT_TYPE, id);
        }

        StoreyDO storeyDO = optionalStorey.get();

        RoomsTO roomsTO = roomManager.getAll(storeyDO.getId(), false);
        if (!roomsTO.rooms().isEmpty()) {
            throw NoneDeletedObjectsException.create("There are active rooms", String.format("Cannot delete storey with id '%s' because it has none deleted rooms", id), Constant.STOREY_OBJECT_TYPE, id, Constant.ROOM_OBJECT_TYPE, RoomTO::id, roomsTO.rooms());
        }

        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Deleted storey")
                .with(Constant.KEY_OPERATION, "Delete")
                .with(Constant.KEY_OBJECT_TYPE, Constant.STOREY_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, storeyDO.getId());

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        logger.info(log);

        storeyDO.setDeletedAt(Instant.now());
        storeyRepository.save(storeyDO);
    }

    private BuildingDO getAndValidatedBuilding(String buildingId) {
        UUID buildingUUID = ConverterUtil.convertArgumentId(buildingId, Constant.STOREY_OBJECT_TYPE, "building_id");
        Optional<BuildingDO> buildingDO = buildingRepository.findById(buildingUUID);

        if (buildingDO.isEmpty()) {
            throw new InvalidArgumentException("Invalid argument building id", String.format("No building found with id '%s'", buildingId), Constant.STOREY_OBJECT_TYPE, "building_id", buildingId, null);
        }

        if (buildingDO.get().getDeletedAt() != null) {
            throw new InvalidArgumentException("Invalid argument building id", String.format("Cannot use deleted building '%s'", buildingId), Constant.STOREY_OBJECT_TYPE, "building_id", buildingId, null);
        }

        return buildingDO.get();
    }

    private void validatedName(String name) {
       if (name == null || !NAME_TEST.test(name)) {
            throw new InvalidArgumentException("Invalid argument name", String.format("'%s' does not match pattern '%s'", name, NAME_PATTERN), Constant.STOREY_OBJECT_TYPE, "name", name, NAME_PATTERN);
        }
    }
}
