package pw.i2o.miniklan;

import java.io.Serializable;

/**
 * Created by varx on 24/02/2017.
 */

public class Device implements Serializable {
    public String mac;
    public String pwd;
    public String hwVer;
    public String swVer;
    public boolean on;

    public Device(String mac, String pwd, String hwVer, String swVer) {
        this.mac = mac;
        this.pwd = pwd;
        this.hwVer = hwVer;
        this.swVer = swVer;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
