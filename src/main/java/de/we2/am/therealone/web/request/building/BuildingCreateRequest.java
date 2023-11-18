package de.we2.am.therealone.web.request.building;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BuildingCreateRequest(@JsonProperty(required = true) String name,
                                    @JsonProperty(required = true) String address) {
}
