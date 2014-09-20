package com.example.noircynical.bsproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BSProjectActivity extends Activity {

    final private String _db= "samples";
    private String[] list;
    private ArrayAdapter<String> adapter;

    private BS bs;
    private BS.AdapterCursor _adapter;

    private void init(){
        list= getResources().getStringArray(R.array.list_str);
        adapter= new ArrayAdapter<String>(this, R.layout.list_row, R.id.list_text, list);
        ((ListView)findViewById(R.id.listview)).setAdapter(adapter);
//        bs.Sqlite(_db).exec(bs.Rstring(R.string.str_list_query_create));
//        Cursor c= bs.Sqlite(_db).select(bs.Rstring(R.string.str_list_query));
//
//        if(_adapter == null){
//            _adapter= new BS.AdapterCursor(c){
//                public View view(Cursor c, int index){
//                    return bs.Rlayout(R.layout.list_row);
//                }
//
//                public void data(Cursor c, int index, View v, ViewGroup g){
//                    ((TextView)v.findViewById(R.id.list_text)).setText(list[index]);
//                }
//            };
////            bs.View(R.id.listview).A(BS.V_adapter, _adapter);
//            ((ListView)findViewById(R.id.listview)).setAdapter(_adapter);
//        } else _adapter.update(c);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bs = new BS( this, savedInstanceState );
        bs.View(R.layout.activity_bsproject).S(BS.V_parent, BS.V_ROOT);
        init();
    }

    @Override
    protected void onPostCreate( Bundle $savedInstanceState ){
        super.onPostCreate($savedInstanceState);
        bs.onPostCreate($savedInstanceState);
    }
    @Override
    protected void onActivityResult( int $request, int $result, Intent $data ){
        bs.onActivityResult( $request, $result, $data );
    }
    @Override
    protected void onResume(){
        super.onResume();
        bs.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        bs.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
        bs.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bs.onDestroy();
    }
}
