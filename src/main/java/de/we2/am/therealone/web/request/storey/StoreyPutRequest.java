package de.we2.am.therealone.web.request.storey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class StoreyPutRequest {

    public static final String DEFAULT_DELETED_AT = "Laputa";

    private final String name;

    private final String buildingId;

    /*
        There is no easy way in Jackson (at least I didn't find one)
        to differentiate between a null value e.g. {"deleted_at": null} and a missing value e.g. {},
        without hooking and overriding Jacksons parsing method.

        Which is why we use this method. If it is an explicit null, this field is set to null.
        If it is missing, the default value is present.
        If it is something else, then the value is set to something else.
        This means we tread the value "Laputa" as missing, which is not a bug, but a feature / Easter egg.
     */
    @JsonProperty("deleted_at")
    private String deletedAt = DEFAULT_DELETED_AT;

    @JsonCreator
    public StoreyPutRequest(@JsonProperty(required = true, value = "name") String name, @JsonProperty(required = true, value = "building_id") String buildingId) {
        this.name = name;
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        StoreyPutRequest that = (StoreyPutRequest) object;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getBuildingId(), that.getBuildingId()) && Objects.equals(getDeletedAt(), that.getDeletedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBuildingId(), getDeletedAt());
    }

    @Override
    public String toString() {
        return "BuildingPutRequest{" +
                "name='" + getName() + '\'' +
                ", buildingId='" + getBuildingId() + '\'' +
                ", deletedAt='" + getDeletedAt() + '\'' +
                '}';
    }
}
