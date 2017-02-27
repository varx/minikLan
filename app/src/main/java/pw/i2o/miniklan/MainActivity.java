package pw.i2o.miniklan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import pw.i2o.miniklan.bean.Device;
import pw.i2o.miniklan.view.MainDeviceAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = MainActivity.class.getSimpleName();
    private MainDeviceAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MainDeviceAdapter();
        recyclerview.setAdapter(mAdapter);

        IntentFilter intentFilter = new IntentFilter(Define.ACTION.NEW_DEVICE);
        intentFilter.addAction(Define.ACTION.SEARCH_COMPLETE);
        registerReceiver(receiver, intentFilter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Define.ACTION.NEW_DEVICE.equals(action)) {
                Device device = (Device) intent.getSerializableExtra(Define.ARG.DEVICE);
                Log.d(TAG, "receive new device");
                if (device != null)
                    mAdapter.add(device);
            }
            if (Define.ACTION.SEARCH_COMPLETE.equals(action) && mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

        }
    };

    @Override
    public void onRefresh() {
        startService(new Intent(this, DiscoverService.class));
    }
}
