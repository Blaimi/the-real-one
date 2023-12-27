package de.we2.am.therealone.web.request.room;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoomCreateRequest(@JsonProperty(required = true) String name,
                                  @JsonProperty(required = true, value = "storey_id") String storeyId) {
}
