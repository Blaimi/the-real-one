package de.we2.am.therealone.util;

import de.we2.am.therealone.exception.InvalidArgumentException;

import java.util.UUID;

public final class ConverterUtil {

    private static final String UUID_PATTERN = " ^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    public static UUID convertId(String id, String objectType) {
        // It's easier to take a string as id and manually parse it (and throw an error if it is not an uuid)
        // Instead of taking an uuid and catch the thrown error from jakarta
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new InvalidArgumentException("Not a valid uuid", String.format("The id '%s' is not a valid uuid", id), objectType, "quarry parameter", id, UUID_PATTERN);
        }
    }

    public static UUID convertArgumentId(String id, String objectType, String argumentName) {
        // It's easier to take a string as id and manually parse it (and throw an error if it is not an uuid)
        // Instead of taking an uuid and catch the thrown error from jakarta
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new InvalidArgumentException("Not a valid uuid", String.format("The id '%s' is not a valid uuid", id), objectType, argumentName, id, UUID_PATTERN);
        }
    }
}
