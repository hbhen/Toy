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
import android.text.TextUtils;
import android.util.Log;

import com.libra.sinvoice.Buffer;
import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;
import com.libra.sinvoice.VoiceRecognition;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toy.android.com.toy.activity.App;
import toy.android.com.toy.utils.LogUtil;
import toy.android.com.toy.utils.NetWorkUtil;
import toy.android.com.toy.utils.SPUtils;
import toy.android.com.toy.utils.ToastUtil;
import toy.android.com.toy.utils.WifiAutoConnectManager;

public class WifiSoundListenerService extends Service implements SinVoiceRecognition.Listener, VoiceRecognition.Callback {
    private final static String TAG = WifiSoundListenerService.class.getSimpleName();
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
    private SinVoiceRecognition mSinVoiceRecognition = null;
    private ArrayList<String> arr = new ArrayList<String>();
    String regText = "";
    String substring = "";
    String newregText = "";
    String newSubstring = "";

    @Override
    public Buffer.BufferData getRecognitionBuffer() {
        return null;
    }

    @Override
    public void freeRecognitionBuffer(Buffer.BufferData buffer) {
        String s = buffer.mData.toString();
        Log.i(TAG, "freeRecognitionBuffer: sssssss:" + s);
        buffer.reset();
    }

    public enum WifiType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: wifi开始了");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        App.serviceCount++;
        LogUtil.i(TAG, "++" + App.serviceCount + ";");
        LogUtil.i(TAG, "onStartCommand: (wifisoundlistenerservice)" + "走一个走一个");
        if (mSinVoiceRecognition != null) {
            mSinVoiceRecognition = null;
        }
        mSinVoiceRecognition = new SinVoiceRecognition(CODEBOOK);
        mSinVoiceRecognition.setListener(this);
        mSinVoiceRecognition.stop();
        mSinVoiceRecognition.stopRecognition();
        mSinVoiceRecognition.start();
        LogUtil.i(TAG, "onStartCommand: 点击了recognition");

//       在这里控制
        mHandler = new Handler() {
            StringBuilder textBuilder = new StringBuilder();

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SET_FULL_REG_TEXT:
                        String obj = (String) msg.obj;
                        LogUtil.i(TAG, "传回来的msg消息是(MSG_SET_FULL_REG_TEXT) : " + obj);
                        //拿到传回来的消息,也就是wifiname和wifekey,去设置网络.
                        if (obj == null) {
                            LogUtil.i(TAG, "handleMessage: 接收到的recognized的消息是空");
                            return;
                        } else {
                            String wlananme = SPUtils.getString(WifiSoundListenerService.this, "wlananme", "");
                            String wlankey = SPUtils.getString(WifiSoundListenerService.this, "wlankey", "");
                            LogUtil.i(TAG, "handleMessage: 拿到wifi的名字和密码了");
                            LogUtil.i(TAG, "handleMessage: wlanname=" + wlananme);
                            LogUtil.i(TAG, "handleMessage: wlankey=" + wlankey);
//                            setWlan(wlananme, wlankey);
                        }
                        break;
                    case MSG_SET_RECG_TEXT:
                        textBuilder.append((char) msg.arg1);
                        // TODO textbuilder要不要显示监听到的字符??
                    /*
                     2018/07/12 注释掉
                     List<StringBuilder> stringBuilders = Arrays.asList(textBuilder);
                        String ss = "";
                        for (StringBuilder s : stringBuilders) {
                            ss = ss + s;
                            LogUtil.i(TAG, s + "");
                        }
                        LogUtil.i(TAG, "enenen++" + ss);
                        LogUtil.i(TAG, "MSG_SET_RECG_TEXT : " + textBuilder.toString());*/
                        return;

                    case MSG_RECG_START:
                        textBuilder.delete(0, textBuilder.length());
                        LogUtil.i(TAG, "MSG_RECG_START : " + textBuilder.toString());
                        return;

                    case MSG_RECG_END:
//                        应该在接收数据结束的时候设置wifi信息
//                        LogUtil.i(TAG, "recognition end");
                        LogUtil.i(TAG, "MSG_RECG_END: (regText)=" + regText);
                       /*
                        2018/07/12 15:55分注释掉  ,使用ListenerManager来掌控网络的连接的状态
                        arr.clear();
                        for (int i = 0; i < regText.length(); i++) {
                            arr.add(String.valueOf(regText.charAt(i)));
                        }
                        LogUtil.i(TAG, "MSG_RECG_END: " + arr.toString());
                        if (arr != null) {
                            while (arr.contains("￥")) {
                                int y = arr.indexOf("￥");
                                String remove = arr.remove(y);
                                LogUtil.i(TAG, "handleMessage: remove" + remove);
                                arr.add(y, arr.get(y - 1));
                            }
                            LogUtil.i(TAG, "MSG_RECG_END: " + arr.toString());
                        }
                        LogUtil.i(TAG, "MSG_RECG_END: " + arr.toString());
                        newregText = "";
                        for (int i = 0; i < arr.size(); i++) {
                            newregText = newregText + arr.get(i);
                        }
                        LogUtil.i(TAG, "MSG_RECG_END: (newregText)" + newregText);
                        decodeWifiInfo(newregText);*/


//                        try {
//                            JSONObject jsonObject = new JSONObject();
//                            jsonObject.put("wifi", textBuilder.toString());
//                            ListenerManager.getInstance().sendBroadCast("connectWifi", jsonObject);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } finally {
//                            textBuilder.delete(0, textBuilder.length());
//                        }
                        String wifiInfo = textBuilder.toString();
                        if (wifiInfo.length() > 4 && wifiInfo.length() % 2 == 0) {

                            wifiInfo = parseString(wifiInfo);
                            LogUtil.i(TAG, "wifiInfo:" + wifiInfo);
                        }
                        String[] temp = wifiInfo.split("\\|");
                        if (temp.length < 2) {
                            ToastUtil.showToast(WifiSoundListenerService.this, "请输入密码");
                            return;
                        }
//                        temp[0] = temp[0].replace("-", " ");
                        connectWifi(toURLDecoded(temp[0]), toURLDecoded(temp[1]));
                        mSinVoiceRecognition.stop();
                        mSinVoiceRecognition.stopRecognition();
                        textBuilder.delete(0, textBuilder.length());
                        stopSelf();
                        LogUtil.i(TAG, "没了");
                        return;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
//        return START_REDELIVER_INTENT;
        return super.onStartCommand(intent, flags, startId);//
    }

