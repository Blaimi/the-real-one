package de.we2.am.therealone.web.request.room;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RoomPutRequest {

    public static final String DEFAULT_DELETED_AT = "Laputa";

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true, value = "storey_id")
    private String storeyId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreyId() {
        return storeyId;
    }

    public void setStoreyId(String storeyId) {
        this.storeyId = storeyId;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RoomPutRequest that = (RoomPutRequest) object;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getStoreyId(), that.getStoreyId()) && Objects.equals(getDeletedAt(), that.getDeletedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getStoreyId(), getDeletedAt());
    }

    @Override
    public String toString() {
        return "RoomPutRequest{" +
                "name='" + getName() + '\'' +
                ", storeyId='" + getStoreyId() + '\'' +
                ", deletedAt='" + getDeletedAt() + '\'' +
                '}';
    }
}
