package de.we2.am.therealone.mapper;

import de.we2.am.therealone.dao.entity.RoomDO;
import de.we2.am.therealone.dao.repository.StoreyRepository;
import de.we2.am.therealone.web.to.room.RoomTO;
import jakarta.inject.Inject;

public class RoomMapper implements ObjectMapper<RoomTO, RoomDO> {

    @Inject
    private StoreyRepository storeyRepository;

    @Override
    public RoomDO convertTOtoDO(RoomTO transferObject) {
        RoomDO roomDO = new RoomDO();
        roomDO.setId(transferObject.id());
        roomDO.setName(transferObject.name());
        roomDO.setStoreyDO(storeyRepository.findById(transferObject.storeyId()).orElse(null));
        roomDO.setDeletedAt(transferObject.deletedAt());

        return roomDO;
    }

    @Override
    public RoomTO convertDOtoTO(RoomDO dataObject) {
        return new RoomTO(dataObject.getId(), dataObject.getName(), dataObject.getStoreyDO().getId(), dataObject.getDeletedAt());
    }
}
