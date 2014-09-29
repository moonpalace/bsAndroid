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

import java.util.HashMap;

public class BSProjectActivity extends Activity {

    final private String _db= "samples";
    private String[] list;
    private ArrayAdapter<String> adapter;

    private BS bs;
    private BS.AdapterCursor _adapter;

    private void init(){
        list= getResources().getStringArray(R.array.list_str);
        bs.Sqlite(_db).exec(bs.Rstring(R.string.str_list_query_create));
        for(int i=0; i<list.length; i++){
            String query= "insert into samples(name)values('"+list[i]+"')";
            System.out.println(query);
            bs.Sqlite(_db).exec(query);
        }
        Cursor c= bs.Sqlite(_db).select(bs.Rstring(R.string.str_list_query));
        if(_adapter == null){
            Log.d("BSProject", "adapter create");
            _adapter= new BS.AdapterCursor(c, 0, null, null){
                public View view(Cursor c, int index, int rowid, HashMap<String, Object> $data){
                    return bs.Rlayout(R.layout.list_row);
                }

                public void data(Cursor c, int index, View v, ViewGroup g, int rowid, HashMap<String, Object> $data){
                    Log.d("BSProject", "index: "+Integer.toString(index));
                    bs.View(R.id.list_text, v, false).S(BS.TV_text, c.getString(0));
                }
            };
            bs.View(R.id.listview).A(BS.V_adapter, _adapter);
        } else _adapter.update(c, 0);
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
