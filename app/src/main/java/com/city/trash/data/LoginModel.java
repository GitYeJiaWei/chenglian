package com.city.trash.data;

import com.city.trash.bean.BaseBean;
import com.city.trash.bean.LoginBean;
import com.city.trash.data.http.ApiService;
import com.city.trash.presenter.contract.LoginContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class LoginModel implements LoginContract.ILoginModel{
    private ApiService mApiService;

    public LoginModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<LoginBean>> login(String userName, String password) {
        Map<String,String> params = new HashMap<>();
        params.put("userName",userName);
        params.put("password",password);
        return mApiService.login(params);
    }
}
