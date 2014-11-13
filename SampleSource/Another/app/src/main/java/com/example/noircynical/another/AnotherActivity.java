package com.example.noircynical.another;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AnotherActivity extends Activity {

    private String dbname= "samples";
    private SQLiteDatabase sqlite;

    private ArrayList<String> name_list= new ArrayList<String>();
    private ArrayList<String> url_list = new ArrayList<String>();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_another);
    }

    @Override
    protected void onResume(){
        super.onResume();
        intent= getIntent();
        onNewIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(this.intent);

        if(this.intent == null){
        } else if(this.intent.getExtras() != null && this.intent.getExtras().getBoolean("require_db")){
            sqlite= openOrCreateDatabase(dbname, Context.MODE_PRIVATE, null);
            sqlite.execSQL(getResources().getString(R.string.str_list_query_create));

            String name_sql = getResources().getString(R.string.str_list_query_select_name);
            String url_sql = getResources().getString(R.string.str_list_query_select_url);

            setdata();

            Cursor cursor = sqlite.rawQuery(name_sql, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                name_list.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor= sqlite.rawQuery(url_sql, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                url_list.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();

            sqlite.close();

            Intent send = getPackageManager().getLaunchIntentForPackage("com.example.noircynical.bsproject");
            send.setAction(Intent.ACTION_SEND);
            send.putStringArrayListExtra("name", name_list);
            send.putStringArrayListExtra("url", url_list);
            startActivity(Intent.createChooser(send, "send_data"));
        }
    }

    private void setdata(){
        String[] list= getResources().getStringArray(R.array.list_str);
        String[] url= getResources().getStringArray(R.array.url_list);

        for(int i=0; i<list.length; i++) {
            ContentValues value= new ContentValues();
            value.put("name", list[i]);
            value.put("url", url[i]);
            sqlite.insert("samples", null, value);
//            sqlite.execSQL("insert into samples values(" + i + ", \'" + list[i] + "\', \'" + url[i] + "\');");
        }
    }

    @Override
    protected void onStop(){ super.onStop(); finish(); }
}
