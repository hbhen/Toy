package com.example.meetingsdkdemo;


import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.Strings;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.emm.meeting.MeetingUser;
import info.emm.meeting.Session;
import info.emm.meeting.SessionInterface;

//import com.weiyicloud.whitepad.Face_Share_Fragment.penClickListener;
//import com.weiyicloud.whitepad.SharePadMgr;

/**
 * ?????SDKDemo
 *
 * @author Administrator
 */
public class MainActivity extends FragmentActivity implements SessionInterface, OnClickListener {

    static public int WEIYI_VIDEO_OUT_SLOW = 1;       //????????????
    static public int WEIYI_VIDEO_OUT_DISCONNECT = 2; //?????????????????
    static public int WEIYI_VIDEO_IN_SLOW = 3;        //????????????
    static public int WEIYI_VIDEO_IN_DISCONNECT = 4;  //?????????????????
    static public int WEIYI_AUDIO_DISCONNECT = 10;    //?????????????
    static public int WEIYI_AUDIO_PERMISSION = 11;    //???????????

    static public int SENDMSGTOALL_EXCEPT_ME = 0;

    static public int SENDMSGTOALL = 0xFFFFFFFF;
    final static public String ASSOCAITEUSERID = "associatedUserID";


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
    public List<Integer> _cams = null;

    public MeetingFragment _fragment_meeting = null;
    public Fragment _fragment_share = null;

    // viewpager about
    private MyAdapter _adapter = null;
    private MyViewpager _pager = null;
    public int nLastShowPageUser = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForCrashes();
        Session.getInstance().registerListener(this);
//        Session.getInstance().registerWhiteBroad(SharePadMgr.getInstance());
        usefront = hasfront = Session.getInstance().Init(getApplicationContext(), "demo", "", true);
        _cams = Session.getInstance().getCameraInfo();

        // Meeting about
        _fragment_meeting = new MeetingFragment(this);

        // White pad about
//        SharePadMgr.getInstance().setShareControl(Session.getInstance());
//        SharePadMgr.getInstance().setAppContext(getApplicationContext());
//        SharePadMgr.getInstance().setControlMode(ControlMode.fullcontrol);
//        SharePadMgr.getInstance().setClient(Session.getInstance().client);

//        _fragment_share = SharePadMgr.getInstance().createWhitePadFragment(this, Session.getInstance(), this, false);

        // ViewPager about
        _adapter = new MyAdapter(getSupportFragmentManager());
//        _pager = (MyViewpager) findViewById(R.id.vPager_meeting);
//        _pager.setAdapter(_adapter);
//        _pager.setCanScroll(false);
//        log(android.os.Build.MANUFACTURER + " " + android.os.Build.BRAND + " has " + _cams.size() + " camera(s)");
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

        CrashManager.register(this, UPDATE_URL, HOCKEY_APP_HASH, listener);
    }

    public void log(String str) {
        if (_fragment_meeting != null)
            _fragment_meeting.log(str);
    }


    public void onPhotoTaken(boolean success, byte[] data) {
        log("onPhotoTaken len = " + data.length);
        if (!success)
            return;

        try {
            saveToSDCard(data);
        } catch (java.io.IOException e) {

        }
    }


    /**
     * ??????????????????SD????
     *
     * @param data
     * @throws IOException
     */
    public static void saveToSDCard(byte[] data) throws java.io.IOException {
        //        java.util.Date date = new java.util.Date();
        //        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss"); // ????????
        //        String filename = format.format(date) + ".jpg";
        //        java.io.File fileFolder = new java.io.File(android.os.Environment.getExternalStorageDirectory()
        //                + "/vy/");
        //        if (!fileFolder.exists()) { // ?????????????????????"finger"????
        //            fileFolder.mkdir();
        //        }
        //        java.io.File jpgFile = new java.io.File(fileFolder, filename);
        //        java.io.FileOutputStream outputStream = new java.io.FileOutputStream(jpgFile); // ????????
        //        outputStream.write(data); // д??sd????
        //        outputStream.close(); // ????????

        //		try {
        //			Thread.sleep(500);
        //		} catch (InterruptedException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //		}
    }

    public void sendText(int toWhom, String text, String myname) {
        Session.getInstance().sendTextMessage(toWhom, text, null);
    }

    //	 fromID????????
//	 msgType?? 0??????????? 2??????
//	 format??????
    private void OnReceiveText(int fromID, int Type, String text, JSONObject arg3) {
        log("OnReceiveText ID" + fromID + " says " + text + "type" + Type);
    }

    @Override
    public void onClick(View id) {
        int i = id.getId();
        if (i == R.id.show_messge_btn) {
            sendText(-1, "text message test", "user" + uid);

        } else {
        }
    }

    // viewpager
    public class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        //??????item
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return _fragment_meeting;
                case 1:
                    return _fragment_share;
            }
            return null;
        }

        // ??????????????
        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            // TODO Auto-generated method stub
            return super.instantiateItem(arg0, arg1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            System.out.println("position Destory" + position);
            super.destroyItem(container, position, object);
        }

    }


