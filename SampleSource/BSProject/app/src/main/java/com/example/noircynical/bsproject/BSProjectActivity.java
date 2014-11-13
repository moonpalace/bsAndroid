package com.example.noircynical.bsproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class BSProjectActivity extends Activity {

    private Context mContext;

    private ListView listview;
    private TextView urlText;
    private Button btn;
    private Adapter adapter;

    private ArrayList<String> list;
    private ArrayList<String> url;

    private void Init() {
        mContext= getApplicationContext();

        listview = (ListView) findViewById(R.id.listview);
        urlText = (TextView) findViewById(R.id.text);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn:
                    if (urlText.getText().length() < 1 || urlText.getText().toString().equalsIgnoreCase("url here"))
                        Toast.makeText(getApplicationContext(), "please select a item in list", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent= new Intent(mContext, WebActivity.class);
                        intent.putExtra("url", urlText.getText().toString());
                        startActivity(intent);
                    }
                    break;
            }
            }
        });

        adapter= new Adapter(this.getApplicationContext(), R.layout.list_row, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                urlText.setText(url.get(i));
            }
        });
    }

    private void createData() {
        list= new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.list_str)));
        url= new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.url_list)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bsproject);
    }

    protected void onResume(){
        super.onResume();
        onNewIntent(this.getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(intent != null && (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND) || intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND_MULTIPLE))){
            list= intent.getStringArrayListExtra("name");
            url= intent.getStringArrayListExtra("url");
            Init();
        }
        else{
            intent = getPackageManager().getLaunchIntentForPackage("com.example.noircynical.another");
            intent.putExtra("require_db", true);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop(){ super.onStop(); }
}
