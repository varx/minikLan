package pw.i2o.miniklan;

import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by varx on 24/02/2017.
 */

public class Network {


    private static String TAG=Network.class.getSimpleName();

    public static byte[] getWifiIp(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()){
                NetworkInterface anInterface = networkInterfaces.nextElement();
                if(!anInterface.getName().startsWith("wlan"))
                    continue;
                Enumeration<InetAddress> addrs = anInterface.getInetAddresses();
                while (addrs.hasMoreElements()){
                    InetAddress addr = addrs.nextElement();
                    if(addr instanceof Inet4Address){
                        return addr.getAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
