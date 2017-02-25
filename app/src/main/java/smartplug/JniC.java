package smartplug;

/**
 * Created by varx on 22/02/2017.
 */

public class JniC {
    static {
        System.loadLibrary("CommunSmartPlug-jni");
    }

    public native String AnalyzeRecvData(byte[] bArr, int i);

    public native int[] PackageConfigData(String str, String str2, int i);

    public native byte[] PackageSendData(String str, int i);


}
