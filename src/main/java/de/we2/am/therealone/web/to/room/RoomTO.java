package de.we2.am.therealone.web.to.room;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoomTO(UUID id, String name, @JsonProperty("storey_id") UUID storeyId,
                     @JsonProperty("deleted_at") Instant deletedAt) {
}
