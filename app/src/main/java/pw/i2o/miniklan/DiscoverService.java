package pw.i2o.miniklan;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import smartplug.JniC;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DiscoverService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "pw.i2o.miniklan.action.FOO";
    private static final String ACTION_BAZ = "pw.i2o.miniklan.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "pw.i2o.miniklan.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "pw.i2o.miniklan.extra.PARAM2";
    private String TAG = DiscoverService.class.getSimpleName();

    public DiscoverService() {
        super("DiscoverService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DiscoverService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DiscoverService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    private boolean stop = false;

    @Override
    protected void onHandleIntent(Intent intent) {
        byte[] wifiIp = Network.getWifiIp();
        if (wifiIp == null) {
            Log.e(TAG, "can't get wifi ip,discover fail");
            return;
        }
        wifiIp[3] = (byte) 0xff;
        JniC jniC = new JniC();
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

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
