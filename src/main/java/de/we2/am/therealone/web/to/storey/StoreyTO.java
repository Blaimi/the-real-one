package de.we2.am.therealone.web.to.storey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StoreyTO(UUID id, String name, @JsonProperty("building_id") UUID buildingId,
                       @JsonProperty("deleted_at") Instant deletedAt) {
}
