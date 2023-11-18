package de.we2.am.therealone.mapper;

import de.we2.am.therealone.dao.entity.BuildingDO;
import de.we2.am.therealone.web.to.building.BuildingTO;

public class BuildingMapper implements ObjectMapper<BuildingTO, BuildingDO> {

    @Override
    public BuildingDO convertTOtoDO(BuildingTO transferObject) {
        BuildingDO buildingDO = new BuildingDO();
        buildingDO.setId(transferObject.id());
        buildingDO.setAddress(transferObject.address());
        buildingDO.setName(transferObject.name());
        buildingDO.setDeletedAt(transferObject.deletedAt());

        return buildingDO;
    }

    @Override
    public BuildingTO convertDOtoTO(BuildingDO dataObject) {
        return new BuildingTO(dataObject.getId(), dataObject.getAddress(), dataObject.getName(), dataObject.getDeletedAt());
    }
}
