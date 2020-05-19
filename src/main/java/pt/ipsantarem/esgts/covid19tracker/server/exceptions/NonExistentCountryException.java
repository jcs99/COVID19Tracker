package pt.ipsantarem.esgts.covid19tracker.server.exceptions;

/**
 * Exception that gets thrown if the user tries to get a list of virus stat trees from a country that doesn't exist.
 */
public class NonExistentCountryException extends RuntimeException {
    public NonExistentCountryException() {
    }

    public NonExistentCountryException(String message) {
        super(message);
    }
}
