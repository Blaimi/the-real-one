package de.we2.am.therealone.manager;

import de.we2.am.therealone.dao.entity.BuildingDO;
import de.we2.am.therealone.dao.repository.BuildingRepository;
import de.we2.am.therealone.exception.InvalidArgumentException;
import de.we2.am.therealone.exception.NoneDeletedObjectsException;
import de.we2.am.therealone.exception.NotFoundException;
import de.we2.am.therealone.mapper.BuildingMapper;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.web.request.building.BuildingCreateRequest;
import de.we2.am.therealone.web.request.building.BuildingPutRequest;
import de.we2.am.therealone.web.to.building.BuildingTO;
import de.we2.am.therealone.web.to.building.BuildingsTO;
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
public class BuildingsManager {

    private static final String NAME_PATTERN = "^([a-zA-ZäöüÄÖÜß0-9-.]+ )*([a-zA-ZäöüÄÖÜß0-9-.]+)$";
    private static final Predicate<String> NAME_TEST = Pattern.compile(NAME_PATTERN).asMatchPredicate();

    private static final String ADDRESS_PATTERN = "^(([a-zA-ZäöüÄÖÜß0-9-.]+ )*([a-zA-ZäöüÄÖÜß0-9-.]*)\n?)*$";
    private static final Predicate<String> ADDRESS_TEST = Pattern.compile(ADDRESS_PATTERN).asMatchPredicate();
    private final BuildingMapper buildingMapper = new BuildingMapper();
    private final Logger logger = LogManager.getLogger(BuildingsManager.class);
    @Inject
    private BuildingRepository buildingRepository;
    @Inject
    private StoreyManager storeyManager;

    @Transactional
    public BuildingsTO getAll(boolean includeDeleted) {
        List<BuildingTO> result = new ArrayList<>();
        for (BuildingDO buildingDO : buildingRepository.findAll()) {
            if (!includeDeleted && buildingDO.getDeletedAt() != null) {
                continue;
            }

            result.add(buildingMapper.convertDOtoTO(buildingDO));
        }

        return new BuildingsTO(result);
    }

    @Transactional
    public BuildingTO get(UUID id) {
        Optional<BuildingTO> building = buildingRepository.findById(id).map(buildingMapper::convertDOtoTO);

        if (building.isEmpty()) {
            throw new NotFoundException("Requested building was not found", String.format("A building with the id '%s' does not exist", id), Constant.BUILDING_OBJECT_TYPE, id);
        }

        return building.get();
    }

    @Transactional
    public BuildingTO create(SecurityContext securityContext, BuildingCreateRequest request) {
        validatedName(request.name());
        validatedAddress(request.address());

        BuildingDO buildingDO = new BuildingDO();
        buildingDO.setId(UUID.randomUUID());
        buildingDO.setName(request.name());
        buildingDO.setAddress(request.address());

        buildingDO = buildingRepository.save(buildingDO);

        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Created new building")
                .with(Constant.KEY_OPERATION, "Create")
                .with(Constant.KEY_OBJECT_TYPE, Constant.BUILDING_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, buildingDO.getId());

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        logger.info(log);

        return buildingMapper.convertDOtoTO(buildingDO);
    }

    @Transactional
    public Pair<Response.Status, BuildingTO> updateOrCreate(SecurityContext securityContext, UUID id, BuildingPutRequest request) {
        if (request.getDeletedAt() != null && !BuildingPutRequest.DEFAULT_DELETED_AT.equals(request.getDeletedAt())) {
            throw new InvalidArgumentException("Invalid argument deleted_at", String.format("deleted_at only supports null, got '%s'", request.getDeletedAt()), Constant.BUILDING_OBJECT_TYPE, "deleted_at", request.getDeletedAt(), "null");
        }

        validatedName(request.getName());
        validatedAddress(request.getAddress());

        Optional<BuildingDO> optionalBuilding = buildingRepository.findById(id);
        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_OBJECT_TYPE, Constant.BUILDING_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, id);
        BuildingDO buildingDO;
        boolean created = false;
        if (optionalBuilding.isEmpty()) {
            // Creating new building
            buildingDO = new BuildingDO();
            buildingDO.setId(id);
            created = true;
            log.with(Constant.KEY_MESSAGE, "Create new building via put request");
            log.with(Constant.KEY_OPERATION, "Create");
        } else {
            buildingDO = optionalBuilding.get();
            log.with(Constant.KEY_MESSAGE, "Updated building");
            log.with(Constant.KEY_OPERATION, "Updated");
        }

