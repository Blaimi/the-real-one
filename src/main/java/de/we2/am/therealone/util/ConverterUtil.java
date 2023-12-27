package de.we2.am.therealone.util;

import de.we2.am.therealone.exception.InvalidArgumentException;
import de.we2.am.therealone.exception.NotFoundException;

import java.util.Locale;
import java.util.UUID;

public final class ConverterUtil {

    public static UUID convertId(String id, String objectType) {
        // It's easier to take a string as id and manually parse it (and throw an error if it is not an uuid)
        // Instead of taking an uuid and catch the thrown error from jakarta
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("Requested %s was not found", objectType.toLowerCase(Locale.ROOT)), String.format("A %s with the id '%s' does not exist", objectType.toLowerCase(Locale.ROOT), id), objectType, id);
        }
    }

    public static UUID convertArgumentId(String id, String objectType, String argumentName) {
        // It's easier to take a string as id and manually parse it (and throw an error if it is not an uuid)
        // Instead of taking an uuid and catch the thrown error from jakarta
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Not a valid uuid", String.format("The id '%s' is not a valid uuid", id), objectType, argumentName, id, null);
        }
    }
}
