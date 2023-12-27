package de.we2.am.therealone.web.request.storey;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StoreyCreateRequest(@JsonProperty(required = true) String name,
                                  @JsonProperty(required = true, value = "building_id") String buildingId) {
}
