package toy.android.com.toy.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.Strings;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import info.emm.meeting.MeetingUser;
import info.emm.meeting.Session;
import info.emm.meeting.SessionInterface;
import toy.android.com.toy.R;
import toy.android.com.toy.utils.ToastUtil;

import static android.R.attr.name;

public class VideoActivity extends AppCompatActivity implements SessionInterface {

    private static final String TAG = "videoaa";
    private static final String TAGD = "circle";
    //错误的信息 wrongmessage
    static public int WEIYI_VIDEO_OUT_SLOW = 1;
    static public int WEIYI_VIDEO_OUT_DISCONNECT = 2;
    static public int WEIYI_VIDEO_IN_SLOW = 3;
    static public int WEIYI_VIDEO_IN_DISCONNECT = 4;
    static public int WEIYI_AUDIO_DISCONNECT = 10;
    static public int WEIYI_AUDIO_PERMISSION = 11;
    static public int SENDMSGTOALL_EXCEPT_ME = 0;
    static public int SENDMSGTOALL = 0xFFFFFFFF;
    LayoutInflater _inflater;
    View _fragmentView;
    public boolean hasfront = false;
    public boolean usefront = false;
    public static final String UPDATE_URL = "http://u.weiyicloud.com/";
    public static final String HOCKEY_APP_HASH = "dedae71020c1c014120ef0153cb8457c";
    public info.emm.sdk.VideoView surface1 = null;
    public info.emm.sdk.VideoView surface2 = null;
    public TextView textView = null;
    private String mUserFlag;
    private List<Integer> mCameraInfo = null;
    private boolean _serverMix = false;
    private int _codec = 1;
    private int _warningtime = 0;
    private int uid;
    private int _myPeerID = 0;
    private int _watchingPeerID = 0;
    private ArrayList<Integer> _userList = new ArrayList<>();
    private boolean hasAudio = true;
    public boolean freeSpeak = false;
    private String mRoomid;
    public static final int CAMERA_OK = 0;
    private Intent mIntent;
    public static int RECORD_AUDIO_OK = 1;
    private String mMethod;
    private Intent mIntent1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        AtyContainer.getInstance().addActivity(this);
        Log.d(TAGD, "(VideoActivity)onCreate: went");
        mIntent = getIntent();
        mMethod = getIntent().getStringExtra("method");
        mRoomid = mIntent.getStringExtra("roomid");
        Log.i(TAG, "onCreate: mRoomid:" + mRoomid);
        if (mMethod.equals("1")) {
            Log.d(TAGD, "判断 onCreate: method 1  went");
            Session.getInstance().registerListener(this);
            usefront = hasfront = Session.getInstance().Init(this, "demo", "", true);
            boolean hasFrontCamera = Session.getInstance().hasFrontCamera();
            mCameraInfo = Session.getInstance().getCameraInfo();
            checkForCrashes();

            Log.i(TAG, "onCreate: hasfrontcamera:" + hasFrontCamera);
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_OK);
                } else {
                    Start(false, 0);
                }
            } else {
                Start(false, 0);
            }
        } else {
            Log.d(TAGD, "判断 onCreate: method 2  went");
            leaveMeeting();
            stopService(mIntent);
            finish();
        }
