package pt.ipsantarem.esgts.covid19tracker.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for input streams.
 */
public class InputStreamUtils {

    /**
     * Reads all bytes from an input stream.
     *
     * @param inputStream The input stream to read bytes from.
     * @return The read bytes
     */
    public static byte[] readAllBytes(InputStream inputStream) {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}
