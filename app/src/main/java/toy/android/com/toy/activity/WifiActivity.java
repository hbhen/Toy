package toy.android.com.toy.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.Record;
import com.libra.sinvoice.SinVoiceRecognition;

import java.util.Arrays;
import java.util.List;

import toy.android.com.toy.R;
import toy.android.com.toy.utils.ToastUtil;

/**
 * Created by DTC on 2017/12/410:32.
 */
public class WifiActivity extends AppCompatActivity implements SinVoiceRecognition.Listener, Record.Listener {
    private static final String TAG = "wifiactivity";
    private final static String CODEBOOK = "0123456789￥$";
    private SinVoiceRecognition mSinVoiceRecognition;
    Handler mHandler;
    private final static int MSG_SET_RECG_TEXT = 1;
    private final static int MSG_RECG_START = 2;
    private final static int MSG_RECG_END = 3;
    private TextView mRecognisedTextView;

    private List<String> arrayList = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "_", "!", "b", "c", "d", "e", "f", "g", "h", ".",
            "i", "。", "j", "k", "l", "m", "n", "o", "p", "~", "q", "r", ";", "s", "t", "u", "v", "w", "x", "`", "y", "z", "A", ":", "B", "C", "D",
            "E", "F", "<", "G", "H", "I", "J", ">", "K", "L", "M", "N", "?", "O", "P", "Q", "R", "S", ",", "T", "U", "V", "'", "W", "X", "Y", "Z",
            "0", "|", "77", "a", "@", "#", "%", "^", "&", "*", "(", ")", "-", "+", "9", "{", "1", "2", "3", "4", "5", "6", "7", "8", "}", "", "",
            "", "", "", "", "");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        Log.d(TAG, "onCreate: 从这里开始");
        mSinVoiceRecognition = new SinVoiceRecognition(CODEBOOK);
        mSinVoiceRecognition.setListener(this);
        mRecognisedTextView = (TextView) findViewById(R.id.tv_wifi);

        mHandler = new RegHandler(mRecognisedTextView);
        Button recgonized_start = (Button) findViewById(R.id.bt_recg_start);
        Button recgonized_stop = (Button) findViewById(R.id.bt_recg_stop);
        recgonized_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSinVoiceRecognition.start();
            }
        });
        recgonized_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSinVoiceRecognition.stop();
            }
        });


    }

    @Override
    public void onStartRecord() {
        ToastUtil.showToast(this, "startrecord");
        Log.d(TAG, "onStartRecord: 开始录音");
    }

    @Override
    public void onStopRecord() {
        ToastUtil.showToast(this, "stoprecord");
        Log.d(TAG, "onStopRecord: 结束录音");
    }

    private static class RegHandler extends Handler {
        private StringBuilder mTextBuilder = new StringBuilder();
        private TextView mRecognisedTextView;

        public RegHandler(TextView textView) {
            mRecognisedTextView = textView;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_RECG_TEXT:
                    char ch = (char) msg.arg1;
                    Log.d(TAG, "handleMessage: " + ch);
                    mTextBuilder.append(ch);
                    if (null != mRecognisedTextView) {
                        mRecognisedTextView.setText(mTextBuilder.toString());
                    }
                    break;

                case MSG_RECG_START:
                    mTextBuilder.delete(0, mTextBuilder.length());
                    break;

                case MSG_RECG_END:
                    LogHelper.d(TAG, "recognition end");
                    break;
            }
            super.handleMessage(msg);
        }
    }

    //继承的是sinvoiceRecognized
    @Override
    public void onRecognitionStart() {
        ToastUtil.showToast(this, "recognitionstart");
        Log.d(TAG, "onRecognitionStart: ???");
        Log.d(TAG,"start");
        mHandler.sendEmptyMessage(MSG_RECG_START);
    }

    @Override
    public void onRecognition(char ch) {
        ToastUtil.showToast(this, "onrecognition");
        Log.d(TAG, "onRecognition: ??");
        Log.d(TAG,"onstart");
        Log.d(TAG, "onRecognition: char字符" + ch);
//        mRecognisedTextView.setText(ch);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    @Override
    public void onRecognitionEnd() {
        ToastUtil.showToast(this, "recognitionend");
        Log.d(TAG, "onRecognitionEnd: ??");
        Log.d(TAG,"end");
        mHandler.sendEmptyMessage(MSG_RECG_END);
//压根就没有收到声音,没有反应,那会哪里出问题? 声音发出来后,应该打开
    }

}
