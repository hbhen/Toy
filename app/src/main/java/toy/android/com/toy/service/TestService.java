package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
* Notice:
* 该service功能:
* 1/打开声波接收功能,准备接收声波
* 2/接收到wifi声波信息之后,把wifi的名字和密码保存
* 3/将保存的wifi的name和密码,设置到系统wifi上
* 4/如果wifi设置成功,关闭当前的service(依据什么判断wifi连接成功??)
* */
@Deprecated
public class TestService extends Service implements SinVoiceRecognition.Listener {
    private final static String TAG = "MainActivity";
    private final static int MAX_NUMBER = 5;
    private final static int MSG_SET_RECG_TEXT = 1;
    private final static int MSG_RECG_START = 2;
    private final static int MSG_RECG_END = 3;
    private final static int MSG_SET_FULL_REG_TEXT = 4;
    private SinVoicePlayer mSinVoicePlayer;
    private SinVoiceRecognition mRecognition;
    private Handler mHandler;
    private ArrayList mList = new ArrayList();
    private final static String CODEBOOK = "0123456789￥$";
    private List<String> arrayList = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "_", "!", "b", "c", "d", "e", "f", "g", "h", ".",
            "i", "。", "j", "k", "l", "m", "n", "o", "p", "~", "q", "r", ";", "s", "t", "u", "v", "w", "x", "`", "y", "z", "A", ":", "B", "C", "D",
            "E", "F", "<", "G", "H", "I", "J", ">", "K", "L", "M", "N", "?", "O", "P", "Q", "R", "S", ",", "T", "U", "V", "'", "W", "X", "Y", "Z",
            "0", "|", "77", "a", "@", "#", "%", "^", "&", "*", "(", ")", "-", "+", "9", "{", "1", "2", "3", "4", "5", "6", "7", "8", "}", "", "",
            "", "", "", "", "");

    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRecognition = new SinVoiceRecognition(CODEBOOK);
        mRecognition.setListener(this);
        mHandler = new RegHandler();
        mRecognition.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static class RegHandler extends Handler {
        private StringBuilder mTextBuilder = new StringBuilder();
        private TextView mRecognisedTextView;
        public RegHandler() {

        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_RECG_TEXT:
                    char ch = (char) msg.arg1;
                    mTextBuilder.append(ch);
//                    if (null != mRecognisedTextView) {
//                        mRecognisedTextView.setText(mTextBuilder.toString());
//                    }
                    Log.i(TAG, "handleMessage: mtextbuilder" + mTextBuilder.toString());
                    break;

                case MSG_RECG_START:
//                    mTextBuilder.delete(0, mTextBuilder.length());
                    Log.i(TAG, "handleMessage: start recognite");
                    break;

                case MSG_RECG_END:
                    Log.i(TAG, "recognition end");
                    Log.i(TAG, "handleMessage: message_recg_end:" + mTextBuilder.toString());
                    break;
//                case MSG_SET_FULL_REG_TEXT:
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRecognitionStart() {

    }

    String regText = "";

    @Override
    public void onRecognition(char ch) {
        Log.i(TAG, "onRecognition: char" + ch);
        regText = regText + ch;
        Log.i(TAG, "onRecognition: regtext" + regText);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    @Override
    public void onRecognitionEnd() {
        mHandler.sendEmptyMessage(MSG_RECG_END);
        decodeWifiInfo();
//        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SET_FULL_REG_TEXT, regText));
        regText = "";
        substring = "";
    }

    String substring = "";

    private void decodeWifiInfo() {
        //循环regtext
        for (int i = 0; i < regText.length() - 1; i++) {
            //我想两位两位取
            if (i % 2 == 0) {
                String newSubstring = regText.substring(i, i + 2);//i=0,2; newSubstring=91
                int parseIndex = Integer.parseInt(newSubstring);
                String s = arrayList.get(parseIndex);
                substring = substring + s;
                mList.add(newSubstring);
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

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            myHandler.sendMessage(myHandler.obtainMessage(MSG_SET_FULL_REG_TEXT, regText));

        }


    }
}
