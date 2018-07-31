package toy.android.com.toy.service;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.emm.meeting.MeetingUser;
import info.emm.meeting.Session;
import info.emm.meeting.SessionInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toy.android.com.toy.R;
import toy.android.com.toy.bean.ToyQuitRoomBySelfReqBean;
import toy.android.com.toy.bean.ToyQuitRoomBySelfResBean;
import toy.android.com.toy.interf.MyInterface;
import toy.android.com.toy.internet.Constants;
import toy.android.com.toy.utils.LogUtil;
import toy.android.com.toy.utils.SPUtils;
import toy.android.com.toy.utils.ToastUtil;

/**
 * Created by DTC on 2017/10/2816:48.
 */

public class MultiVideoServiceUse extends IntentService implements SessionInterface, ControlPlayService.onMusicPlaying {

    private static final String TAG = MultiVideoServiceUse.class.getSimpleName();
    static public int WEIYI_VIDEO_OUT_SLOW = 1;       //视频发送速度慢
    static public int WEIYI_VIDEO_OUT_DISCONNECT = 2; //视频发送连接断开重连
    static public int WEIYI_VIDEO_IN_SLOW = 3;        //视频接收速度慢
    static public int WEIYI_VIDEO_IN_DISCONNECT = 4;  //视频接收连接断开重连
    static public int WEIYI_AUDIO_DISCONNECT = 10;    //音频连接断开重连
    static public int WEIYI_AUDIO_PERMISSION = 11;    //无法开启麦克风
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
    public List<Integer> mCams = null;
    private ImageView mStopCall;
    private String mRoomid;
    private String mUserFlag;
    private String mMethod;
    private int toyId;
    private int tvId;
    private String mToken;
    private String mToyid;

