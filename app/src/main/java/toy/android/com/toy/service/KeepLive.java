package toy.android.com.toy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toy.android.com.toy.bean.ToyLoginReqBean;
import toy.android.com.toy.bean.ToyLoginResBean;
import toy.android.com.toy.interf.MyInterface;
import toy.android.com.toy.internet.Constants;
import toy.android.com.toy.utils.SPUtils;

public class KeepLive extends Service {
    private static final String TAG = "tag";
    private String mDevicecode;

    public KeepLive() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String deviceid = intent.getStringExtra("deviceid");
        mDevicecode = intent.getStringExtra("devicecode");
        ToyLogin(deviceid);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void ToyLogin(String deviceId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyInterface anInterface = retrofit.create(MyInterface.class);
        ToyLoginReqBean.BODYBean bodyBean = new ToyLoginReqBean.BODYBean(deviceId, "", mDevicecode, "A",
//                mVersionName);
                "1.0");
        ToyLoginReqBean toyLoginReqBean = new ToyLoginReqBean("REQ", "LOG", "", new SimpleDateFormat
                ("yyyyMMddHHmmssSSS").format(new Date()), bodyBean, "", "", "1");
        Gson gson = new Gson();
        String s = gson.toJson(toyLoginReqBean);
        Log.i(TAG, s);
        Call<ToyLoginResBean> toyLoginResBeanCall = anInterface.TOY_LOGIN_RES_BEAN_CALL(s);

        toyLoginResBeanCall.enqueue(new Callback<ToyLoginResBean>() {
            @Override
            public void onFailure(Call<ToyLoginResBean> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t);
            }

            @Override
            public void onResponse(Call<ToyLoginResBean> call, Response<ToyLoginResBean> response) {
//                ToastUtil.showToast(MainActivity.this, response.message());
                Log.i(TAG, "onResponse: toy login" + response.body().getTOKEN());
                Log.i(TAG, "onResponse: toy login" + response.body().getBODY());
                Log.i(TAG, "onResponse: toy login" + "成功了");
                String token = response.body().getTOKEN();
                SPUtils.putString(KeepLive.this, "token", token);
//                initDeviceInfo();
//                toyHeart(mVersionName, mWifiRssi, mRid, token);
            }
        });
    }
}