//    @Override
//    public void OnPenClick(boolean bShowPoints) {
//        // TODO Auto-generated method stub
//        Log.d("emm", "OnPenClick");
//    }


    /**
     * ?????????
     */
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

    /**
     * ?????????
     */
    @Override
    public void onConnect(int status, int quality) {
        log("onConnect " + status + " " + quality);
        _pager.setCanScroll(true);
        if (status == 0) {
            int meetingType = Session.getInstance().getMeetingType();
            if (meetingType == 0 || meetingType == 1 || meetingType == 2 || meetingType == 3 || meetingType == 4 || meetingType == 5 || meetingType
                    == 6) {
            } else {
                Session.getInstance().LeaveMeeting();
                Toast.makeText(this, getString(R.string.meeting), Toast.LENGTH_LONG).show();
            }
        } else {
            if (_fragment_meeting._buttonStart != null)
                _fragment_meeting._buttonStart.setEnabled(true);
            Session.getInstance().setM_bInmeeting(false);
            onConnect(MainActivity.this, status);
        }
    }

    /**
     * ?????????????????????????????????????????????
     * name:???????
     * peerID:?????????id
     * params:???????
     * <p>
     * ?????   ???????
     */
    @Override
    public void onCallClientFunction(String name, int peerID, Object params) {
        // TODO Auto-generated method stub
        log("onCallClientFunction Name" + name + "peerId" + peerID + "params" + params);

    }

    /**
     * ???????
     */
    @Override
    public void onDisConnect(int arg0) {
        log("onDisconnect " + arg0);
        _myPeerID = 0;
        _watchingPeerID = 0;
        uid = 0;
        _userList.clear();
        _pager.setCanScroll(false);
        if (_fragment_meeting._buttonSend != null) {
            _fragment_meeting._buttonSend.setText(R.string.main_send_video);
            _fragment_meeting._buttonWatch.setText(R.string.main_watch_video);
            _fragment_meeting._buttonFreeSpeak.setText(R.string.main_free_speaker);
            _fragment_meeting._buttonStart.setEnabled(true);
        }
    }

    /**
     * ????????????
     */
    @Override
    public void onEnablePresence(int peerID) {

        _myPeerID = peerID;
        sendText(0, "i am " + android.os.Build.MODEL + android.os.Build.VERSION.RELEASE, android.os.Build.MODEL);
    }

    /**
     * ??????
     * fromid:???????user id
     * type?????????
     * msg?????????
     */
    @Override
    public void onRecTextMsg(int fromid, int type, String msg, JSONObject arg3) {
        OnReceiveText(fromid, type, msg, arg3);
    }


    @Override
    public void onRemoteDelMsg(String msgName, int fromID, int associatedUserID,
                               String id, String associatedMsgID, Object body) {
        log("onRemoteDelMsg msgName" + msgName + "fromId" + fromID);
    }

    /**
     * ?????????
     * msgName???????
     * fromId?????????user??id
     * associatedUserID??????userid
     */
    @Override
    public void onRemotePubMsg(String msgName, int fromID, int associatedUserID,
                               String id, String associatedMsgID, Object body) {
        log("onRemotePubMsg msgName" + msgName + "fromId" + fromID);

    }

    /**
     * ??????????
     */
    @Override
    public void onUserIn(int peerId, boolean inList) {

        log("ClientFunc_UserIn " + peerId);
        //		if(!inList){
        _userList.add(peerId);
        //		}
        if (!_serverMix && hasAudio)
            Session.getInstance().playAudio(peerId);
    }


    /**
     * ??????????
     */
    @Override
    public void onUserOut(MeetingUser arg0) {
        log("ClientFunc_UserOut " + arg0.getPeerID());
        _userList.remove(new Integer(arg0.getPeerID()));

        if (!_serverMix && hasAudio)
            Session.getInstance().unplayAudio(arg0.getPeerID());
        if (_watchingPeerID == arg0.getPeerID()) {
            _watchingPeerID = 0;
            if (_fragment_meeting != null && _fragment_meeting._buttonWatch != null)
                _fragment_meeting._buttonWatch.setText(R.string.main_watch_video);
        }

    }

    /**
     * ???????User????????仯
     * peerID:?仯user??id
     * arg0:?仯??????
     */
    @Override
    public void onUserPropertyChange(int peerID, JSONObject arg0) {
        log("UserChange PeerID" + peerID + "JsonObject" + arg0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Session.getInstance().LeaveMeeting();
        _myPeerID = 0;
        uid = 0;
        _watchingPeerID = 0;
        _userList.clear();
    }


    @Override
    public void ChangeAudioStatus(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onCameraDidOpen(Camera arg0, boolean arg1, int arg2) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onCameraWillClose(Camera arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onPresentComplete() {
        // TODO Auto-generated method stub

    }


    @Override
    public void showpage() {
        // TODO Auto-generated method stub

    }


    @Override
    public void syncVideoModeChange(boolean arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onGotMeetingProperty(JSONObject arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onServerRecording(boolean arg0) {
        // TODO Auto-generated method stub

    }


    private static void onConnect(Activity activity, int nRet) {
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
    public void onCallSipACK(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onFocusSipChange(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onFocusUserChange(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onVideoSizeChanged(int arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onWhitePadPageCount(int arg0) {
        // TODO Auto-generated method stub

    }

}
