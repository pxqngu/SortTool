package com.pxqngu.sorttool;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * 创建日期：18-3-10 on 下午12:39
 * 描述:
 * 作者:彭辛乾 pxqngu
 */
public class UpdateIndex extends AsyncTask<Void , Integer , Boolean> {
    private ProgressBar mprogressBar;
    private TextView mshowText;
    private List<String> sqls = null;
    private Button mStartButton = null;
    private Button mExitButton = null;

    UpdateIndex(ProgressBar mprogressBar ,
                TextView mshowText ,
                List<String> sqls ,
                Button mStartButton ,
                Button mExitButton){
        this.mprogressBar = mprogressBar;
        this.mshowText = mshowText;
        this.sqls = sqls;
        this.mStartButton = mStartButton;
        this.mExitButton = mExitButton;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mprogressBar.setMax(sqls.size());
        mprogressBar.setVisibility(View.VISIBLE);
        mshowText.setText("开始更新序号!");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        DBUtil.db.beginTransaction();
        try {
            for (int i = 0 ; i < sqls.size() ; i++){
                DBUtil.db.execSQL(sqls.get(i));
                publishProgress(i);
            }
            DBUtil.db.setTransactionSuccessful();
        } catch (Exception ex){
            return false;
        }finally {
            DBUtil.db.endTransaction();
        }
        return true;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mprogressBar.setProgress(values[0]+1);
        mshowText.setText(values[0] + 1 + "/" + sqls.size());
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean){
            mshowText.append("设置成功,请退出!");
            mStartButton.setVisibility(View.GONE);
            mExitButton.setVisibility(View.VISIBLE);
            DBUtil.db.close();
        }
    }
}
