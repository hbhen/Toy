package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import toy.android.com.toy.utils.SPUtils;

public class WifiSoundListenerService extends Service implements SinVoiceRecognition.Listener, SinVoicePlayer.Listener {
    private final static String TAG = "Main_ConnectService";
    private final static int MSG_SET_RECG_TEXT = 1;
    private final static int MSG_RECG_START = 2;
    private final static int MSG_RECG_END = 3;
    private final static int MSG_SET_FULL_REG_TEXT = 4;
    private final static String CODEBOOK = "0123456789￥$";
    private List<String> arrayList = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "_", "!", "b", "c", "d", "e", "f", "g", "h", ".",
            "i", "。", "j", "k", "l", "m", "n", "o", "p", "~", "q", "r", ";", "s", "t", "u", "v", "w", "x", "`", "y", "z", "A", ":", "B", "C", "D",
            "E", "F", "<", "G", "H", "I", "J", ">", "K", "L", "M", "N", "?", "O", "P", "Q", "R", "S", ",", "T", "U", "V", "'", "W", "X", "Y", "Z",
            "0", "|", "77", "a", "@", "#", "%", "^", "&", "*", "(", ")", "-", "+", "9", "{", "1", "2", "3", "4", "5", "6", "7", "8", "}", "", "",
            "", "", "", "", "");
    private SinVoicePlayer mSinVoicePlayer;
    private Handler mHandler;
    private WifiManager mWifiManager;
    private SinVoiceRecognition mSinVoiceRecognition;

    public enum WifiType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: wifi开始了");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: (wifisoundlistenerservice)" + "走一个走一个");
        mSinVoiceRecognition = new SinVoiceRecognition(CODEBOOK);
        mSinVoiceRecognition.setListener(this);
        mSinVoiceRecognition.start();
        Log.i(TAG, "onStartCommand: 点击了recognition");

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                StringBuilder mTextBuilder = new StringBuilder();
                switch (msg.what) {
                    case MSG_SET_FULL_REG_TEXT:
                        String obj = (String) msg.obj;
                        Log.i(TAG, "传回来的msg消息是:" + obj);
                        //拿到传回来的消息,也就是wifiname和wifekey,去设置网络.
                        if (obj == null) {
                            Log.i(TAG, "handleMessage: 接收到的recognized的消息是空");
                            return;
                        } else {
                            String wlananme = SPUtils.getString(WifiSoundListenerService.this, "wlananme", "");
                            String wlankey = SPUtils.getString(WifiSoundListenerService.this, "wlankey", "");
                            Log.i(TAG, "handleMessage: 拿到wifi的名字和密码了");
//                            setWlan(wlananme, wlankey);
                        }
                        break;
                    case MSG_SET_RECG_TEXT:
                        char ch = (char) msg.arg1;
                        mTextBuilder.append(ch);
                        break;

                    case MSG_RECG_START:
                        mTextBuilder.delete(0, mTextBuilder.length());
                        break;

                    case MSG_RECG_END:
//                        应该在接收数据结束的时候设置wifi信息
                        Log.i(TAG, "recognition end");
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        结束ConnectInternetService
        this.stopSelf();
//        结束BroadCastReceiver
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
//        这里返回空行不行,有没有更好的返回值??
    }


    private void setWlan(String wlanname, String wlankey) {
//        在这里开始做设置网络的操作,判断在外头做,这里只做
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = this.createWifiInfo(wlanname, wlankey, WifiType.WIFICIPHER_WPA);
        //设置网络,名称,key传给config
        mWifiManager.addNetwork(wifiConfiguration);
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
        String textInfo = connectionInfo.toString();
        Log.i(TAG, "setWlan: textInfo" + textInfo);
        //判断以下是否有网了,注册一个有网的广播,监听有网了以后,去
//            mTv_settext.setText(textInfo);
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, WifiType type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WifiType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);//有个问题,有什么快捷的方式来知道,应该填哪些参数?
        } else if (type == WifiType.WIFICIPHER_WEP) {
            config.wepKeys[0] = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedAuthAlgorithms.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedAuthAlgorithms.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedAuthAlgorithms.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedAuthAlgorithms.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }

    @Override
    public void onPlayStart() {
        Log.i(TAG, "onPlayStart: went");
    }

    @Override
    public void onPlayEnd() {
        Log.i(TAG, "onPlayEnd: went");

    }

    @Override
    public void onRecognitionStart() {
        mHandler.sendEmptyMessage(MSG_RECG_START);
    }

    String regText = "";
    String substring = "";

    @Override
    public void onRecognition(char ch) {
//在这里做操作~
        Log.i(TAG, "onRecognition: 看看走了多久");
        Log.i(TAG, "onRecognition: char" + ch);
        regText = regText + ch;
        Log.i(TAG, "onRecognition: regtext" + regText);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    @Override
    public void onRecognitionEnd() {
        mHandler.sendEmptyMessage(MSG_RECG_END);
        decodeWifiInfo();
        regText = "";
        substring = "";
        mSinVoiceRecognition.stop();
    }

    private void decodeWifiInfo() {
        //循环regtext
        for (int i = 0; i < regText.length() - 1; i++) {
            //我想两位两位取
            if (i % 2 == 0) {
                String newSubstring = regText.substring(i, i + 2);//i=0,2; newSubstring=91
                int parseIndex = Integer.parseInt(newSubstring);
                String s = arrayList.get(parseIndex);
                substring = substring + s;
                Log.i(TAG, "onCreate: String s=" + s);
            }
        }
        Log.i(TAG, "onCreate: substring" + substring);
        int indexOfVerticalBar = substring.indexOf("|");
        if (indexOfVerticalBar == -1) {
            return;
        } else {
            Log.i(TAG, "onCreate: indexOfVerticalBar+" + indexOfVerticalBar);
            String wlanName = substring.substring(0, indexOfVerticalBar);
            String wlanSecret = substring.substring(indexOfVerticalBar + 1, substring.length());
            Log.i(TAG, "onCreate----wlanName:" + wlanName + "和 wlanSecret:" + wlanSecret + " ;");
            try {
                String decodeWlanName = URLDecoder.decode(wlanName, "UTF-8");
                String decodeWlanSecret = URLDecoder.decode(wlanSecret, "UTF-8");
                Log.i(TAG, "onCreate----decodeWlanName:" + decodeWlanName + "和 decodeWlanSecret:" + decodeWlanSecret + " ;");
                SPUtils.putString(this, "wifiname", decodeWlanName);
                SPUtils.putString(this, "wifikey", decodeWlanSecret);
                setWlan(decodeWlanName, decodeWlanSecret);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_FULL_REG_TEXT, regText));
        }
    }
}
