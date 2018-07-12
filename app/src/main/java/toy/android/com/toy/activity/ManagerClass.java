package toy.android.com.toy.activity;

import org.json.JSONObject;

import static toy.android.com.toy.activity.ManagerClass.OnListener.STATUS_CONNECTWIFI;
import static toy.android.com.toy.activity.ManagerClass.OnListener.STATUS_RESETNETWORK;

/**
 * Created by DTC on 2018/7/12.
 */

public class ManagerClass implements ListenerManager.IListener {
    private OnListener mOnListener;

    @Override
    public void notifyAllActivity(String s, JSONObject object) {
        if (s.equals("resetNetwork")) {
            mOnListener.OnTransport(STATUS_RESETNETWORK, object);
        }
        if (s.equals("connectWifi")) {
            mOnListener.OnTransport(STATUS_CONNECTWIFI, object);
        }
    }

    public interface OnListener {

        public static final int STATUS_CONTROLMUSIC = 0;
        public static final int STATUS_VOLUME = 1;
        public static final int STATUS_CONTROLTOY = 2;
        public static final int STATUS_RECORDVOLUME = 3;
        public static final int STATUS_UPDATE = 4;
        public static final int STATUS_ACTIVATION = 5;
        public static final int STATUS_RESETNETWORK = 6;
        public static final int STATUS_CURRENTSTATUS = 7;
        public static final int STATUS_CONNECTWIFI = 8;

        void OnTransport(int i, JSONObject jSONObject);
    }
}
