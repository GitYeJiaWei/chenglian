package com.city.trash.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.city.trash.AppApplication;
import com.city.trash.common.util.NetUtils;
import com.city.trash.di.component.AppComponent;
import com.city.trash.presenter.BasePresenter;
import com.city.trash.ui.BaseView;
import com.city.trash.ui.receiver.NetworkChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView
{

    private Unbinder mUnbinder;

    protected AppApplication mApplication;

    private Toast mToast = null;
    private ProgressDialog waitDialog = null;
    protected boolean mCheckNetWork = true; //默认检查网络状态

    @Inject
    public T mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(setLayout());

        mUnbinder = ButterKnife.bind(this);
        this.mApplication = (AppApplication) getApplication();
        setupAcitivtyComponent(mApplication.getAppComponent());
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //在无网络情况下打开APP时，系统不会发送网络状况变更的Intent，需要自己手动检查
        hasNetWork(NetUtils.isConnected(this));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mUnbinder!=null&&mUnbinder != Unbinder.EMPTY)
        {

            mUnbinder.unbind();
        }
        if (waitDialog != null)
        {
            waitDialog = null;
        }
        EventBus.getDefault().unregister(this);
    }

    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init();


    @Override
    public void showLoading()
    {
        if (waitDialog == null)
        {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage("加载中...");
        waitDialog.show();
    }

    @Override
    public void showError(String msg)
    {
        if (waitDialog != null)
        {
            waitDialog.setMessage(msg);
            waitDialog.show();
        }
    }

    @Override
    public void dismissLoading()
    {
        if (waitDialog != null && waitDialog.isShowing())
        {
            waitDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeEvent(NetworkChangeEvent event)
    {
        hasNetWork(event.isConnected);
    }

    private void hasNetWork(boolean has)
    {
        if (isCheckNetWork())
        {
            handleNetWorkTips(has);
        }
    }

    protected void handleNetWorkTips(boolean has)
    {
    }
    public void setCheckNetWork(boolean checkNetWork)
    {
        mCheckNetWork = checkNetWork;
    }

    public boolean isCheckNetWork()
    {
        return mCheckNetWork;
    }

}
