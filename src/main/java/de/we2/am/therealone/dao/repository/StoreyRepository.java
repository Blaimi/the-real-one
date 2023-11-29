package de.we2.am.therealone.dao.repository;

import de.we2.am.therealone.dao.entity.StoreyDO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface StoreyRepository extends CrudRepository<StoreyDO, UUID> {

    @Query("Select s from StoreyDO s WHERE s.buildingDO.id = :buildingId")
    Iterable<StoreyDO> findAllByBuildingId(@Param("buildingId") UUID buildingId);
}
