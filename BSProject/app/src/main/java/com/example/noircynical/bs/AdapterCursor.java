package com.example.noircynical.bs;

import android.view.View;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AdapterCursor extends BaseAdapter {

    abstract public View view(P $data);
    abstract public void data(Cursor $c, int $idx, View $v, ViewGroup $g, int $rowid, P $data);

    private Cursor _c;
    public P p = P.pool();
    private int[] _rowid;

    public AdapterCursor() {
    }

    public AdapterCursor(BSCursor $c, int $rowid, Object... arg) {
        init($c, $rowid);
        int i = 0, j = arg.length;
        while (i < j) p.put(arg[i++], arg[i++]);
    }

    public AdapterCursor(BSCursor $c, int $rowid, P arg) {
        init($c, $rowid);
        p.putAll(arg);
    }

    private void init(BSCursor $c, int $rowid) {
        if ($c != null) {
            _c = new BSCursor($c);
            if ($rowid > -1) {
                int i = 0, j = _c.getCount();
                _rowid = new int[j];
                while (i < j) {
                    _c.moveToPosition(i);
                    _rowid[i++] = _c.getInt($rowid);
                }
            }
        } else _c = null;
    }

    public int getCount() {
        return _c == null ? 0 : _c.getCount();
    }

    public Object getItem(int $i) {
        return null;
    }

    public long getItemId(int $i) {
        return $i;
    }

    public View getView(int $idx, View $v, ViewGroup $g) {
        _c.moveToPosition($idx);
        if ($v == null) $v = view(p);
        data(_c, $idx, $v, $g, _rowid[$idx], p);
        return $v;
    }

    public boolean isEnabled(int $idx) {
        return true;
    }

    public void update(BSCursor $c, int $rowid) {
        init($c, $rowid);
        notifyDataSetChanged();
    }

    public int rowid(int $idx) {
        return _rowid[$idx];
    }

    public Object data(String $key) {
        return p.get($key);
    }

    public void data(String $key, Object $val) {
        p.put($key, $val);
    }
}