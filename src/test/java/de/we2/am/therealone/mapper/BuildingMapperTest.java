package de.we2.am.therealone.mapper;

import de.we2.am.therealone.dao.entity.BuildingDO;
import de.we2.am.therealone.web.to.building.BuildingTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingMapperTest {

    @Test
    public void testDOtoTO() {
        BuildingMapper buildingMapper = new BuildingMapper();

        BuildingDO buildingDO = new BuildingDO();

        UUID id = UUID.randomUUID();
        buildingDO.setName("Kiki");
        buildingDO.setAddress("Koriko");
        buildingDO.setId(id);

        BuildingTO buildingTO = buildingMapper.convertDOtoTO(buildingDO);

        assertEquals("Kiki", buildingTO.name());
        assertEquals("Koriko", buildingTO.address());
        assertEquals(id, buildingTO.id());
    }
}
