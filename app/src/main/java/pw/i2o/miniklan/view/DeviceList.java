package pw.i2o.miniklan.view;

import android.support.v7.util.SortedList;
import android.util.Log;

import pw.i2o.miniklan.bean.Device;

/**
 * Created by varx on 25/02/2017.
 */

public class DeviceList extends SortedList<Device> {
    public DeviceList() {
        super(Device.class, new Callback<Device>() {

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }

            @Override
            public int compare(Device o1, Device o2) {
                return o1.mac.compareTo(o2.mac);
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Device oldItem, Device newItem) {
                boolean equals = oldItem.mac.equals(newItem.mac);
                Log.e("areContentsTheSame",oldItem.mac+" : "+newItem.mac+" > "+equals);
                return oldItem.mac.equals(newItem.mac);
            }

            @Override
            public boolean areItemsTheSame(Device item1, Device item2) {
                return areContentsTheSame(item1,item2);
            }
        });
    }

}

