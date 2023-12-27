package de.we2.am.therealone.dao.repository;

import de.we2.am.therealone.dao.entity.RoomDO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RoomRepository extends CrudRepository<RoomDO, UUID> {

    @Query("Select r from RoomDO r WHERE r.storeyDO.id = :storeyId")
    Iterable<RoomDO> findAllByStoreyId(@Param("storeyId") UUID storeyId);
}
