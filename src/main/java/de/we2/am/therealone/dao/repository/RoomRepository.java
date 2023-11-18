package de.we2.am.therealone.dao.repository;

import de.we2.am.therealone.dao.entity.RoomDO;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RoomRepository extends CrudRepository<RoomDO, UUID> {
}
