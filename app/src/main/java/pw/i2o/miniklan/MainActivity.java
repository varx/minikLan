package pw.i2o.miniklan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter();
        recyclerview.setAdapter(mAdapter);
        registerReceiver(receiver, new IntentFilter(Define.ACTION.NEW_DEVICE));
        startService(new Intent(this, DiscoverService.class));
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<Device> devices = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_device, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(devices.get(position));
        }

        public void add(Device device) {
            devices.add(device);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }
    }

    private static class SwitchTask extends AsyncTask<Device, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Device... params) {
            Device device = params[0];
            String cmd = device.on ? "open" : "close";
            try {
                return MiniK.send(device.mac, device.pwd, cmd);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private TextView textMac, textInfo;
        private Switch sw;
        private Device device;

        public ViewHolder(View itemView) {
            super(itemView);
            textMac = (TextView) itemView.findViewById(R.id.text_mac);
            textInfo = (TextView) itemView.findViewById(R.id.text_info);
            sw = (Switch) itemView.findViewById(R.id.sw);
            sw.setOnCheckedChangeListener(this);
        }

        public void bind(Device device) {
            this.device = device;
            textMac.setText(device.mac);
            textInfo.setText("pwd: " + device.pwd + " hw ver: " + device.hwVer + " sw ver: " + device.swVer);
            sw.setChecked(device.on);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            device.setOn(isChecked);
            new SwitchTask().execute(device);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Device device = (Device) intent.getSerializableExtra(Define.ARG.DEVICE);
            Log.d(TAG, "receive new device");
            if (device != null)
                mAdapter.add(device);
        }
    };

}
