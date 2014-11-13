package com.example.noircynical.bsproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import com.example.noircynical.bs.*;

public class BSProjectActivity extends Activity {

    final private String _db= "bssamples";
    private String[] list;
//    private ArrayAdapter<String> adapter;
    private ListView listview;
    private TextView text;
    private Button btn;

    private BS_ol bs;
    private AdapterCursor _adapter;
    private BSSQLite bsSQlite;
    private BSCursor bsCursor;

    View.OnClickListener listener= new View.OnClickListener(){
        public void onClick(View v){
            switch(v.getId()){
                case R.id.btn:
                    Toast.makeText(BSProjectActivity.this, "btn clicked", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void init(){
        Log.w("BSProject", "init function call");
        list= getResources().getStringArray(R.array.list_str);
        Log.w("BSProject", "list get data");

        bsSQlite= BSSQLite.pool(this, _db);
        bsSQlite.exec(BS.Rstring(this, R.string.str_list_query_create));

        for(int i=0; i<list.length; i++){
            String query= "insert into bssamples(name)values('"+list[i]+"')";
            System.out.println(query);
            bsSQlite.exec(query);
        }

        Cursor c= bsSQlite.select(BS.Rstring(this, R.string.str_list_query));
        bsCursor= new BSCursor(c);
        if(_adapter == null){
            _adapter= new AdapterCursor(null, 0, list) {
                @Override
                public View view(P $data) {
                    Log.d("BSProject", "view called in adapter");
                    return BS.Rlayout(BSProjectActivity.this, R.layout.list_row);
                }

                @Override
                public void data(Cursor $c, int $idx, View $v, ViewGroup $g, int $rowid, P $data) {
                    Log.d("BSProject", "index: "+Integer.toString($idx));

                    View v= $v;
                    if(v == null){
                        LayoutInflater inflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v= inflater.inflate(R.layout.list_row, null);
                    }

                    if(list[$idx] != null){
                        TextView tv= (TextView)v.findViewById(R.id.list_text);
                        tv.setText(list[$idx]);
                    }
//                    BS.Rlayout(BSProjectActivity.this, R.id.list_text);
                }
            };


        } else _adapter.update(bsCursor, 0);
//        bs.Sqlite(_db).exec(bs.Rstring(R.string.str_list_query_create));
//
//        Cursor c= bs.Sqlite(_db).select(bs.Rstring(R.string.str_list_query));
//        if(_adapter == null){
//            _adapter= new BS.AdapterCursor(c, 0, list){
//                public View view(BS.bsCursor c, int index, int rowid, HashMap<String, Object> $data){
//                    return bs.Rlayout(R.layout.list_row);
//                }
//
//                public void data(BS.bsCursor c, int index, View v, ViewGroup g, int rowid, HashMap<String, Object> $data){
//                    bs.View(R.id.list_text, v, false).S(BS.TV_text, c.getString(0));
//                }
//            };
//            bs.View(R.id.listview).A(BS.V_adapter, _adapter);
//        } else _adapter.update(c, 0);
    }

    private void setView(){
        listview= (ListView)findViewById(R.id.listview);
        text= (TextView)findViewById(R.id.text);
        btn= (Button)findViewById(R.id.btn);
        btn.setOnClickListener(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BS.pool(this, savedInstanceState);
        setContentView(R.layout.activity_bsproject);
        onNewIntent(this.getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        setView();
        init();

        if(intent == null){

        }
        else{

        }
    }

    @Override
    protected void onPostCreate( Bundle $savedInstanceState ){
        super.onPostCreate($savedInstanceState);
//        bs.onPostCreate($savedInstanceState);
    }
    @Override
    protected void onActivityResult( int $request, int $result, Intent $data ){
//        bs.onActivityResult( $request, $result, $data );
    }
    @Override
    protected void onResume(){
        super.onResume();
//        bs.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
//        bs.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
//        bs.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        bs.onDestroy();
    }
}
