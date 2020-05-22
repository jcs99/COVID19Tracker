package pt.ipsantarem.esgts.covid19tracker.server.utils;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for object operations.
 */
public class ObjectUtils {

    /**
     * Method that returns a default object (the {@param defaultObj}) if the provided {@param obj} is null.
     *
     * @param obj        The object to return if not null.
     * @param defaultObj The object to return if the first passed object is null.
     * @param <T>        The type of object to return.
     * @return Either the {@param defaultObj} or the {@param obj}
     * @throws NullPointerException if both objects are null
     */
    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (obj != null) ? obj : requireNonNull(defaultObj, "defaultObj");
    }
}