        boolean recover = request.getDeletedAt() == null;

        if (buildingDO.getDeletedAt() != null && !recover) {
            // Cannot change deleted building without recovering it
            throw new InvalidArgumentException("Missing argument deleted_at", "Cannot change delete building with out recovering it", Constant.BUILDING_OBJECT_TYPE, "deleted_at", "", "null");
        } else if (buildingDO.getDeletedAt() == null && recover) {
            // Cannot recover a none deleted building -> throw an error
            throw new InvalidArgumentException("Invalid argument deleted_at", String.format("Cannot recover none deleted building '%s'", id), Constant.BUILDING_OBJECT_TYPE, "deleted_at", String.valueOf(request.getDeletedAt()), "null");
        }

        if (recover) {
            buildingDO.setDeletedAt(null);
            log.with(Constant.KEY_MESSAGE, "Updated and recover building");
        }

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        logger.info(log);

        buildingDO.setName(request.getName());
        buildingDO.setAddress(request.getAddress());
        buildingRepository.save(buildingDO);

        return Pair.of(created ? Response.Status.CREATED : Response.Status.OK, buildingMapper.convertDOtoTO(buildingDO));
    }

    @Transactional
    public void delete(SecurityContext securityContext, UUID id) {
        Optional<BuildingDO> optionalBuilding = buildingRepository.findById(id);

        if (optionalBuilding.isEmpty()) {
            throw new NotFoundException("Building not found", String.format("The Building with the id '%s' does not exist", id), Constant.BUILDING_OBJECT_TYPE, id);
        }

        if (optionalBuilding.map(BuildingDO::getDeletedAt).isPresent()) {
            throw new NotFoundException("Building not found", String.format("The Building with the id '%s' was already deleted", id), Constant.BUILDING_OBJECT_TYPE, id);
        }

        BuildingDO buildingDO = optionalBuilding.get();

        StoreysTO storeysTO = storeyManager.getAll(buildingDO.getId(), false);
        if (!storeysTO.storeys().isEmpty()) {
            throw NoneDeletedObjectsException.create("There are active storeys", String.format("Cannot delete building with id '%s' because it has none deleted storeys", id), Constant.BUILDING_OBJECT_TYPE, id, Constant.STOREY_OBJECT_TYPE, StoreyTO::id, storeysTO.storeys());
        }

        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Deleted building")
                .with(Constant.KEY_OPERATION, "Delete")
                .with(Constant.KEY_OBJECT_TYPE, Constant.BUILDING_OBJECT_TYPE)
                .with(Constant.KEY_OBJECT_ID, buildingDO.getId());

        if (securityContext.getUserPrincipal() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        logger.info(log);

        buildingDO.setDeletedAt(Instant.now());
        buildingRepository.save(buildingDO);
    }

    private void validatedName(String name) {
        if (name == null || !NAME_TEST.test(name)) {
            throw new InvalidArgumentException("Invalid argument name", String.format("'%s' does not match pattern '%s'", name, NAME_PATTERN), Constant.BUILDING_OBJECT_TYPE, "name", name, NAME_PATTERN);
        }
    }

    private void validatedAddress(String address) {
        if (address == null || !ADDRESS_TEST.test(address)) {
            throw new InvalidArgumentException("Invalid argument address", String.format("'%s' does not match pattern '%s'", address, ADDRESS_PATTERN), Constant.BUILDING_OBJECT_TYPE, "address", address, ADDRESS_PATTERN);
        }
    }
}
