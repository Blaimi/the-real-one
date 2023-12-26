package de.we2.am.therealone.dao.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class RoomDO {

    @Id
    private UUID id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "storey_id", referencedColumnName = "id")
    private StoreyDO storeyDO;
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

    public StoreyDO getStoreyDO() {
        return storeyDO;
    }

    public void setStoreyDO(StoreyDO storeyDO) {
        this.storeyDO = storeyDO;
    }
}
