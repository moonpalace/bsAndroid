package com.example.noircynical.bsproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by noirCynical on 14. 11. 13..
 */
public class Adapter extends BaseAdapter{

    private Context mContext;
    private ArrayList list;
    private int resource;

    public Adapter(Context context, int resId){
        mContext= context;
        resource= resId;
    }

    public Adapter(Context context, int resId, ArrayList array){
        mContext= context;
        list= array;
        resource= resId;
    }

    public View getView(final int position, View convertView, ViewGroup viewgroup){
        View row= convertView;

        Log.d("BSProject", "index: "+position);

        if(row == null){
//            LayoutInflater inflater= ((BSProjectActivity)mContext).getLayoutInflater();
            LayoutInflater inflater= LayoutInflater.from(mContext);
            row= inflater.inflate(R.layout.list_row, viewgroup, false);
        }

        TextView tv= (TextView)row.findViewById(R.id.list_text);
        if(tv != null)
            tv.setText((String)list.get(position));

        return row;
    }

    public long getItemId(int pos){ return pos; }
    public int getCount(){ return list.size(); }
    public Object getItem(int pos){ return pos; }
}
