package com.city.trash.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.LoginBean;
import com.city.trash.common.util.ACache;
import com.city.trash.common.util.NetUtils;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.ui.adapter.DrawerListAdapter;
import com.city.trash.ui.adapter.DrawerListContent;
import com.city.trash.ui.fragment.BaseFragment;
import com.city.trash.ui.fragment.HomeFragment;

/**
 *
 */
public class MainActivity extends UHFBaseActivity
{
    public static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private String[] mOptionTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View headerView;
    private View mNetWorkTips;
    public BaseBean<LoginBean> mUserInfo;

    private int pressKey;

    @Override
    public int setLayout()
    {
        return R.layout.activity_main;
    }

    @Override
    public void init()
    {
        super.init();
        Intent intent = getIntent();
        if (intent != null)
        {
            mUserInfo = (BaseBean<LoginBean>) intent.getSerializableExtra("Login");
        }
        if (mUserInfo == null)
        {
            ToastUtil.toast("获取数据失败");
            finish();
        }
        initview();
        selectItem(0);
    }

    //网络检测
    @Override
    protected void handleNetWorkTips(boolean has)
    {
        if (has)
        {
            mNetWorkTips.setVisibility(View.GONE);
        } else
        {
            mNetWorkTips.setVisibility(View.VISIBLE);
        }
    }

    private void initview()
    {
        mOptionTitles = getResources().getStringArray(R.array.options_array);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        //官方导航栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.show();

        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header, mDrawerList, false);
        mDrawerList.addHeaderView(headerView);
        TextView mTxt_username = headerView.findViewById(R.id.txt_username);
        mTxt_username.setText(ACache.get(AppApplication.getApplication()).getAsString("username"));

        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_list_item, DrawerListContent.ITEMS));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mNetWorkTips = findViewById(R.id.network_view);
        mNetWorkTips.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });
    }

    /**
     * The click listener for ListView in the navigation drawer
     * 点击左侧抽屉的item
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            mDrawerLayout.closeDrawer(mDrawerList);
            if (position == 0)//headView click
            {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            } else
            {
                String mOptionTitle = mOptionTitles[position];
                if (!NetUtils.isConnected(MainActivity.this))
                {
                    ToastUtil.toast(R.string.error_network_unreachable);
                    return;
                }
                if (mOptionTitle.equals(MainActivity.this.getResources().getString(R.string.action_refresh)))
                {
                    MainActivity.this.selectItem(1);
                }else
                {
                    selectItem(position);
                }
            }
        }
    }


    public void selectItem(int position)
    {
        // update the no_items content by replacing fragments
        Fragment fragment = null;

        switch (position)
        {
            case 0:
                fragment = HomeFragment.newInstance();
                break;
            case 6:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;

        }

        replaceFragment(position, fragment);
    }

    public void replaceFragment(int position, Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0)
        {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).commit();
        } else
        {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mOptionTitles[position]);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && position == 0)
        {
            actionBar.setHomeAsUpIndicator(R.mipmap.button_daohang);
        } else
        {
            actionBar.setHomeAsUpIndicator(null);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    @Override
    protected void setBarCode(String barCodeText)
    {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment != null)
        {
            ((BaseFragment) fragment).setBarCode(barCodeText);
        }
    }

    @Override
    public void onBackPressed()
    {
        mDrawerList.setItemChecked(0, true);
        setTitle(mOptionTitles[0]);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.mipmap.button_daohang);
        }
        try
        {
            super.onBackPressed();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (pressKey == 0)
        {
            if (Build.PRODUCT.equals("c71"))//c71
            {
                pressKey = 139;
            } else if (Build.PRODUCT.startsWith("c72"))
            {
                pressKey = 280;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (pressKey == 0)
        {
            if (Build.PRODUCT.equals("c71"))//c71
            {
                pressKey = 139;
            } else if (Build.PRODUCT.equals("c72"))
            {
                pressKey = 280;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                if (fragment != null)
                {
                    if (fragment instanceof HomeFragment)
                    {
                        if (mDrawerLayout.isDrawerVisible(mDrawerList))
                        {
                            mDrawerLayout.closeDrawer(mDrawerList);
                        } else
                        {
                            mDrawerLayout.openDrawer(mDrawerList);
                        }
                    } else
                    {
                        if(isSoftShowing())
                        {
                            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        this.onBackPressed();
                    }
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        // 获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom != 0;
    }

}



