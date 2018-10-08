package com.example.administrator.goldaappnew.db;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.administrator.goldaappnew.R;

public class DBManager {
    private static final String TAG = "DBManager";
    private final int BUFFER_SIZE = 1024;
    public static final String DB_NAME = "city_cn.s3db";
    public static final String PACKAGE_NAME = "com.example.administrator.goldaappnew";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"+ PACKAGE_NAME;
    private SQLiteDatabase database;
    private Context context;
    private File file=null;

    public DBManager(Context context) {
        Log.e(TAG, "DBManager");
        this.context = context;
    }

    public void openDatabase() {
        Log.e(TAG, "openDatabase()");
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }
    public SQLiteDatabase getDatabase(){
        Log.e(TAG, "getDatabase()");
        return this.database;
    }

    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            Log.e(TAG, "open and return");
            file = new File(dbfile);
            if (!file.exists()) {
                Log.e(TAG, "file");
                InputStream is = context.getResources().openRawResource(R.raw.city);
                if(is!=null){
                    Log.e(TAG, "is null");
                }else{
                }
                FileOutputStream fos = new FileOutputStream(dbfile);
                if(is!=null){
                    Log.e(TAG, "fosnull");
                }else{
                }
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count =is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                    Log.e(TAG, "while");
                    fos.flush();
                }
                fos.close();
                is.close();
            }
            database = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
            return database;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IO exception");
            e.printStackTrace();
        } catch (Exception e){
            Log.e(TAG, "exception "+e.toString());
        }
        return null;
    }
    public void closeDatabase() {
        Log.e(TAG, "closeDatabase()");
        if(this.database!=null)
            this.database.close();
    }
}