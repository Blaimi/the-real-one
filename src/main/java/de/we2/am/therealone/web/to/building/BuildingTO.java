package de.we2.am.therealone.web.to.building;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuildingTO(UUID id, String name, String address, @JsonProperty(value = "deleted_at") Instant deletedAt) {
}
