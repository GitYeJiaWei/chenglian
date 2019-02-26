package com.city.trash.data.http;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.LoginBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiService
{


    String BASE_URL = "http://192.168.66.3:8107/api/";

    //http://192.168.66.3:8107/api/user/Login?userName=admin&password=123
    @GET("user/Login")//登录
    Observable<BaseBean<LoginBean>> login(@QueryMap Map<String ,String> params);

/*    @FormUrlEncoded
    @POST("Warehouse/Login")//登录
    public Observable<BaseBean<LoginBean>> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("EPC/InitEpc")//初始化标签
    public Observable<BaseBean> initEpc(@Field("epc") String jsonParam);

    @FormUrlEncoded
    @POST("Warehouse/GetInboundDeliveryPack")//仓库,获取内向交货单明细(sku列表)
    public Observable<BaseBean<List<Pack>>> getInboundDeliveryPack(@Field("docNo") String docNo);*/


}
