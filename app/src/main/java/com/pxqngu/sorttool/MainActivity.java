package com.pxqngu.sorttool;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pxqngu.sorttool.httputil.HttpCallbackListener;
import com.pxqngu.sorttool.httputil.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends Activity {
    private List<String> groupNames = null;
    private ArrayAdapter<String> spinnerAdapter = null;
    private DBUtil dbUtil = null;
    private List<String> jslDBSQL = null;
    @BindView(R.id.groupSpinner)    Spinner mGroup;
    @BindView(R.id.showText)        TextView mShowText;
    @BindView(R.id.jd)              ProgressBar progressBar;
    @BindView(R.id.startButton)     Button mStartButton;
    @BindView(R.id.exitButton)      Button mExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题拦
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //申请权限.
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                ).request();

        /**
         * 按钮单击处理
         */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowText.setText("开始下载数据...");
                mStartButton.setEnabled(false);
                //发送get请求,获取json文件
                HttpUtil.sendHttpRequest("https://pxqngu.github.io/zls/ordertool/"
                                + mGroup.getSelectedItem().toString().trim() + ".json",
                        new HttpCallbackListener() {
                    //请求成功
                    @Override
                    public void onFinish(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            jslDBSQL = new ArrayList<String>();
                            for (int i = 0 ; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //拼接sql语句
                                jslDBSQL.add("update USERINFO set ORDERING=" + "\'"
                                        + (jsonObject.getString("index")) + "\'" + " " +
                                        "where USERCODE=" + "\'" + jsonObject.getString("usercode") + "\'" +
                                        " and BOOKNUM=" + "\'"
                                        + mGroup.getSelectedItem().toString().trim() + "\'");
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                        /**
                         * 回到主线程启动asynctask设置顺序
                         */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mShowText.setText("数据下载成功!");
                                new UpdateIndex(progressBar , mShowText , jslDBSQL ,mStartButton ,mExitButton ).execute();
                            }
                        });
                    }

                    //请求失败
                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStartButton.setEnabled(true);
                                mShowText.setText("系统后台不存在该组数据,数据下载失败!");
                                Toast.makeText(MainActivity.this ,
                                        "系统后台不存在该组数据,数据下载失败!" , Toast.LENGTH_LONG).show();
                            }
                        });
                        e.printStackTrace();
                    }
                });
            }
        });

        //退出系统
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void loadingDataSuccess(){
        initSpinner();
    }

    @PermissionFail(requestCode = 100)
    public void loadingDataFail(){
        Toast.makeText(this , "程序需要读写SD卡的权限，否则无法运行！" , Toast.LENGTH_LONG).show();
        this.finish();
    }

    /**
     * 初始化spinner列表
     */
    private void initSpinner(){
        dbUtil = new DBUtil();
        //加载组列表
        groupNames = dbUtil.getGroupNames();
        spinnerAdapter = new ArrayAdapter<String>(this ,
                android.R.layout.simple_spinner_item ,
                groupNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroup.setAdapter(spinnerAdapter);
    }
}
