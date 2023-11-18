package de.we2.am.therealone.dao.repository;

import de.we2.am.therealone.dao.entity.StoreyDO;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface StoreyRepository extends CrudRepository<StoreyDO, UUID> {
}
