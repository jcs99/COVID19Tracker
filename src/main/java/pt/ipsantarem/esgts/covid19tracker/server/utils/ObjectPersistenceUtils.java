package pt.ipsantarem.esgts.covid19tracker.server.utils;

import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * An utility class that has various methods related to object persistence.
 */
public class ObjectPersistenceUtils {
    private static final String LAST_DOWNLOAD_DATE_FILE = "lastDownloadDate.dat";
    private static final String SAVED_MAP_FILE = "map.dat";

    /**
     * Read the last download date.
     *
     * @return A LocalDate object
     */
    public static LocalDate readLastDownloadDate() {
        return readObjectFile(LAST_DOWNLOAD_DATE_FILE);
    }

    /**
     * Writes the last download date to the file.
     *
     * @param lastDownloadDate A LocalDate object
     */
    public static void writeLastDownloadDate(LocalDate lastDownloadDate) {
        writeObjectFile(lastDownloadDate, LAST_DOWNLOAD_DATE_FILE);
    }

    /**
     * Reads the records map.
     *
     * @return The read records map
     */
    public static Map<String, List<AVLVirusStatsTree<?, ?>>> readRecordsMap() {
        return readObjectFile(SAVED_MAP_FILE);
    }

    /**
     * Writes a map of records to the file.
     *
     * @param map A records map
     */
    public static void writeRecordsMap(Map<String, List<AVLVirusStatsTree<?, ?>>> map) {
        writeObjectFile(map, SAVED_MAP_FILE);
    }

    /**
     * Utility function for reading object files.
     *
     * @param fileName The name of the file.
     * @param <T>      The type of the object we are reading.
     * @return The read object.
     */
    @SuppressWarnings("unchecked")
    private static <T> T readObjectFile(String fileName) {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(
                             new File(getCurrentAbsolutePath() + "/" + fileName)))) {
            return (T) ois.readObject();
        } catch (FileNotFoundException fnfe) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Utility function for writing object files.
     *
     * @param obj      The object to write
     * @param fileName The filename for which the object should be written into
     * @param <T>      The type of the object we are writing.
     */
    private static <T> void writeObjectFile(T obj, String fileName) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(
                             new File(getCurrentAbsolutePath() + "/" + fileName)))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the current absolute path based on the context of the currently running JAR file.
     *
     * @return The absolute path
     */
    private static String getCurrentAbsolutePath() {
        return ObjectPersistenceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
}
