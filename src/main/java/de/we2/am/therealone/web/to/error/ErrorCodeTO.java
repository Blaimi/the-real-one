package de.we2.am.therealone.web.to.error;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum ErrorCodeTO {
    BAD_REQUEST,
    NOT_AUTHORIZED,
    NO_NEED_TO_KNOW;

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase(Locale.ROOT);
    }
}
