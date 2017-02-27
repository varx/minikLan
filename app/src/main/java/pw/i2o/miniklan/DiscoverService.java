package pw.i2o.miniklan;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v7.util.SortedList;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pw.i2o.miniklan.bean.Device;
import smartplug.JniC;


public class DiscoverService extends IntentService {

    private String TAG = DiscoverService.class.getSimpleName();

    public DiscoverService() {
        super("DiscoverService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        byte[] wifiIp = Network.getWifiIp();
        if (wifiIp == null) {
            Log.e(TAG, "can't get wifi ip,discover fail");
            return;
        }

        wifiIp[3] = (byte) 0xff;
        JniC jniC = new JniC();

        //广播多次,因为wifi下UDP很容易丢包
        int retry = 3;
        while (retry-- > 0) {
            String cmd_query = "lan_phone%mac%nopassword%" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ENGLISH).format(new Date(System.currentTimeMillis())) + "%heart";
            try {
                InetAddress broadcastAddr = Inet4Address.getByAddress(wifiIp);
                DatagramSocket socket = new DatagramSocket();
                byte[] cmd_buf = jniC.PackageSendData(cmd_query, cmd_query.length());
                Log.d(TAG, "req:" + cmd_query);
                Log.d(TAG, "send to " + broadcastAddr.getHostAddress());
                socket.send(new DatagramPacket(cmd_buf, cmd_buf.length, broadcastAddr, Define.UDP_PORT));
                socket.setSoTimeout(5000);

                //等待timeout结束循环
                while (true) {
                    byte[] respBuf = new byte[128];
                    DatagramPacket backPacket = new DatagramPacket(respBuf, respBuf.length);
                    socket.receive(backPacket);
//                    String resp = new String(decode(backbuf, backPacket.getLength()));
                    String resp = jniC.AnalyzeRecvData(respBuf, backPacket.getLength());
                    Log.e("RESP", resp);
                    Device device = parseResp(resp);
                    if (device != null) {
                        Intent intent1 = new Intent(Define.ACTION.NEW_DEVICE);
                        intent1.putExtra(Define.ARG.DEVICE, device);
                        sendBroadcast(intent1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent1=new Intent(Define.ACTION.SEARCH_COMPLETE);
        sendBroadcast(intent1);
        Log.d(TAG, "discover finish");
    }


    private Device parseResp(String resp) {
        String[] split = resp.split("%");
        if (split.length != 5) {
            Log.e(TAG, "parse error,should have 5 pieces");
            return null;
        }
        if (!split[0].equals("lan_device")) {
            Log.e(TAG, "not start with lan_device");
            return null;
        }
        if (split[1].length() != 17) {
            Log.e(TAG, "piece 2 dont like mac address");
            return null;
        }
        if (!split[3].contains("#")) {
            Log.e(TAG, "piece 4 dont contains #");
            return null;
        }
        String[] sub = split[3].split("#");
        if (sub.length != 3) {
            Log.e(TAG, "parse error,should have 3 pieces");
            return null;
        }
        String mac = split[1];
        String pwd = split[2];
        boolean on = sub[0].equals("open");
        String hwVer = sub[1];
        String swVer = sub[2];
        Device device = new Device(mac, pwd, hwVer, swVer);
        device.setOn(on);
        return device;

    }


}
