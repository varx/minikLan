package pw.i2o.miniklan;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import smartplug.JniC;

/**
 * Created by varx on 24/02/2017.
 */

public class MiniK {


    private static String TAG = MiniK.class.getSimpleName();


    public static boolean send(String mac, String pwd, final String cmd) throws UnknownHostException, SocketException {
        byte[] wifiIp = Network.getWifiIp();
        JniC jniC = new JniC();
        if (wifiIp == null) {
            Log.e(TAG, "can't get wifi ip,discover fail");
            return false;
        }
        wifiIp[3] = (byte) 0xff;

        String msg = "lan_phone%" + mac + "%" + pwd + "%" + cmd + "%relay";
        byte[] cmd_buf = jniC.PackageSendData(msg, msg.length());
        InetAddress address = Inet4Address.getByAddress(wifiIp);
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        int retry = 3;
        while (retry-- > 0) {
            try {
                socket.send(new DatagramPacket(cmd_buf, cmd_buf.length, address, Define.UDP_PORT));
                byte[] backbuf = new byte[128];
                DatagramPacket backPacket = new DatagramPacket(backbuf, backbuf.length);
                socket.receive(backPacket);

                String resp = jniC.AnalyzeRecvData(backbuf, backPacket.getLength());
                //lan_phone%28-d9-8a-8b-96-bd%EUWlkBrG%open%relay��
                Log.e("resp", resp);

                String[] split = resp.split("%");
                if(split[3].equals(cmd)){
                    return true;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
