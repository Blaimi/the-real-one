package de.we2.am.therealone.mapper;

import de.we2.am.therealone.dao.entity.StoreyDO;
import de.we2.am.therealone.dao.repository.BuildingRepository;
import de.we2.am.therealone.web.to.storey.StoreyTO;
import jakarta.inject.Inject;

public class StoreyMapper implements ObjectMapper<StoreyTO, StoreyDO> {

    @Inject
    private BuildingRepository buildingRepository;

    @Override
    public StoreyDO convertTOtoDO(StoreyTO transferObject) {
        StoreyDO storeyDO = new StoreyDO();
        storeyDO.setId(transferObject.id());
        storeyDO.setName(transferObject.name());
        storeyDO.setBuildingDO(buildingRepository.findById(transferObject.buildingId()).orElse(null));
        storeyDO.setDeletedAt(transferObject.deletedAt());

        return storeyDO;
    }

    @Override
    public StoreyTO convertDOtoTO(StoreyDO dataObject) {
        return new StoreyTO(dataObject.getId(), dataObject.getName(), dataObject.getBuildingDO().getId(), dataObject.getDeletedAt());
    }
}