    private void connectWifi(String ssid, String psd) {
        WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager((WifiManager) getSystemService(WIFI_SERVICE));
        try {
            wifiAutoConnectManager.connect(ssid, psd, psd.equals("") ? WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS : WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
        } catch (Exception e) {
            LogUtil.i(TAG, "connectwifi exception: " + e);
        }

    }

    public String toURLDecoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            LogUtil.i(TAG, "wifi : " + paramString);
            return "";
        }
        try {
            return URLDecoder.decode(new String(paramString.getBytes(), "UTF-8"), "UTF-8");
        } catch (Exception localException) {
            LogUtil.i(TAG, "wifi : + errlr" + localException);
            return "";
        }
    }

    @Override
    public void onDestroy() {
//        结束ConnectInternetService
        this.stopSelf();
        LogUtil.i(TAG, "sed1");
        super.onDestroy();
        LogUtil.i(TAG, "sed");
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
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = this.createWifiInfo(wlanname, wlankey, WifiType.WIFICIPHER_WPA);
        //设置网络,名称,key传给config
        mWifiManager.addNetwork(wifiConfiguration);
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
        String textInfo = connectionInfo.toString();
        LogUtil.i(TAG, "setWlan: textInfo" + textInfo);
        //判断以下是否有网了,注册一个有网的广播,监听有网了以后,去
//            mTv_settext.setText(textInfo);
        if (NetWorkUtil.isNetworkConn(this) && App.isNetWorkAvailable) {
            mSinVoiceRecognition.stop();
            mSinVoiceRecognition.stopRecognition();
            LogUtil.i(TAG, "setWlan: 成功联网了");
        } else {
//            mSinVoiceRecognition.freeRecognitionBuffer(mSinVoiceRecognition.getRecognitionBuffer());
//            在这里做重新监听,或者关闭录制的借口
//            LogUtil.i(TAG, "setWlan: buffer:" + mSinVoiceRecognition.getRecognitionBuffer().toString());
            String s = mSinVoiceRecognition.getRecognitionBuffer().mData.toString();
            Log.i(TAG, "setWlan: ssssssssssss():" + s);
            mSinVoiceRecognition.getRecognitionBuffer().reset();
            Log.i(TAG, "setWlan: reset走没有?");
            substring = "";
            newSubstring = "";
            newregText = "";
            arr.clear();
            Log.i(TAG, "setWlan: (substring)+(newsubstring)+(newregtext)+(arr)" + substring + ";" + newSubstring + ";" + newregText + ";" + arr.size() + ";");
            SPUtils.putString(WifiSoundListenerService.this, "wlananme", "");
            SPUtils.putString(WifiSoundListenerService.this, "wlankey", "");
            Log.i(TAG, "setWlan: (wlanname)" + ":" + wlanname + "; wlankey : " + wlankey + ";");
            mSinVoiceRecognition.stop();
            LogUtil.i(TAG, "(WifiSoundListenerService,setWlan,未联网成功的mSinVoiceRecognition.stop");
            mSinVoiceRecognition.start();
            LogUtil.i(TAG, "(WifiSoundListenerService,setWlan,未联网成功的mSinVoiceRecognition.start");
            LogUtil.i(TAG, "setWlan: 重新开始收听");
        }
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
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }

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


    private static boolean isHexWepKey(String wepkey) {
        int len = wepkey.length();
        if (len == 10 || len == 26 || len == 58) {
            return isHex(wepkey);
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

    @Override
    public void onRecognitionStart() {
        regText = "";
        LogUtil.i(TAG, "onRecongnitionStart ");
        mHandler.sendEmptyMessage(MSG_RECG_START);
    }

    @Override
    public void onRecognition(char ch) {
//        在这里做操作

      /* 注释的时间 2018年7月13日14:51:06
       (LogUtil.i(TAG, "onRecognition: 看看走了几次");
        LogUtil.i(TAG, "onRecognition: THREAD信息 : " + Thread.activeCount() + ";" + Thread.currentThread() + ".");
        LogUtil.i(TAG, "onRecognition: regtext(前) : " + regText);
        LogUtil.i(TAG, "onRecognition: char" + ch);
        regText = regText + ch;
        LogUtil.i(TAG, "onRecognition: regtext(后) : " + regText);
        if (regText.endsWith("$")) {
            int endFlagFirstAppear = regText.indexOf('$');
            String withoutEndFlagString = regText.substring(0, endFlagFirstAppear);
            LogUtil.i(TAG, withoutEndFlagString);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_RECG_TEXT, withoutEndFlagString));
            mSinVoiceRecognition.stopRecognition();
            mSinVoiceRecognition.stop();
        } else {
            LogUtil.i(TAG, ch + "");
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
        })*/
        LogUtil.i(TAG, "onRecognition: regtext(前) : " + regText);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    //Main_ConnectService
    @Override
    public void onRecognitionEnd() {
        LogUtil.i(TAG, "onRecognitionEnd");
        mHandler.sendEmptyMessage(MSG_RECG_END);
    }
//    解析wifi信息  这里会不会有问题?比如说是其中一个循环除了问题,后面的都不能用?
//    在此之前,是监听的声波录制的字符,字符如果录制的就有问题,那么转码肯定有问题.
//    在录制声波的时候,没法判断是否正确,因为,手机不知道你要传输的是什么样的字符,他不能判断什么是正确的wifi信息.
//    其次,人也不行,因为人不知道,我发送的是正确,但是录制的是不是正确的,人不知道.所以只能在是否联网成功这一块做工作.

    private void decodeWifiInfo(String text) {
//        LogUtil.i(TAG, "decodeWifiInfo: regText="+regText);
        //循环egtext
        LogUtil.i(TAG, "decodeWifiInfo: text::" + text + "{}" + text.length());
        substring = "";
        for (int i = 0; i < text.length() - 1; i++) {//这里为什么要length-1;因为是每次取两位,如果循环到length,取两位会越界!
            //我想两位两位取
            if (i % 2 == 0) {
                newSubstring = text.substring(i, i + 2);//i=0,2; newSubstring=9
                Log.i(TAG, "decodeWifiInfo: for()　:　" + newSubstring);
                if (newSubstring.contains("$")) {
                    return;
                }
                int parseIndex = Integer.parseInt(newSubstring);
                String s = arrayList.get(parseIndex);
                substring = substring + s;
                LogUtil.i(TAG, "onCreate: String s=" + s);
                LogUtil.i(TAG, "onCreate: String substring=" + substring);
            }
        }
        LogUtil.i(TAG, "onCreate: substring:" + substring);

        int indexOfVerticalBar = substring.indexOf("|");
        if (indexOfVerticalBar == -1) {
            return;
        } else {
            LogUtil.i(TAG, "onCreate: indexOfVerticalBar+" + indexOfVerticalBar);
            String wlanName = substring.substring(0, indexOfVerticalBar);
            String wlanSecret = substring.substring(indexOfVerticalBar + 1, substring.length());
            LogUtil.i(TAG, "onCreate----wlanName:" + wlanName + "和 wlanSecret:" + wlanSecret + " ;");
            try {
                String decodeWlanName = URLDecoder.decode(wlanName, "UTF-8");
                String decodeWlanSecret = URLDecoder.decode(wlanSecret, "UTF-8");
                LogUtil.i(TAG, "onCreate----decodeWlanName:" + decodeWlanName + "和 decodeWlanSecret:" + decodeWlanSecret + " ;");
                SPUtils.putString(this, "wifiname", decodeWlanName);
                SPUtils.putString(this, "wifikey", decodeWlanSecret);
                LogUtil.i(TAG, "decodeWlanName : " + SPUtils.getString(this, "wifiname", "") + "decodeWlanSecret : " + SPUtils.getString(this, "wifikey", ""));
                setWlan(decodeWlanName, decodeWlanSecret);
//            } catch (UnsupportedEncodingException e) {
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_FULL_REG_TEXT, text));
        }
    }

    private String parseString(String text) {
        String XX = "";
        String YY = "";
        String result = "";
        for (int i = 0; i < text.length(); i += 2) {
            String alpha = "";
            String s = text.substring(i, i + 1);
            String x = text.substring(i + 1, i + 2);
            if (s.equals("￥") || s.equals("$")) {
                s = YY;
            }
            XX = s;
            if (x.equals("￥") || x.equals("$")) {
                x = XX;
            }
            YY = x;
            result = result + ((String) arrayList.get(Integer.parseInt(alpha + s + x)));

        }
        return result;
    }


}
