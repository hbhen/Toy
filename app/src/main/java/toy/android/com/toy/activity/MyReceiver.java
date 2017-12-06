package toy.android.com.toy.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import toy.android.com.toy.service.ControlPlayService;
import toy.android.com.toy.service.VideoService;
import toy.android.com.toy.utils.ToastUtil;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private static final String TAGD = "circle";
    Intent controlMusicIntent = new Intent();
    private String mRoomId;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            String result = bundle.getString(JPushInterface.EXTRA_ALERT);
            try {
                JSONObject obj = new JSONObject(result);
                final JSONObject params = obj.getJSONObject("param");
                String cmd = obj.get("cmd").toString();//从服务器返回的cmd 当前的任务
                //控制播放
                if (cmd.equals("control_play")) {
                    String playUrl = (String) params.get("url");
                    Log.i(TAG, "onReceive: resid" + playUrl);
                    String method = params.getString("method");
                    int mediaControlMethod = Integer.parseInt(method);
                    switch (mediaControlMethod) {
                        case 1:
                            //播放
                            /**
                             * 1,当前没有播放(不是暂停和停止状态),收到1的按钮,拿resid申请播放,要不要后台播放(不要)
                             * 2,当前正在播放,是同一个resid,则不重新加载resid.其他的resid排队,等播放完上一首,再去申请播放(按时间顺序来排)
                             * 3,当前没有播放(出于暂停状态),不重新加载resid,继续播放当前的resid
                             */
                            controlMusicIntent.putExtra("method", method);
                            controlMusicIntent.putExtra("url", playUrl);
                            controlMusicIntent.setClass(context, ControlPlayService.class);
                            context.startService(controlMusicIntent);//开启服务
                            break;
                        case 2://暂停
                            /**
                             * 1,当前正在播放,则暂停播放
                             * 2,当前没有播放,则不做操作
                             * */
                            controlMusicIntent.putExtra("method", method);
                            controlMusicIntent.setClass(context, ControlPlayService.class);
                            context.startService(controlMusicIntent);
                            break;
                        case 3://停止
                            controlMusicIntent.putExtra("method", method);
                            controlMusicIntent.setClass(context, ControlPlayService.class);
                            context.startService(controlMusicIntent);
                            /**
                             * 1,当前正在播放,停止播放,清除resid
                             * 2,当前暂停,停止播放.清除resid
                             * */
                            break;
                        case 4://切换下一首
                            controlMusicIntent.putExtra("method", method);
                            controlMusicIntent.putExtra("url", params.getString("url"));
                            controlMusicIntent.setClass(context, ControlPlayService.class);
                            context.startService(controlMusicIntent);
                            /**
                             * 1,当前正在播放,替换resid,播放
                             * 2,暂停,替换resid,播放
                             * 3,停止,替换resid,播放
                             * */
                            break;
                        default:
                            break;
                    }
                }

                //调节玩具音量 这里有个问题,就是音量的值应该怎么取舍??
                else if (cmd.equals("volume")) {
                    //然后设置  设置系统,多媒体,通话的音量的api  不用service吧  ,用通知就行了.
                    boolean isVideo = true;
                    boolean isMedia = true;
                    if (isVideo) {
                        String valueS = params.getString("value");
                        int value = Integer.parseInt(valueS);
                        int calvalue = value * 15 / 100;
                        Log.i(TAG, "volume" + value);
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, calvalue, audioManager
                                .FLAG_PLAY_SOUND
                                | AudioManager.FLAG_SHOW_UI);
                    } else if (isMedia) {
                        String valueS = params.getString("value");
                        int value = Integer.parseInt(valueS);
                        int calvalue = value * 15 / 100;
                        Log.i(TAG, "volume" + value);
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, calvalue, audioManager
                                .FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                    }
                }

                //语音视频通话
                else if (cmd.equals("contact_toy")) {
                    String method = params.getString("method");
                    mRoomId = params.getString("room");
                    Log.i(TAG, "method: " + method);
//                    switch (videoControlMethod) {
//                        case 1://连接房间
                    intent.setClass(context, VideoService.class);
//                            intent.putExtra("method", 1);
//                            intent.putExtra("roomid", mRoomId);
//                            context.startService(intent);
//                            Log.i("context", "video" + videoControlMethod);
////                            //把房间号传到videoactivity里面去
//                            ToastUtil.showToast(context, "开始通话" + mRoomId);
//                            Log.i(TAG, "开始通话" + mRoomId);
//                            break;
                    intent.putExtra("method", method);
                    intent.putExtra("roomid", mRoomId);
                    context.startService(intent);
                    ToastUtil.showToast(context, "通话" + mRoomId);
                    Log.d(TAGD, "(MyReceiver)onReceive: went" + method);
//                        case 2://关闭通话
//                            Log.i("context", "video" + videoControlMethod);
//                            ToastUtil.showToast(context, "关闭通话" + mRoomId);
//                            Log.i(TAG, "关闭通话" + mRoomId);
//                            break;
//                        case 3:
//                            Log.i("context", "video" + videoControlMethod);
//                            ToastUtil.showToast(context, "未知" + mRoomId);
//                            Log.i(TAG, "未知" + mRoomId);
//                            break;
//                        default:
//                            break;
//                    }
                }
                //控制录音
                else if (cmd.equals("recordvolume")) {
                    ListenerManager.getInstance().sendBroadCast("recordvolume", params);
                    String methodString = params.getString("method");
                    String recordUrl = params.getString("url");
                    int methodInt = Integer.parseInt(methodString);
                    switch (methodInt) {
                        case 1://播放
                            controlMusicIntent.putExtra("method", methodString);
                            controlMusicIntent.putExtra("url", recordUrl);
                            controlMusicIntent.setClass(context, ControlPlayService.class);
                            context.startService(controlMusicIntent);
                            break;
                        case 2://停止
                            controlMusicIntent.putExtra("method", 3);//这里直接传一个3过去,因为它和Mucic有区别,Music是三个控制,Music的控制是1,
                            // play;2,pause;3,stop;4,next;Record的控制是1,play;2,stop;
                            controlMusicIntent.setClass(context, ControlPlayService.class);
                            context.startService(controlMusicIntent);
                            break;
                        default:
                            break;
                    }
                }
                //不需要这个接口,通过心跳接口定时上传后台就行.
                //拉取当前状态
                else if (cmd.equals("current_status")) {
//                    ListenerManager.getInstance().sendBroadCast("current_status", null);
                    Log.i(TAG, "onReceive: ++++++++");
                    ToastUtil.showToast(context, "current_status");
                }

                //更新版本
                else if (cmd.equals("update")) {
                    ListenerManager.getInstance().sendBroadCast("update", params);
                    String updateUrl = params.getString("url");//更新地址
                    /**
                     *拿到地址开始下载 开启服务
                     *  1,后台自动下载
                     *  2,下载好了之后自动安装
                     *  3,安装好之后自动启动
                     */

                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                } else if (cmd.equals("TOYSP")) {

                    ToastUtil.showToast(context, params.toString());
                    Log.i(TAG, "onReceive: params" + params.toString());

                } else if (cmd.equals("准备接收网络指令")) {
                    //开启一个service,等待用户发送指令
                } else if (cmd.equals("接收指令并比对填充wlan")) {
                    //1,开启的是声波解码activity,拿到wlan的名称,和密码.
                    Intent wlanIntent = new Intent();
                    wlanIntent.setClass(context, WifiActivity.class);
                    wlanIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(wlanIntent);

                    //2,拿到密码去设置,不对,发出未连接成功声音.对,发出连接成功的声音

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

//			if (map.get())
//			ListenerManager.getInstance().sendBroadCast("control_play",map);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            //打开自定义的Activity
//        	Intent i = new Intent(context, TestActivity.class);
//        	i.putExtras(bundle);
//        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        	context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            /**
             * 保存服务器推送下来的附加字段.(JSON字符串)
             * 对应API消息内容的extras字段
             * 对应Protal推送消息界面上的"可选设置"里的附加字段.
             * */
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

}
