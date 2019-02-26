package com.city.trash.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.LoginBean;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.city.trash.di.component.DaggerLoginComponent;
import com.city.trash.di.module.LoginModule;
import com.city.trash.presenter.LoginPresenter;
import com.city.trash.presenter.contract.LoginContract;
import com.city.trash.ui.widget.LoadingButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 1.MVP 大家都知道 P的作用是让MV间接拥有肮脏的PY交易，而不是直接让他们进行交易。
 * 2.Rxjava 响应式编程 0.0 一个特别屌的地方就是你可以随便切换线程,异步
 * 3.Retrofit 代替Volley的东东，网络请求
 * 4.Dagger2 Android 的IOC框架，即控制反转，也叫依赖注入，解耦用的
 * 4.DataBinding MVVM的东东，用起来比较方便，可以让bean与View绑定，抛弃setText()!
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.LoginView {

    @BindView(R.id.rfid)
    ImageView rfid;
    @BindView(R.id.txt_mobi)
    EditText txtMobi;
    @BindView(R.id.txt_password)
    EditText txtPassword;
    @BindView(R.id.btn_login)
    LoadingButton btnLogin;

    public static final String USER_NAME = "username";
    public static final String PASS_WORD = "password";

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        //mPresenter 不再以new的形式创建
        //Dagger就是依赖注入，解耦用的。常见的使用地方就是注入Presenter到Activity中
        //其实就是使用Component创建一个Presenter，而Presenter所需的参数都是由Moudule提供的

        DaggerLoginComponent.builder().appComponent(appComponent).loginModule(new LoginModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        String username = ACache.get(AppApplication.getApplication()).getAsString(USER_NAME);
        String password = ACache.get(AppApplication.getApplication()).getAsString(PASS_WORD);

        if (!TextUtils.isEmpty(username)) {
            txtMobi.setText(username);
        }
        if (!TextUtils.isEmpty(password)) {
            txtPassword.setText(password);
        }
    }

    @Override
    public void loginResult(BaseBean<LoginBean> baseBean) {
        if (baseBean == null)
        {
            ToastUtil.toast("操作失败");
            return;
        }
        String message = "";
        if (baseBean.success()){
            message = "登陆成功";
            ACache.get(AppApplication.getApplication()).put(USER_NAME,txtMobi.getText().toString());
            ACache.get(AppApplication.getApplication()).put(PASS_WORD,txtPassword.getText().toString());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("Login", baseBean);
            startActivity(intent);
            finish();
        }else {
            message = "账号或密码错误";
        }
        ToastUtil.toast(message);

    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        String username = txtMobi.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.toast("请输入账号");
            return;
        }
        String password = txtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toast("请输入密码");
            return;
        }
        mPresenter.login(username, password);
    }

    @Override
    public void showLoading() {
        btnLogin.showLoading();
    }

    @Override
    public void showError(String msg) {
        btnLogin.showButtonText();
        ToastUtil.toast("登录失败：" + msg);
    }

    @Override
    public void dismissLoading() {
        btnLogin.showButtonText();
    }
}
