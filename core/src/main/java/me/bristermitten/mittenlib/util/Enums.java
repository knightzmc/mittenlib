package me.bristermitten.mittenlib.util;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class for enums
 */
public class Enums {
    private Enums() {

    }

    /**
     * Creates a "prettified" name of an enum.
     * This is calculated by removing underscores and capitalizing the first letter of each word.
     *
     * @param e   The enum to get the name of
     * @param <E> The type of the enum
     * @return The prettified name of the enum
     */
    public static <E extends Enum<E>> @NotNull String prettyName(@NotNull E e) {
        final String[] split = e.name().split("_");
        return Strings.joinWith(split, Strings::capitalize, " ");
    }
}
