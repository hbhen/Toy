package com.example.meetingsdkdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

import info.emm.meeting.Session;
import info.emm.sdk.VideoView;

//import com.weiyicloud.whitepad.SharePadMgr;

@SuppressLint("ValidFragment")
public class MeetingFragment extends Fragment {

	MainActivity _activity;
	LayoutInflater _inflater;
	View _fragmentView;

	public info.emm.sdk.VideoView surface1 = null;
	public info.emm.sdk.VideoView surface2 = null;
	public TextView textView = null; 

	public EditText _editIP   = null;
	public EditText _editPort = null;
	public EditText editMeetingId = null;

	public CheckBox _checkFront = null;
	public CheckBox _checkHQ = null;
	public CheckBox _checkPause = null;
	public CheckBox _checkSpeaker = null;

	public Button _buttonStart = null;
	public Button _buttonStop = null;
	public Button _buttonSend  = null;
	public Button _buttonWatch = null;
	public Button _buttonFreeSpeak = null;
	public Button _buttonSendMessage = null;

	@SuppressLint("ValidFragment")
	public MeetingFragment(MainActivity activity){
		_activity = activity;
	}

	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
	{  
		if (_fragmentView == null) {
			_inflater = inflater;
			_fragmentView = inflater.inflate(R.layout.meetingfragment, null); 
			surface1 = (VideoView) _fragmentView.findViewById(R.id.surfaceView1);
			surface2 = (VideoView) _fragmentView.findViewById(R.id.surfaceView2);
			textView = (TextView) _fragmentView.findViewById(R.id.textView1);
			_checkFront = (CheckBox)  _fragmentView.findViewById(R.id.checkBox1);
			if(_checkFront != null)
			{
				_checkFront.setEnabled(_activity.hasfront);
				_checkFront.setChecked(_activity.usefront);
				_checkFront.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {

						if(!_activity.hasfront)
							return;

						_activity.usefront = !_activity.usefront;
						Session.getInstance().switchCamera(_activity.usefront);
					}
				});
			}

