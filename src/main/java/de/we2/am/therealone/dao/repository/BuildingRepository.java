package de.we2.am.therealone.dao.repository;

import de.we2.am.therealone.dao.entity.BuildingDO;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BuildingRepository extends CrudRepository<BuildingDO, UUID> {
}
