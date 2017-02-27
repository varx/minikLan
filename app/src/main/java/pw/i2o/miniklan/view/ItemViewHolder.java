package pw.i2o.miniklan.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import pw.i2o.miniklan.bean.Device;
import pw.i2o.miniklan.R;
import pw.i2o.miniklan.Worker;

/**
 * Created by varx on 25/02/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
    private TextView textMac, textInfo;
    private Switch sw;
    private Device device;

    public ItemViewHolder(View itemView) {
        super(itemView);
        textMac = (TextView) itemView.findViewById(R.id.text_mac);
        textInfo = (TextView) itemView.findViewById(R.id.text_info);
        sw = (Switch) itemView.findViewById(R.id.sw);
        sw.setOnCheckedChangeListener(this);
    }

    public void bind(Device device) {
        this.device = device;
        textMac.setText(device.mac);
        textInfo.setText("hw ver: " + device.hwVer + " sw ver: " + device.swVer);
        sw.setChecked(device.on);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        device.setOn(isChecked);
        Worker worker = Worker.getInstance();
        Worker.ExeCallback callback = (result) -> {
            if (!result) {
                device.setOn(false);
                sw.setChecked(false);
            }
        };

        if (isChecked) {
            worker.open(device.mac, device.pwd, callback);
        } else {
            worker.close(device.mac, device.pwd, callback);
        }
    }
}
