package de.we2.am.therealone.web.to.error;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorTO(ErrorCodeTO code, String message, @JsonProperty("more_info") String moreInfo) {
}
