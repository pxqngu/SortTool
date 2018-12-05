package com.pxqngu.sorttool;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：18-3-17 on 下午5:52
 * 描述:
 * 作者:彭辛乾 pxqngu
 */
public class DBUtil{
    /**
     * 数据库文件名
     */
    private final String DB_NAME="cbjdb.db";

    /**
     * SDcard根目录
     */
    private final String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 数据库文件路径
     */
    private final String DBFilePath = SD_ROOT  + "/JSL_CBData/" + DB_NAME;

    /**
     * 数据库文件
     */
    private File DBFile;

    /**
     * sqliteDatabase对象
     */
    public static SQLiteDatabase db;

    /**
     * 构造方法
     */
    DBUtil(){
        DBFile = new File(DBFilePath);
        db = null;
    }

    /**
     * 打开数据库文件
     * @return
     */
    public boolean openSQLiteDB(int flag){
        if (DBFile.exists()){
            db = SQLiteDatabase.openDatabase(DBFilePath ,
                    null ,
                    flag);
            return true;
        }
        return false;
    }

    /**
     * 获取组名列表
     * @return
     */
    public List<String> getGroupNames(){
        List<String> colList = new ArrayList<>();
        String sql = "select distinct BOOKNUM from USERINFO";
        Cursor cursor = null;

        if (openSQLiteDB(SQLiteDatabase.OPEN_READWRITE)){
            cursor = db.rawQuery(sql , null);

            if (cursor.moveToFirst()){
                do {
                    colList.add(cursor.getString(cursor.getColumnIndex("BOOKNUM")));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return colList;
    }
}
