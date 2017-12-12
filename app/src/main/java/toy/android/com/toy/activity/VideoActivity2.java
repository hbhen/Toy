package toy.android.com.toy.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.emm.meeting.MeetingUser;
import info.emm.meeting.Session;
import info.emm.meeting.SessionInterface;
import info.emm.sdk.VideoView;
import toy.android.com.toy.R;
import toy.android.com.toy.utils.ToastUtil;


public class VideoActivity2 extends AppCompatActivity implements View.OnClickListener, SessionInterface {

    private static final String TAG = "videoactivity2";
    static public int WEIYI_VIDEO_OUT_SLOW = 1;       //视频发送速度慢
    static public int WEIYI_VIDEO_OUT_DISCONNECT = 2; //视频发送连接断开重连
    static public int WEIYI_VIDEO_IN_SLOW = 3;        //视频接收速度慢
    static public int WEIYI_VIDEO_IN_DISCONNECT = 4;  //视频接收连接断开重连
    static public int WEIYI_AUDIO_DISCONNECT = 10;    //音频连接断开重连
    static public int WEIYI_AUDIO_PERMISSION = 11;    //无法开启麦克风
    public VideoView mMy_video, mOther_video;

    public boolean hasfront = false;
    public boolean usefront = false;

    final private boolean hasAudio = true;

    public int _watchingPeerID = 0;
    public int _myPeerID = 0;
    public boolean _sendingVideo = false;
    public boolean _serverMix = false;
    public int _codec = 1;
    public ArrayList<Integer> _userList = new ArrayList<Integer>();
    public boolean _playAudio = false;
    public int uid;
    public boolean _freeSpeak = false;

    public static final String UPDATE_URL = "http://u.weiyicloud.com/";
    public static final String HOCKEY_APP_HASH = "dedae71020c1c014120ef0153cb8457c";
    public int _warningtime = 0;

    private ImageView bottom_action_end_call;
    private boolean change = false;
    private int _xDelta;
    private int _yDelta;
    public List<Integer> mCams = null;
    private ImageView mStopCall;
    private ImageView mNoVideo;
    private boolean isShowVideo = true;
    private String mRoomid;
    private String mToken;
    private String mToyid;
    private SeekBar mSeekBar;
    private TextView mVolume_text;
    private String mVolumeString = "50";
    private int mVolumeInt;
    private ImageView mBabyImg;
    private TextView mBabyName;
    private String mUserFlag;
    private String mBabyimgString;
    private String mBabynameString;
    private int toyId;
    private int tvId;
    private String mMethod;

    public static void launch(Context context, String babyimgString, String babynameString, String roomid, String token, String toyId, String tvId) {
        Intent it = new Intent(context, VideoActivity.class);
        it.putExtra("babyimgString", babyimgString);
        it.putExtra("babynameString", babynameString);
        it.putExtra("roomid", roomid);
        it.putExtra("token", token);
        it.putExtra("toyId", toyId);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video2);
        //TODO  进来的时候还要获取一个玩具的初始音量 现在不加了
        mMy_video = (VideoView) findViewById(R.id.surfaceView5);
// 其他的视频不显示了
        mOther_video = (VideoView) findViewById(R.id.surfaceView6);
        mOther_video.setZOrderOnTop(true);

