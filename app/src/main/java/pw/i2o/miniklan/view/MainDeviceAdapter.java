package pw.i2o.miniklan.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pw.i2o.miniklan.R;
import pw.i2o.miniklan.bean.Device;

/**
 * Created by varx on 25/02/2017.
 */

public class MainDeviceAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private DeviceList devices = new DeviceList();

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_device, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
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