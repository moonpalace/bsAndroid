package com.example.noircynical.bs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class BSSQLite extends SQLiteOpenHelper {

    static private HashMap<String, BSSQLite> _pool = new HashMap<String, BSSQLite>();

    static public BSSQLite pool(Context $context, String $db, Object... $arg) {
        if (!_pool.containsKey($db))
            _pool.put($db, new BSSQLite($context, $db, 1, $arg.length == 1 && $arg[0] instanceof Object[] ? (Object[])$arg[0] : $arg));
        return _pool.get($db);
    }

    static public BSSQLite pool(Context $context, String $db) {
        if (!_pool.containsKey($db))
            _pool.put($db, new BSSQLite($context, $db, 1));
        return _pool.get($db);
    }

    static public void pool(BSSQLite $v) {
        $v.close();
        P.pool($v.p);
    }

    private String d;
    private SQLiteDatabase w;
    private SQLiteDatabase r;
    public P p;

    private BSSQLite(Context $context, String $database, int $ver, Object[] $arg) {
        super($context, $database, null, $ver);
        d = $database;
        w = this.getWritableDatabase();
        r = this.getReadableDatabase();
        p = P.pool();
        int i = 0, j = $arg.length;
        while (i < j) p.put($arg[i++], $arg[i++]);
        p.put(BS.CONTEXT, $context);
    }

    private BSSQLite(Context $context, String $db, int $ver){
        super($context, $db, null, $ver);
        d= $db;
        w= this.getWritableDatabase();
        r= this.getReadableDatabase();
        p= P.pool();
        p.put(BS.CONTEXT, $context);
    }

    public String database() {
        return d;
    }

    public void onCreate(SQLiteDatabase db) {}

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public BSSQLite table(String $name, String... arg) {
        Cursor c = w.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + $name + "'", null);
        int i, j;
        if (c != null) {
            i = c.getCount();
            c.close();
            if (i > 0) return this;
        }
        StringBuilder sb = new StringBuilder(150).append("CREATE TABLE ").append($name).append(" (");
        i = 0;
        j = arg.length;
        while (i < j) {
            sb.append(arg[i++]).append(" ").append(arg[i++]);
            if (i != j) sb.append(",");
        }
        sb.append(")");
        exec(sb.toString());
        return this;
    }

    public int exec(String $query){
        w.execSQL($query);
        Cursor c = r.rawQuery("SELECT changes()", null);
        return c != null && c.getCount() > 0 && c.moveToFirst() ? c.getInt(0) : 0;
    }

    public int exec(String $query, String... arg) {
        w.execSQL(arg.length == 0 ? $query : BS.log(BS.tmpl($query, arg)));
        Cursor c = r.rawQuery("SELECT changes()", null);
        return c != null && c.getCount() > 0 && c.moveToFirst() ? c.getInt(0) : 0;
    }

    public int exec(String $query, HashMap<String, String> arg) {
        w.execSQL(arg == null ? $query : BS.tmpl($query, arg));
        Cursor c = r.rawQuery("SELECT changes()", null);
        return c != null && c.getCount() > 0 && c.moveToFirst() ? c.getInt(0) : 0;
    }

    public Cursor select(String $query, String... arg) {
        Cursor c = r.rawQuery(arg.length == 0 ? $query : BS.tmpl($query, arg), null);
        return c != null && c.getCount() > 0 && c.moveToFirst() ? c : null;
    }

    public Cursor select(String $query, HashMap<String, String> arg) {
        Cursor c = r.rawQuery(arg == null ? $query : BS.tmpl($query, arg), null);
        return c != null && c.getCount() > 0 && c.moveToFirst() ? c : null;
    }

    public int lastId() {
        Cursor c = r.rawQuery("select last_insert_rowid()", null);
        return c != null && c.getCount() > 0 && c.moveToFirst() ? c.getInt(0) : -1;
    }
}