			_checkHQ = (CheckBox)  _fragmentView.findViewById(R.id.checkBox2);
			if(_checkHQ != null)
			{
				_checkHQ.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {

						Session.getInstance().setCameraQuality(_checkHQ.isChecked());
					}
				});
			}

			_checkPause = (CheckBox)  _fragmentView.findViewById(R.id.checkBox3);
			if(_checkPause != null)
			{
				_checkPause.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {

						if(_checkPause.isChecked())
						{
							Session.getInstance().pauseLocalVideo();
						}
						else
						{
							Session.getInstance().resumeLocalVideo();
						}

					}
				});
			}

			_checkSpeaker = (CheckBox) _fragmentView.findViewById(R.id.checkBox4);
			if(_checkSpeaker != null)
			{
				_checkSpeaker.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						Session.getInstance().setLoudSpeaker(_checkSpeaker.isChecked());
					}
				});
			}

			_buttonStart = (Button) _fragmentView.findViewById(R.id.button1);
			if(_buttonStart != null)
			{
				_buttonStart.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						if (!imm.isActive()) {
							return;
						}
						imm.hideSoftInputFromWindow(view.getWindowToken(), 0);		            	

						Start(false, 0);
					}
				});
			}

			_buttonStop = (Button) _fragmentView.findViewById(R.id.button2);
			if(_buttonStop != null)
			{
				_buttonStop.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {

						log("Leave");
						Session.getInstance().LeaveMeeting();


						_activity._myPeerID = 0;
						_activity._watchingPeerID = 0;
						_activity._userList.clear();
						_buttonSend.setText(R.string.main_send_video);
						_buttonWatch.setText(R.string.main_watch_video);
						_buttonFreeSpeak.setText(R.string.main_free_speaker);
					}
				});
			}

			_editIP   = (EditText) _fragmentView.findViewById(R.id.editTextIP);
			_editPort = (EditText) _fragmentView.findViewById(R.id.editTextPort);
			editMeetingId = (EditText) _fragmentView.findViewById(R.id.editTextMeetingId);

			_buttonSend = (Button) _fragmentView.findViewById(R.id.buttonSend);
			if(_buttonSend != null)
			{
				_buttonSend.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						if(_activity._myPeerID == 0)
							return;

						if(!_activity._sendingVideo)
						{
							Session.getInstance().PlayVideo(0, true, surface1, 0, 0, 1, 1, 0, false, 0, 0);
							_activity._sendingVideo = true;

						}
						else
						{
							Session.getInstance().PlayVideo(0, false, surface1, 0, 0, 1, 1, 0, false, 0, 0);
							_activity._sendingVideo = false;

						}

						_buttonSend.setText(_activity._sendingVideo ? R.string.main_unsend_video : R.string.main_send_video);
					}
				});
			}

			_buttonWatch = (Button)  _fragmentView.findViewById(R.id.buttonWatch);
			if(_buttonWatch != null)
			{
				_buttonWatch.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						if(_activity._userList.size() == 0)
							return;

						if(_activity._watchingPeerID == 0)
						{
							int peerID = _activity._userList.get(0);
							Session.getInstance().PlayVideo(peerID, true, surface2, 0, 0, 1, 1, 0, false, 0, 0);

							_activity._watchingPeerID = peerID;
						}
						else
						{
							Session.getInstance().PlayVideo(_activity._watchingPeerID, false, surface2, 0, 0, 1, 1, 0, false, 0, 0);
							_activity._watchingPeerID = 0;
						}

						_buttonWatch.setText(_activity._watchingPeerID == 0 ? R.string.main_watch_video : R.string.main_unwatch_video);
					}
				});
			} 

			_buttonSendMessage = (Button) _fragmentView.findViewById(R.id.show_messge_btn);
			_buttonSendMessage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					_activity.sendText(-1, "text message test 123", "user" + _activity.uid);
				}
			});

			_buttonFreeSpeak = (Button) _fragmentView.findViewById(R.id.free_speaker);
			if(_buttonFreeSpeak != null){


				_buttonFreeSpeak.setOnClickListener(new OnClickListener() {


					@Override
					public void onClick(View arg0) {

						if(_activity._myPeerID == 0)
							return;

						if(!_activity._freeSpeak)
						{
							Session.getInstance().requestSpeaking(_activity._myPeerID);
							_activity._freeSpeak = true;

						}
						else
						{
							Session.getInstance().StopSpeaking();
							_activity._freeSpeak = false;

						}

						_buttonFreeSpeak.setText(_activity._freeSpeak ? R.string.main_unfree_speaker : R.string.main_free_speaker);
					}
				});
			}

		}else {
			ViewGroup parent = (ViewGroup) _fragmentView.getParent();
			if (parent != null) {
				parent.removeView(_fragmentView);
			}			
		}

		return _fragmentView;  
	}

	public void Start(boolean serverMix, int codec)
	{
		_buttonStart.setEnabled(false);
		_activity._serverMix = serverMix;
		_activity._codec = codec;
		_activity._warningtime = 0;

		_activity.uid = (int)(Math.random() * 100000);
		String ip = _editIP.getText().toString();
		int port  = Integer.parseInt(_editPort.getText().toString());
		String meetingId = editMeetingId.getText().toString();
		Session.getInstance().setWebHttpServerAddress(ip+":"+port);
		Session.getInstance().switchCamera(_activity.usefront);
		Session.getInstance().setCameraQuality(_checkHQ.isChecked());
		Session.getInstance().setLoudSpeaker(_checkSpeaker.isChecked());

//		SharePadMgr.getInstance().setShareControl(Session.getInstance());
//		SharePadMgr.getInstance().setAppContext(getActivity());
//		SharePadMgr.getInstance().setControlMode(ControlMode.fullcontrol);
//		SharePadMgr.getInstance().setClient(Session.getInstance().client);
		Session.getInstance().joinmeeting(ip, port, "user" + _activity.uid, meetingId, "", _activity.uid, 0, null);

	}

	public void log(String str)
	{
		if(textView != null)
			textView.setText(str);

		Log.i("test", str);
	}
	public String getRandom(int count){
		StringBuffer sb = new StringBuffer();
		String str = "0123456789";
		Random r = new Random();
		for(int i=0;i<count;i++){
			int num = r.nextInt(str.length());
			sb.append(str.charAt(num));
			str = str.replace((str.charAt(num)+""), "");
		}
		return sb.toString();
	}
}
