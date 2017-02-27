package pw.i2o.miniklan;

import android.os.AsyncTask;
import android.os.Handler;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Created by varx on 25/02/2017.
 */

public class Worker {
    private static Worker instance;
    private final Executor mExecutor;
    private Handler mHandler;

    private Worker() {
        mHandler = new Handler();
        mExecutor = AsyncTask.SERIAL_EXECUTOR;
    }

    public static Worker getInstance() {
        if (instance == null)
            instance = new Worker();
        return instance;
    }

    public static interface ExeCallback {
        public void result(boolean result);
    }

    public void close(final String mac, final String pwd, final ExeCallback callback) {
        execute(mac, pwd, "close", callback);
    }

    public void open(final String mac, final String pwd, final ExeCallback callback) {
        execute(mac, pwd, "open", callback);
    }


    public void execute(final String mac, final String pwd, final String cmd, final ExeCallback callback) {
        Runnable runnable = () -> {
            boolean result = false;
            try {
                result = MiniK.send(mac, pwd, cmd);
            } catch (UnknownHostException | SocketException e) {
                e.printStackTrace();
            }
            final boolean finalResult = result;
            mHandler.post(() -> callback.result(finalResult));
        };
        mExecutor.execute(runnable);
    }
}
