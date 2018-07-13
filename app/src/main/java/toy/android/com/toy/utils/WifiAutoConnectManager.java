package toy.android.com.toy.utils;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WifiAutoConnectManager {
    private static final String TAG = WifiAutoConnectManager.class.getSimpleName();
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    Handler mHandler;
    Thread thread;
    WifiManager wifiManager;

    class ConnectRunnable implements Runnable {
        private String password;
        private String ssid;
        private WifiCipherType type;

        public ConnectRunnable(String ssid, String password, WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        public void run() {
            try {
                openWifi();
                sendMsg("opened");
                Thread.sleep(200);
                while (wifiManager.getWifiState() == 2) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
                if (wifiConfig == null) {
                    sendMsg("wifiConfig is null!");
                    return;
                }
                WifiConfiguration tempConfig = isExsits(ssid);
                if (tempConfig != null) {
                    wifiManager.removeNetwork(tempConfig.networkId);
                }
                sendMsg("enableNetwork status enable=" + wifiManager.enableNetwork(wifiManager.addNetwork(wifiConfig), true));
                sendMsg("enableNetwork connected=" + wifiManager.reconnect());
                sendMsg("??????!");
            } catch (Exception e2) {
                sendMsg(e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    public enum WifiCipherType {
        WIFICIPHER_WEP,
        WIFICIPHER_WPA,
        WIFICIPHER_NOPASS,
        WIFICIPHER_INVALID
    }

    public void sendMsg(String info) {
        if (mHandler != null) {
            Message msg = new Message();
            msg.obj = info;
            mHandler.sendMessage(msg);
            return;
        }
        LogUtil.e("wifi", info);
    }

    public WifiAutoConnectManager(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    public void connect(String ssid, String password, WifiCipherType type) {
        thread = new Thread(new ConnectRunnable(ssid, password, type));
        thread.start();
    }

    private WifiConfiguration isExsits(String SSID) {
        for (WifiConfiguration existingConfig : wifiManager.getConfiguredNetworks()) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(0);
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(0);
            config.allowedAuthAlgorithms.set(1);
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(0);
            config.allowedGroupCiphers.set(2);
            config.allowedKeyManagement.set(1);
            config.allowedPairwiseCiphers.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedPairwiseCiphers.set(2);
            config.status = 2;
        }
        return config;
    }

    private boolean openWifi() {
        if (wifiManager.isWifiEnabled()) {
            return true;
        }
        return wifiManager.setWifiEnabled(true);
    }

    private static boolean isHexWepKey(String wepKey) {
        int len = wepKey.length();
        if (len == 10 || len == 26 || len == 58) {
            return isHex(wepKey);
        }
        return false;
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            char c = key.charAt(i);
            if ((c < '0' || c > '9') && ((c < 'A' || c > 'F') && (c < 'a' || c > 'f'))) {
                return false;
            }
        }
        return true;
    }
}
