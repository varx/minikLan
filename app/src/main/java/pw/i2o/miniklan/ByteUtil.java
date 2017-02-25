package pw.i2o.miniklan;

/**
 * Created by varx on 24/02/2017.
 */

public class ByteUtil {


    public static String toHumanString(byte[] data) {
        if (data == null || data.length == 0)
            return "";
        String format = "%02X ";
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format(format, byteChar));
        return stringBuilder.toString();
    }
}
