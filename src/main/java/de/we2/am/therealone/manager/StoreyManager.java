package de.we2.am.therealone.manager;

import de.we2.am.therealone.dao.entity.StoreyDO;
import de.we2.am.therealone.dao.repository.StoreyRepository;
import de.we2.am.therealone.mapper.StoreyMapper;
import de.we2.am.therealone.web.to.storey.StoreyTO;
import de.we2.am.therealone.web.to.storey.StoreysTO;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class StoreyManager {

    private final StoreyMapper storeyMapper = new StoreyMapper();
    private final Logger logger = LogManager.getLogger(StoreyManager.class);
    @Inject
    private StoreyRepository storeyRepository;

    @Transactional
    public StoreysTO getAll(UUID buildingId, boolean includeDeleted) {
        if (buildingId == null) {
            return getAll(storeyRepository.findAll(), includeDeleted);
        }

        return getAll(storeyRepository.findAllByBuildingId(buildingId), includeDeleted);
    }

    private StoreysTO getAll(Iterable<StoreyDO> storeys, boolean includeDeleted) {
        List<StoreyTO> result = new ArrayList<>();
        for (StoreyDO storeyDO : storeys){
            if (!includeDeleted && storeyDO.getDeletedAt() != null) {
                continue;
            }

            result.add(storeyMapper.convertDOtoTO(storeyDO));
        }

        return new StoreysTO(result);
    }
}