    public MultiVideoServiceUse() {
        super("video");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "(videoservice2)onCreate: went");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMethod = intent.getStringExtra("method");
        LogUtil.d(TAG, "onCreate: mMethod??+" + mMethod);
        mRoomid = intent.getStringExtra("roomid");
        mToken = intent.getStringExtra("token");
        mToyid = intent.getStringExtra("toyId");
        if (mMethod.equals("1")) {
            ToastUtil.showToast(this, "method:1");
            LogUtil.d(TAG, "onCreate: method:1");
            //TODO 开发上线的时候把true改为false :不将日志写入文件.
            usefront = hasfront = Session.getInstance().Init(this, "demo", "", false);
            mCams = Session.getInstance().getCameraInfo();
            Session.getInstance().setRotate(0);
            EnterMeeting();
            //TODO 给textview设置一个当前的音量值,这个值是从网络获取的 ,如果没有获取到就默认给50,这个值需不需要传给玩具再说!
            Session.getInstance().registerListener(this);
        } else {
            ToastUtil.showToast(this, "method:2");
            LogUtil.d(TAG, "onCreate: method:2");
            stop();
            quitRoomBySelf();
            this.stopSelf();
        }
        return START_STICKY;
    }

    private void quitRoomBySelf() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyInterface myInterface = retrofit.create(MyInterface.class);
        ToyQuitRoomBySelfReqBean.BODYBean bodyBean = new ToyQuitRoomBySelfReqBean.BODYBean(mRoomid, SPUtils.getString(MultiVideoServiceUse.this, "toyid", ""));
        ToyQuitRoomBySelfReqBean toyQuitRoomBySelfReqBean = new ToyQuitRoomBySelfReqBean("REQ", "TOYQUIT", "", "", bodyBean, "", mToken, "1");
        Gson gson = new Gson();
        String s = gson.toJson(toyQuitRoomBySelfReqBean);
        Call<ToyQuitRoomBySelfResBean> toyQuitRoomBySelfResBeanCall = myInterface.TOY_QUIT_ROOM_BY_SELF_RES_BEAN_CALL(s);
        toyQuitRoomBySelfResBeanCall.enqueue(new Callback<ToyQuitRoomBySelfResBean>() {
            @Override
            public void onResponse(Call<ToyQuitRoomBySelfResBean> call, Response<ToyQuitRoomBySelfResBean> response) {
                if (response.body().getCODE().equals("0")) {
                    LogUtil.i(TAG, response.body().getBODY().toString());
                }
            }

            @Override
            public void onFailure(Call<ToyQuitRoomBySelfResBean> call, Throwable t) {
                LogUtil.i(TAG, t.toString());
            }
        });

    }

    private void EnterMeeting() {
        Start(false, 0);
    }

    public void Start(boolean serverMix, int codec) {
        LogUtil.i(TAG, "Start: ***************");
        _serverMix = serverMix;
        _codec = codec;
        _warningtime = 0;
        uid = (int) (Math.random() * 100000);
        String ip = "www.weiyicloud.com";
        int port = 80;
        String meetingId = mRoomid;
        Session.getInstance().setWebHttpServerAddress(ip + ":" + port);
        mUserFlag = "toy";
        String uidFlagStr = SPUtils.getString(MultiVideoServiceUse.this, "toyid", uid + "");
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
        LogUtil.i(TAG, "name : " + uidFlagStr + ", uid : " + uid + ";");
        Session.getInstance().joinmeeting(ip, port, uidFlagStr, meetingId, "", uid, 0, null);
//        Session.getInstance().joinmeeting(ip, port, mUserFlag, meetingId, "", uid, 0, null);
        shutDownService(ControlPlayService.getInstance(), ControlPlayService.getMediaPlayer());
    }

    private void seeMe() {
        LogUtil.i(TAG, "seeMe : *************** ");
        if (_myPeerID == 0)
            return;
        if (!_sendingVideo) {
            LogUtil.i(TAG, "seeMe: !_sendingVideo=true");
//            Session.getInstance().PlayVideo(0, true, mOther_video, 0, 0, 1, 1, 0, false, 0, 0);
            _sendingVideo = true;
        } else {
            LogUtil.i(TAG, "seeMe: !_sendingVideo=false");
//            Session.getInstance().PlayVideo(0, true, mOther_video, 0, 0, 1, 1, 0, false, 0, 0);
            _sendingVideo = false;
        }
    }

    private void seeYou() {
        LogUtil.i(TAG, "seeYou  go !!");
        LogUtil.i(TAG, "seeYou : *************** ");
        if (_userList.size() == 0)
            return;
        if (_watchingPeerID == 0) {
            LogUtil.i(TAG, "seeYou: _watchingPeerID==0");
            int peerID = _userList.get(0);
//            Session.getInstance().PlayVideo(peerID, true, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
//            Session.getInstance().requestSpeaking(peerID);
            LogUtil.i(TAG, "_watchingPeerID : 判断条件 ");
            _watchingPeerID = peerID;
        } else {
            LogUtil.i(TAG, "seeYou: _watchingPeerID!=0");
//            Session.getInstance().PlayVideo(_watchingPeerID, true, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
//            Session.getInstance().requestSpeaking(_watchingPeerID);
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
        LogUtil.i(TAG, "onConnect go !!");
        LogUtil.i(TAG, "onConnect : ***************");
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
    public void onUserIn(int peerId, boolean b) {
        LogUtil.i(TAG, "onUserIn go !!");
        LogUtil.i(TAG, "onUserIn: ***************");
        _userList.add(peerId);
        MeetingUser user = Session.getInstance().getM_thisUserMgr().getUser(peerId);
        String name = user.getName();
        LogUtil.i(TAG, "onUserIn: name" + name);
        LogUtil.i(TAG, "onUserIn: peerId" + peerId);
        toyId = peerId;
        Session.getInstance().playAudio(_myPeerID);
        seeYou();
//        if (name.equals("phone")) {
//            LogUtil.i(TAG, "onUserIn: phone??走");
//            toyId = peerId;
////            seeMe();
//            seeYou();
//        }
//        if (name.equals("toy")) {
//            LogUtil.i(TAG, "onUserIn: toy??走");
//            toyId = peerId;
//            seeYou();
////            seeMe();
//        }
//        if (name.equals("tv")) {
//            LogUtil.i(TAG, "onUserIn: tv??走");
//            tvId = peerId;
//            seeYou();
//            seeMe();
//            Session.getInstance().PlayVideo(toyId, true, mMy_video, 0, 0, 1, 1, 0, false, 1, 0);
//    }

    }

    @Override
    public void onEnablePresence(int peerID) {
        LogUtil.i(TAG, "onEnablePresence go !!");
        LogUtil.i(TAG, "onEnablePresence: ***************");
        _myPeerID = peerID;
        //画中画的时候可以开启
        seeMe();
        seeYou();
        _freeSpeak = true;
        sendText(0, "i am " + android.os.Build.MODEL + android.os.Build.VERSION.RELEASE, android.os.Build.MODEL);
    }

    @Override
    public void onUserOut(MeetingUser meetingUser) {
        LogUtil.i(TAG, "onUserOut go !!");
        LogUtil.i(TAG, "onUserOut: ***************");
        _userList.remove(new Integer(meetingUser.getPeerID()));
        if (!_serverMix && hasAudio)
            Session.getInstance().unplayAudio(meetingUser.getPeerID());
        if (_watchingPeerID == meetingUser.getPeerID()) {
            _watchingPeerID = 0;
        }
    }

    @Override
    public void onDisConnect(int i) {
        LogUtil.i(TAG, "onDisConnect go !!");
        LogUtil.i(TAG, "onDisConnect: ***************");
        _myPeerID = 0;
        _watchingPeerID = 0;
        uid = 0;
        _userList.clear();
    }


    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy go !!");
        LogUtil.i(TAG, "onDestroy: ***************");
//        PackageManager packageManager=getPackageManager();
//        packageManager.setComponentEnabledSetting(new ComponentName("toy.android.com.toy",MyReceiver.class.getName()),PackageManager
// .COMPONENT_ENABLED_STATE_DISABLEDPackageManager.DONT_KILL_APP);
        stop();
        //这里调用这个没有用
        quitRoomBySelf();
        this.stopSelf();
        super.onDestroy();
    }

    private void stop() {
        LogUtil.i(TAG, "Stop: ***************");
        Session.getInstance().StopSpeaking();
        _freeSpeak = false;
        Session.getInstance().LeaveMeeting();
        _myPeerID = 0;
        _watchingPeerID = 0;
        _userList.clear();
    }

    //控制音频和视频的冲突
    @Override
    public void shutDownService(ControlPlayService controlPlayService, MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();

        }
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
        LogUtil.i(TAG, "onPresentComplete go !!");
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


}