//        Log.i(TAG, "onCreate: ????");
//        Log.i(TAG, "onCreate: permission  !=");
//        Log.i(TAG, "onCreate: permission  ==");
//        Log.i(TAG, "onCreate: userfront:" + usefront + "--hasfront:" + hasfront);
//        Log.i(TAG, "onCreate: mCameraInfo:" + mCameraInfo);
    }

    private void checkForCrashes() {
        CrashManagerListener listener = new CrashManagerListener() {
            public String getStringForResource(int resourceID) {
                switch (resourceID) {
                    case Strings.CRASH_DIALOG_MESSAGE_ID:
                        return getResources().getString(R.string.crash_dialog_message);
                    case Strings.CRASH_DIALOG_NEGATIVE_BUTTON_ID:
                        return getResources().getString(R.string.crash_dialog_negative_button);
                    case Strings.CRASH_DIALOG_POSITIVE_BUTTON_ID:
                        return getResources().getString(R.string.crash_dialog_positive_button);
                    case Strings.CRASH_DIALOG_TITLE_ID:
                        return getResources().getString(R.string.crash_dialog_title);
                    default:
                        return null;
                }
            }
        };

//        CrashManager.register(this, UPDATE_URL, HOCKEY_APP_HASH, listener);
    }

    public void Start(boolean serverMix, int codec) {
        boolean isCameraCanUse = isCameraCanUse();
        Log.i(TAG, "isCameraCanUse:" + isCameraCanUse);
        this._serverMix = serverMix;
        Log.i(TAG, "Start: serverMix" + serverMix);
        Log.i(TAG, "Start: _serverMix" + _serverMix);
        this._codec = codec;
        this._warningtime = 0;
        String ip = "www.weiyicloud.com";
        int port = 80;
        String meetingId = mRoomid;
        uid = (int) (Math.random() * 100000);
        Log.i(TAG, "onCreate: uid:" + uid);
        Session.getInstance().setWebHttpServerAddress(ip + ":" + port);
        mUserFlag = "toy";
        Session.getInstance().switchCamera(usefront);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_OK);
        } else {
            Session.getInstance().setLoudSpeaker(true);
        }
        Session.getInstance().setCameraQuality(true);
        Session.getInstance().joinmeeting(ip, port, mUserFlag + uid, meetingId, "", uid, 0, null);
    }

    //摄像头的判断,能不能用摄像头(当前摄像头可不可用);
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera camera = null;
        try {
            camera = Camera.open();
            Session.getInstance().onCameraDidOpen(camera, true, 0);
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            camera.release();
            camera = null;
        }

        return canUse;
    }

    public void log(String str) {
        if (textView != null)
            textView.setText(str);
        Log.i("test", str);
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
        log("Warning" + _warningtime + " " + warningString);

    }

    //在连接的走的方法

    @Override
    public void onConnect(int status, int quality) {
        Log.d(TAGD, "(VideoActivity)onConnect: went");
        log("onConnect " + status + " " + quality);
        Log.i(TAG, "onConnect: went:" + status + ": :" + quality);
        MeetingUser meetingUser = new MeetingUser();
        Map<Integer, MeetingUser.Camera> mapCamera = meetingUser.getMapCamera();
        boolean b = meetingUser.ismIsOnLine();
        int clientType = meetingUser.getClientType();
        int thirdID = meetingUser.getThirdID();
        boolean video = meetingUser.ishasAudio();
        boolean audio = meetingUser.ishasAudio();
        int cameraCount = meetingUser.getCameraCount();
        String name = meetingUser.getName();
//        boolean cameraEnable = meetingUser.isCameraEnable(0);
//        int cameraIndexByIndex = meetingUser.getCameraIndexByIndex(0);
//        String cameraNameByIndex = meetingUser.getCameraNameByIndex(0);
        Log.i(TAG, "onConnect: mapCamera:" + mapCamera);
        Log.i(TAG, "onConnect: b:" + b);
        Log.i(TAG, "onConnect: clientType:" + clientType);
        Log.i(TAG, "onConnect: thirdID:" + thirdID);
        Log.i(TAG, "onConnect: video:" + video);
        Log.i(TAG, "onConnect: audio:" + audio);
        Log.i(TAG, "onConnect: name:" + name);
        Log.i(TAG, "onConnect: cameraCount:" + cameraCount);
//        Log.i(TAG, "onConnect: cameraEnable:" + cameraEnable);
//        Log.i(TAG, "onConnect: cameraIndexByIndex:" + cameraIndexByIndex);
//        Log.i(TAG, "onConnect: cameraNameByIndex:" + cameraNameByIndex);

        if (status == 0) {//普通人员的会议
            int meetingType = Session.getInstance().getMeetingType();
            if (meetingType == 0 || meetingType == 1 || meetingType == 2 || meetingType == 3 || meetingType == 4 ||
                    meetingType == 5 || meetingType
                    == 6) {
            } else {
                Session.getInstance().LeaveMeeting();
            }
        } else {
            Session.getInstance().setM_bInmeeting(false);
            onConnect(status, quality);
        }
    }

    @Override
    public void onDisConnect(int status) {
        Log.d(TAGD, "(VideoActivity)onDisConnect: went");
        log("onDisconnect " + status);
        Log.i(TAG, "onDisConnect: ??" + status);
        _myPeerID = 0;
        _watchingPeerID = 0;
        uid = 0;
        _userList.clear();
        Log.i(TAG, "onDisConnect: 走到这没有");
        stopService(mIntent);
        finish();


    }

    @Override
    public void onUserIn(int peerId, boolean b) {
        log("ClientFunc_UserIn " + peerId);
        Log.i(TAG, "onUserIn: went:" + peerId);
        //		if(!inList){
        _userList.add(peerId);
        //		}
        if (!_serverMix && hasAudio)
            Session.getInstance().playAudio(peerId);
    }

    @Override
    public void onUserOut(MeetingUser meetingUser) {
        log("ClientFunc_UserOut " + meetingUser.getPeerID());
        Log.i(TAG, "onUserOut: went:" + meetingUser.getPeerID());
        _userList.remove(new Integer(meetingUser.getPeerID()));
        if (!_serverMix && hasAudio)
            Session.getInstance().unplayAudio(meetingUser.getPeerID());
        if (_watchingPeerID == meetingUser.getPeerID()) {
            _watchingPeerID = 0;
        }
        AtyContainer.getInstance().removeActivity(this);
    }

    @Override
    public void onEnablePresence(int peerID) {
        _myPeerID = peerID;
        freeSpeak = true;
//        sendText(0, "i am " + android.os.Build.MODEL + android.os.Build.VERSION.RELEASE, android.os.Build.MODEL);
        sendText(0, "i am " + android.os.Build.MODEL + android.os.Build.VERSION.RELEASE, android.os.Build.MODEL);
    }

    public void sendText(int toWhom, String text, String myname) {
        Session.getInstance().sendTextMessage(toWhom, text, null);
    }

    @Override
    public void onCallClientFunction(String s, int peerID, Object params) {
        log("onCallClientFunction Name" + name + "peerId" + peerID + "params" + params);
    }

    @Override
    public void onUserPropertyChange(int peerID, JSONObject jsonObject) {
        log("UserChange PeerID" + peerID + "JsonObject" + jsonObject);
    }

    @Override
    public void onRemotePubMsg(String msgName, int fromID, int associatedUserID, String id, String associatedMsgID,
                               Object body) {
        log("onRemotePubMsg msgName" + msgName + "fromId" + fromID);
    }

    @Override
    public void onRemoteDelMsg(String msgName, int fromID, int associatedUserID,
                               String id, String associatedMsgID, Object body) {
        log("onRemoteDelMsg msgName" + msgName + "fromId" + fromID);
    }

    @Override
    public void onRecTextMsg(int fromid, int type, String msg, JSONObject arg3) {
        OnReceiveText(fromid, type, msg, arg3);
    }

    private void OnReceiveText(int fromID, int Type, String text, JSONObject arg3) {
        log("OnReceiveText ID" + fromID + " says " + text + "type" + Type);
        Log.i(TAG, "OnReceiveText: ID" + fromID + "say" + text + "type" + Type);
    }

    @Override
    public void onCameraWillClose(Camera camera) {
        camera.release();
        Log.i(TAG, "onCameraWillClose: close  ??");
    }

    @Override
    public void onPhotoTaken(boolean b, byte[] bytes) {

    }

    @Override
    public void onCameraDidOpen(Camera camera, boolean b, int i) {
        try {
            camera.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onCameraDidOpen: b:" + b);
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

    private static void onConnect(Activity activity, int nRet) {
        Log.d(TAGD, "(VideoActivity)onConnect: went");
        Log.d(TAG,"(VideoActivity)onConnect: went");
        Log.e("emm", "check meeting failed and result=" + nRet);
        if (activity == null) {
            return;
        }
        if (nRet == 4008) {
            Toast.makeText(activity, R.string.checkmeeting_error_4008, Toast.LENGTH_LONG).show();
        } else if (nRet == 4110) {
            Toast.makeText(activity, R.string.checkmeeting_error_4110, Toast.LENGTH_LONG).show();
        } else if (nRet == 4007) {
            Toast.makeText(activity, R.string.checkmeeting_error_4007, Toast.LENGTH_LONG).show();
        } else if (nRet == 3001) {
            Toast.makeText(activity, R.string.checkmeeting_error_3001, Toast.LENGTH_LONG).show();
        } else if (nRet == 3002) {
            Toast.makeText(activity, R.string.checkmeeting_error_3002, Toast.LENGTH_LONG).show();
        } else if (nRet == 3003) {
            Toast.makeText(activity, R.string.checkmeeting_error_3003, Toast.LENGTH_LONG).show();
        } else if (nRet == 4109) {
            Toast.makeText(activity, R.string.checkmeeting_error_4109, Toast.LENGTH_LONG).show();
        } else if (nRet == 4103) {
            Toast.makeText(activity, R.string.checkmeeting_error_4103, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, R.string.WaitingForNetwork, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveMeeting();

    }

    private void leaveMeeting() {
        Session.getInstance().LeaveMeeting();
        _myPeerID = 0;
        uid = 0;
        _watchingPeerID = 0;
        _userList.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: start??");
        switch (requestCode) {
            case CAMERA_OK:
                Log.i(TAG, "camra_ok: start??");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.showToast(this, "打开摄像头");
                    Log.i(TAG, "onRequestPermissionsResult: " + "true");
                } else {
                    ToastUtil.showToast(this, "请检查摄像头权限");
                    Log.i(TAG, "onRequestPermissionsResult: " + "false");
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAGD, "(VideoActivity)onStart: went");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAGD, "(VideoActivity)onResume: went");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAGD, "(VideoActivity)onPause: went");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAGD, "(VideoActivity)onStop: went");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mIntent);
        finish();
        Log.d(TAGD, "(VideoActivity)onDestroy: went");
    }
}
