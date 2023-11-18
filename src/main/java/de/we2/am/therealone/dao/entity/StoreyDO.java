package de.we2.am.therealone.dao.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "storeys")
public class StoreyDO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "building_id", referencedColumnName = "id")
    private BuildingDO buildingDO;
    @Column(name = "deleted_at")
    private Instant deletedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public BuildingDO getBuildingDO() {
        return buildingDO;
    }

    public void setBuildingDO(BuildingDO buildingDO) {
        this.buildingDO = buildingDO;
    }
}
