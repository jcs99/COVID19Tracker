package pt.ipsantarem.esgts.covid19tracker.server.callbacks;

/**
 * Interface that's used as a callback for scheduling constant checks to the World in Data webpage.
 */
public interface UpdateAvailableListener {
    void onUpdateAvailable(String data);
}
