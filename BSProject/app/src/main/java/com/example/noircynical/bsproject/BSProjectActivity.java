package com.example.noircynical.bsproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class BSProjectActivity extends Activity {

    final private String _db= "samples_list";

    private BS bs;
    private BS.AdapterCursor _adapter;

//    private void init(){
//        Cursor c= bs.Sqlite(_db).select(bs.Rstring(R.string.str_list_query));
//        if(_adapter == null){
//            _adapter= new BS.AdapterCursor(c){
//                public View view(Cursor c, int index){
//                    View v= bs.Rlayout(R.layout.list_row);
//                    String[] str= {c.getString(0), c.getString(1)};
//                    bs.View(R.id.list_text, v).S();
//                    return v;
//                }
//
//                public void data(Cursor c, int index, View v, ViewGroup g){
//
//                }
//            };
//        } else _adapter.update(c);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bs = new BS( this, savedInstanceState );
        bs.View(R.layout.activity_bsproject).S(BS.V_parent, BS.V_ROOT);

//        init();

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
