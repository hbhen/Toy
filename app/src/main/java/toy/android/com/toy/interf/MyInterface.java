package toy.android.com.toy.interf;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import toy.android.com.toy.bean.ActiveToyResBean;
import toy.android.com.toy.bean.ToyLoginResBean;
import toy.android.com.toy.bean.ToyLogoutResBean;
import toy.android.com.toy.bean.ToyQuitRoomBySelfResBean;


/**
 * Created by Android on 2017/8/18.
 */

public interface MyInterface {
    @GET("login")
    Call<ToyLoginResBean> TOY_LOGIN_RES_BEAN_CALL(@Query("params") String params);

    @GET("heart")
    Call<ActiveToyResBean> ACTIVE_TOY_RES_BEAN_CALL(@Query("params") String params);

    @GET("login")
    Call<ToyLogoutResBean> TOY_LOGOUT_RES_BEAN_CALL(@Query("params") String params);

    @GET("busi")
    Call<ToyQuitRoomBySelfResBean> TOY_QUIT_ROOM_BY_SELF_RES_BEAN_CALL(@Query("params") String params);
}
