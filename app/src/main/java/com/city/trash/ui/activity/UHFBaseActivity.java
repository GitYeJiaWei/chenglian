package com.city.trash.ui.activity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.city.trash.R;
import com.city.trash.common.util.SoundManage;
import com.city.trash.common.util.ToastUtil;
import com.city.trash.di.component.AppComponent;
import com.rscja.deviceapi.Barcode1D;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public abstract class UHFBaseActivity extends BaseActivity
{
    public RFIDWithUHF mReader; //RFID
    private Barcode1D mInstance;
    private boolean threadStop = true;
    private boolean isBarcodeOpened = false;
    private ExecutorService executor;
    private Handler handler;


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

    }

    @Override
    public int setLayout()
    {
        return 0;
    }

    @Override
    public void init()
    {
        initUHF();
        initBarCode();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        executor = Executors.newFixedThreadPool(6);
        new InitBarCodeTask().execute();
    }

    private void initUHF()
    {
        try
        {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex)
        {
            ToastUtil.toast(ex.getMessage());
            return;
        }
        if (mReader != null)
        {
            new InitTask().execute();
        }
    }


    private class InitTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params)
        {
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);

            if (!result)
            {
                ToastUtil.toast("初始化RFID中 ...");
                initUHF();//初始化失败，则再次初始化
            } else
            {
                //ToastUtil.toast("init uhf success");
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
    }

    public void initBarCode()
    {
        try
        {
            mInstance = Barcode1D.getInstance();
        } catch (ConfigurationException e)
        {
            Toast.makeText(this, R.string.rfid_mgs_error_config,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg != null)
                {
                    String barCode = "";
                    switch (msg.arg1)
                    {
                        case 0:
                            barCode = getString(R.string.yid_msg_scan_fail);
                            SoundManage.PlaySound(UHFBaseActivity.this, SoundManage.SoundType.FAILURE);
                            ToastUtil.toast("扫描失败");
                            break;
                        case 1:
                            barCode = msg.obj.toString();
                            if (barCode != null && barCode.length() > 3)
                            {
                                if(!"566".equals(barCode.substring(0,3)))
                                {
                                    SoundManage.PlaySound(UHFBaseActivity.this, SoundManage.SoundType.FAILURE);
                                    ToastUtil.toast("该条码不是货兜单号，请重新扫描");
                                    return;
                                }
                                SoundManage.PlaySound(UHFBaseActivity.this, SoundManage.SoundType.SUCCESS);
                                setBarCode(barCode);
                            }else
                            {
                                SoundManage.PlaySound(UHFBaseActivity.this, SoundManage.SoundType.FAILURE);
                                ToastUtil.toast("条码数据错误，请重新扫描");
                            }
                            break;
                        default:
                            break;
                    }
                    threadStop = true;
                }
            }
        };
    }

    class GetBarcode implements Runnable
    {
        private boolean isContinuous = false;
        String barCode = "";
        private long sleepTime = 1000;
        Message msg = null;

        public GetBarcode(boolean isContinuous, int sleep)
        {
            this.isContinuous = isContinuous;
            this.sleepTime = sleep;
        }

        @Override
        public void run()
        {
            do
            {
                barCode = mInstance.scan();
                Log.i("MY", "barCode " + barCode.trim());
                msg = new Message();
                if (StringUtility.isEmpty(barCode))
                {
                    msg.arg1 = 0;
                    msg.obj = "";
                } else
                {
                    msg.arg1 = 1;
                    msg.obj = barCode;
                }
                handler.sendMessage(msg);
                if (isContinuous)
                {
                    try
                    {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            } while (isContinuous && !threadStop);
        }
    }

    /**
     * 设备上电异步类
     *
     * @author liuruifeng
     */
    public class InitBarCodeTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            return mInstance.open();
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            isBarcodeOpened = result;
            if (!result)
            {
                Toast.makeText(UHFBaseActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        threadStop = true;
        executor.shutdownNow();
        if (isBarcodeOpened)
        {
            mInstance.close();
        }
    }

    protected void setBarCode(String barCodeText)
    {

    }

    public void scan()
    {
        if (threadStop)
        {
            boolean bContinuous = false;
            int iBetween = 0;
/*            bContinuous = true;
            if (bContinuous)
            {
                threadStop = false;
                String strBetween = 100 + "";//间隔100ms
                iBetween = StringUtility.string2Int(strBetween, 0);// 毫秒
            }*/
            executor.execute(new GetBarcode(bContinuous, iBetween));
        } else
        {
            threadStop = true;
        }
    }


    @Override
    protected void onDestroy()
    {
        if (mReader != null)
        {
            mReader.free();
        }
        super.onDestroy();
    }
}