        //停止视频
        mStopCall = (ImageView) findViewById(R.id.iv_fragment_videocall_stopcall);
        //音量的数字显示
        Intent intent = getIntent();
        mMethod = intent.getStringExtra("method");
        Log.d(TAG, "onCreate: mMethod??+"+mMethod);
        mRoomid = intent.getStringExtra("roomid");
        mToken = intent.getStringExtra("token");
        mToyid = intent.getStringExtra("toyId");
        if (mMethod.equals("1")){
            ToastUtil.showToast(this,"method:1");
            Log.d(TAG, "onCreate: method:1");
            usefront = hasfront = Session.getInstance().Init(this, "demo", "", true);
            mCams = Session.getInstance().getCameraInfo();
            EnterMeeting();
            //TODO 给textview设置一个当前的音量值,这个值是从网络获取的 ,如果没有获取到就默认给50,这个值需不需要传给玩具再说!
            mStopCall.setOnClickListener(this);
            mMy_video.setOnClickListener(this);
            mOther_video.setOnClickListener(this);
            Session.getInstance().registerListener(this);
        }else{
            ToastUtil.showToast(this,"method:2");
            Log.d(TAG, "onCreate: method:2");
            stop();
            finish();
        }

    }

    private void EnterMeeting() {
        Start(false, 0);
    }

    public void Start(boolean serverMix, int codec) {
        Log.d(TAG, "Start: ***************");
        _serverMix = serverMix;
        _codec = codec;
        _warningtime = 0;
        uid = (int) (Math.random() * 100000);
        String ip = "www.weiyicloud.com";
        int port = 80;
        String meetingId = mRoomid;
        Session.getInstance().setWebHttpServerAddress(ip + ":" + port);
        mUserFlag = "toy";
        Session.getInstance().switchCamera(usefront);
//        Session.getInstance().setCameraQuality(_checkHQ.isChecked());
        Session.getInstance().setLoudSpeaker(true);
        Session.getInstance().setCameraQuality(true);
        /*
        * ip:服务器地址;
        * port:服务器端口;
        * uid:用户昵称;
        * meetingid:房间号;
        * meetingpwd:房间密码;
        * thirduid:指定一个用户id,在会议中识别身份;
        * usertype:用户身份:发布者1,观看者2,还有0;
        * paramMap:null即可
        * */
        Session.getInstance().joinmeeting(ip, port, mUserFlag + uid, meetingId, "", uid, 0, null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_fragment_videocall_stopcall:
                stop();
//                StopCallServer();
                finish();
                break;
            case 121:

//                Session.getInstance().unplayVideo(_watchingPeerID, 0);
//                Session.getInstance().unwatchOtherVideo(_watchingPeerID,0);
//                Session.getInstance().PlayVideo(_myPeerID, false, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);

//                if (isShowVideo) {
//                    isShowVideo = !isShowVideo;
//                    ToastUtil.showToast(this, "不看视频");
//                    mMy_video.setBackgroundResource(R.color.colorPrimary);
//                } else {
//                    isShowVideo = !isShowVideo;
//                    ToastUtil.showToast(this, "看视频");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        mMy_video.setBackground(null);
//                    }
//                    Session.getInstance().PlayVideo(_watchingPeerID, false, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
//                }

                break;
            //不显示小的视频窗口了,也不让他有点击切换的事件了
//            case R.id.surfaceView6:
//                if (!change) {
//                    Log.d("surfaceview", "surfaceview: +change1");
//                    Session.getInstance().PlayVideo(0, true, mOther_video, 0, 0, 1, 1, 0, false, 1, 0);
//                    Session.getInstance().PlayVideo(_watchingPeerID, true, mMy_video, 0, 0, 1, 1, 65, false, 1, 0);
//                    change = true;
//                } else {
//                    Log.d("surfaceview", "surfaceview: +change2");
//                    Session.getInstance().PlayVideo(0, true, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
//                    Session.getInstance().PlayVideo(_watchingPeerID, true, mOther_video, 0, 0, 1, 1, 65, false, 1, 0);
//                    change = false;
//                }
//                break;

            default:
                break;
        }
    }

    private void seeMe() {
        Log.d(TAG, "seeMe : *************** ");
        if (_myPeerID == 0)
            return;
        if (!_sendingVideo) {
            Log.d(TAG, "seeMe: !_sendingVideo=true");
            Session.getInstance().PlayVideo(0, true, mOther_video, 0, 0, 1, 1, 0, false, 0, 0);
            _sendingVideo = true;
        } else {
            Log.d(TAG, "seeMe: !_sendingVideo=false");
            Session.getInstance().PlayVideo(0, true, mOther_video, 0, 0, 1, 1, 0, false, 0, 0);
            _sendingVideo = false;
        }
    }

    private void seeYou() {
        Log.d(TAG, "seeYou : *************** ");
        if (_userList.size() == 0)
            return;
        if (_watchingPeerID == 0) {
            Log.d(TAG, "seeYou: _watchingPeerID==0");
            int peerID = _userList.get(0);
            Session.getInstance().PlayVideo(peerID, true, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
            Session.getInstance().requestSpeaking(peerID);
            Log.d(TAG, "_watchingPeerID : 判断条件 ");
            _watchingPeerID = peerID;
        } else {
            Log.d(TAG, "seeYou: _watchingPeerID!=0");

            Session.getInstance().PlayVideo(_watchingPeerID, true, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
            Session.getInstance().requestSpeaking(_watchingPeerID);
            _watchingPeerID = 0;
        }
    }

    @Override
    public void onWarning(int warning) {
        String warningString;
        if (warning == WEIYI_VIDEO_OUT_SLOW)
            warningString = getString(R.string.weiyi_video_out_slow);
        else if (warning == WEIYI_VIDEO_OUT_DISCONNECT)
            warningString = getString(R.string.weiyi_video_out_disconnect);
        else if (warning == WEIYI_VIDEO_IN_SLOW)
            warningString = getString(R.string.weiyi_video_in_slow);
        else if (warning == WEIYI_VIDEO_IN_DISCONNECT)
            warningString = getString(R.string.weiyi_video_in_disconnect);
        else if (warning == WEIYI_AUDIO_DISCONNECT)
            warningString = getString(R.string.weiyi_audio_disconnect);
        else if (warning == WEIYI_AUDIO_PERMISSION)
            warningString = getString(R.string.weiyi_audio_pernission);
        else
            warningString = getString(R.string.weiyi_other);

        _warningtime++;
    }

    @Override
    public void onConnect(int status, int i1) {
        Log.d(TAG, "onConnect : ***************");
        if (status == 0) {
            int meetingType = Session.getInstance().getMeetingType();
            if (meetingType == 0 || meetingType == 1 || meetingType == 2 || meetingType == 3 || meetingType == 4 || meetingType == 5 || meetingType
                    == 6) {
            } else {
                Session.getInstance().LeaveMeeting();
//                Toast.makeText(this, getString(R.string.meeting),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDisConnect(int i) {
        Log.d(TAG, "onDisConnect: ***************");
        _myPeerID = 0;
        _watchingPeerID = 0;
        uid = 0;
        _userList.clear();
    }

    @Override
    public void onUserIn(int peerId, boolean b) {
        Log.d(TAG, "onUserIn: ***************");
        _userList.add(peerId);
        MeetingUser user = Session.getInstance().getM_thisUserMgr().getUser(peerId);
        String name = user.getName();
        Log.d(TAG, "onUserIn: name" + name);
        Log.d(TAG, "onUserIn: peerId" + peerId);
        if (name.contains("phone")) {
            Log.d(TAG, "onUserIn: phone??走");
            toyId = peerId;
//            seeMe();
            seeYou();
        }
        if (name.contains("toy")) {
            Log.d(TAG, "onUserIn: toy??走");
            toyId = peerId;
            seeYou();
//            seeMe();
        }
        if (name.contains("tv")) {
            Log.d(TAG, "onUserIn: tv??走");
            tvId = peerId;
            seeYou();
//            seeMe();
//            Session.getInstance().PlayVideo(toyId, true, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
        }
    }

    @Override
    public void onUserOut(MeetingUser meetingUser) {
        Log.d(TAG, "onUserOut: ***************");
        _userList.remove(new Integer(meetingUser.getPeerID()));
        if (!_serverMix && hasAudio)
            Session.getInstance().unplayAudio(meetingUser.getPeerID());
        if (_watchingPeerID == meetingUser.getPeerID()) {
            _watchingPeerID = 0;
        }
    }

    @Override
    public void onEnablePresence(int peerID) {
        Log.d(TAG, "onEnablePresence: ***************");
        _myPeerID = peerID;
        //画中画的时候可以开启
        seeMe();
        seeYou();
        _freeSpeak = true;
        sendText(0, "i am " + android.os.Build.MODEL + android.os.Build.VERSION.RELEASE, android.os.Build.MODEL);
    }

    public void sendText(int toWhom, String text, String myname) {
        Session.getInstance().sendTextMessage(toWhom, text, null);
    }

    @Override
    public void onCallClientFunction(String s, int i, Object o) {

    }

    @Override
    public void onUserPropertyChange(int i, JSONObject jsonObject) {

    }

    @Override
    public void onRemotePubMsg(String s, int i, int i1, String s1, String s2, Object o) {

    }

    @Override
    public void onRemoteDelMsg(String s, int i, int i1, String s1, String s2, Object o) {

    }

    @Override
    public void onRecTextMsg(int i, int i1, String s, JSONObject jsonObject) {

    }

    @Override
    public void onCameraWillClose(Camera camera) {

    }

    @Override
    public void onPhotoTaken(boolean b, byte[] bytes) {

    }

    @Override
    public void onCameraDidOpen(Camera camera, boolean b, int i) {

    }

    @Override
    public void ChangeAudioStatus(int i, int i1) {

    }

    @Override
    public void syncVideoModeChange(boolean b, boolean b1) {

    }

    @Override
    public void showpage() {

    }

    @Override
    public void onPresentComplete() {
        Session.getInstance().requestSpeaking(_myPeerID);

    }

    @Override
    public void onVideoSizeChanged(int i, int i1, int i2, int i3) {

    }

    @Override
    public void onGotMeetingProperty(JSONObject jsonObject) {

    }

    @Override
    public void onServerRecording(boolean b) {

    }

    @Override
    public void onFocusUserChange(int i, int i1) {

    }

    @Override
    public void onWhitePadPageCount(int i) {

    }

    @Override
    public void onFocusSipChange(int i, int i1) {

    }

    @Override
    public void onCallSipACK(int i, int i1) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ***************");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ***************");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ***************");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ***************");
//        PackageManager packageManager=getPackageManager();
//        packageManager.setComponentEnabledSetting(new ComponentName("toy.android.com.toy",MyReceiver.class.getName()),PackageManager
// .COMPONENT_ENABLED_STATE_DISABLEDPackageManager.DONT_KILL_APP);
        stop();
        finish();
        //通知服务器,退出  需不需要通知???
//        StopCallServer();
    }

    private void stop() {
        Log.d(TAG, "Stop: ***************");
        Session.getInstance().StopSpeaking();
        _freeSpeak = false;
        Session.getInstance().LeaveMeeting();
        _myPeerID = 0;
        _watchingPeerID = 0;
        _userList.clear();
    }
}

