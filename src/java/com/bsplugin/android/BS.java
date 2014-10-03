/* bsAndroid v0.1
 * Copyright (c) 2013 by ProjectBS Committe and contributors.
 * http://www.bsplugin.com All rights reserved.
 * Licensed under the BSD license. See http://opensource.org/licenses/BSD-3-Clause
 */
package com.bsplugin.android;

import android.animation.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.media.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;
import java.lang.reflect.Field;

public class BS{

//region Const

static public final int NONE = 0;
static public final int SRC = 1;
static public final int START = 2;
static public final int COMPLETE = 3;
static public final int CURRENT = 4;
static public final int DURATION = 5;

static public final int RUN_invisible = 100;
static public final int RUN_visible = 101;
static public final int RUN_visibleToggle = 102;

//endregion

//region worker&callback
static private ExecutorService _workers = Executors.newFixedThreadPool(5);
{
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
}
static public void worker( Runnable $run ){
	_workers.execute( $run );
}
static public interface Callback{public void run( Object $data );}
static public Callback CallbackNONE = new Callback(){public void run( Object $data ){}};
//endregion

//region http

static public String get( Callback $end, String $uri, String...arg ){
	ArrayList<String> t0 = httpParam( arg );
	return httpRun( $end, "GET", !t0.get( 0 ).equals( "" ) ? $uri + "?" + t0.get( 0 ).equals( "" ) : $uri, t0 );
}
static public String post( Callback $end, String $uri, String...arg ){
	return httpRun( $end, "POST", $uri, httpParam( arg ) );
}
static private ArrayList<String> httpParam( String[]arg ){
	int i = 0, j = arg.length;
	String t0 = "";
	ArrayList<String> t1 = new ArrayList<String>();
	t1.add("");
	try{
		while( i < j ){
			String k = arg[i++];
			String v = arg[i++];
			if( k.charAt( 0 ) == '@' ){
				t1.add( k.substring( 1 ) );
				t1.add( v );
			}else{
				t0 += "&" + URLEncoder.encode( k, "UTF-8" ) + "=" + URLEncoder.encode( v, "UTF-8" );
			}
		}
		t1.set( 0, t0.substring( 1 ) );
	}catch( Exception e ){}
	return t1;
}
static private String httpRun( final Callback $end, final String $method, final String $uri, final ArrayList<String> $data ){
	final String[] result = {null};
	Runnable run = new Runnable(){
		@Override
		public void run(){
		try{
			HttpURLConnection conn = null;
			conn = ( HttpURLConnection ) new URL($uri).openConnection();
			conn.setRequestMethod($method);
			conn.setConnectTimeout( 10000 );
			conn.setReadTimeout( 10000 );
			conn.setRequestProperty( "Connection", "Keep-Alive" );
			conn.setUseCaches( false );
			conn.setRequestProperty( "Accept-Encoding", "gzip" );
			int i = 1, j = $data.size();
			while( i < j ) conn.setRequestProperty( $data.get( i++ ), $data.get( i++ ) );
			if( !$method.equals( "GET" ) ){
				conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
				String body = $data.get( 0 );
				if( body.length() > 0 ){
					conn.setDoInput( true );
					conn.setDoOutput( true );
					OutputStreamWriter out = null;
					out = new OutputStreamWriter( conn.getOutputStream() );
					out.write(body);
					out.flush();
					out.close();
				}
			}
			conn.connect();
			String r = "";
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK ){
				InputStream is = conn.getInputStream();
				String gzip = conn.getHeaderField( "gzip" );
				if( gzip != null && gzip.equalsIgnoreCase("Accept-Encoding") ) is = new GZIPInputStream(is);
				r = str(is);
			}
			if( $end == null ) result[0] = r;
			else $end.run(r);
		}catch( Exception e ){
			log( "http Error:" + $uri + ":" + e.toString() );
		}
		}
	};
	if( $end == null ){
		run.run();
		return result[0];
	}else{
		worker( run );
		return null;
	}
}

//endregion

//region CoreUtil

static public class P extends HashMap<Object, Object>{
	public String getString( Object $key ){return (String)get($key);}
	public int getInt( Object $key ){return (Integer)get($key);}
	public float getFloat( Object $key ){return (Float)get($key);}
	public boolean getBoolean( Object $key ){return (Boolean)get($key);}
	public long getLong( Object $key ){return (Long)get($key);}
	public Runnable getRunnable( Object $key ){return (Runnable)get($key);}

}
static private boolean _isDebug = false;
static public void debugMode( boolean $is ){_isDebug = $is;}


static private ArrayList<P> _pPool = new ArrayList<P>();
static private P hash(){
	int i = _pPool.size();
	if( i > 0 ){
		P t0 = _pPool.get( i - 1 );
		_pPool.remove( i - 1 );
		return t0;
	}else return new P();
}
static private void hashPool( P $v ){
	$v.clear();
	_pPool.add($v);
}
static public String log( String $msg ){if( _isDebug ) Log.i( "BS", $msg );return $msg;}

//endregion

//region String casting

final static private String _strSharp = "#";
static public String str( final InputStream $v ){
    StringBuilder sb = new StringBuilder( 200 );
    try{
        byte[] b = new byte[4096];
        for( int n; ( n = $v.read(b) ) != -1 ; ) sb.append( new String( b, 0, n ) );
        $v.close();
    }catch( Exception $e ){
        throw new Error( log( "BsStr.str:" + $e.toString() ) );
    }
    return trim(sb.toString());
}
static public String str( final int $v ){return Integer.toString($v);}
static public String str( final int[] $v ){
    String t0 = Arrays.toString($v);
    return t0.substring( 1, t0.length() - 1 );
}
static public String str( final float[] $v ){
    String t0 = Arrays.toString($v);
    return t0.substring( 1, t0.length() - 1 );
}
static public String str( final String[] $v ){
    String t0 = Arrays.toString($v);
    return t0.substring( 1, t0.length() - 1 );
}
static public String str( final boolean[] $v ){
    String t0 = Arrays.toString($v);
    return t0.substring( 1, t0.length() - 1 );
}
static public String str( final float $v ){return Float.toString($v);}
static public String str( final boolean $v ){return $v ? "true" : "false";}
static public int str2int( final String $v ){return Integer.parseInt( $v, 10 );}
static public float str2float( final String $v ){return Float.parseFloat($v);}
static public boolean str2bool( final String $v ){return $v.equalsIgnoreCase("true") || $v.equals("1") ? Boolean.TRUE : Boolean.FALSE;}
static public int str2color( final String $v ){return Color.parseColor($v.charAt(0) == '#' ? $v : _strSharp + $v);}

//endregion

//region StringUtil

static public String right( String $v, int $right ){return $v.substring( $v.length() - $right );}
static public String trim( String $v ){return $v.trim();}
static public String[] trim( String[] $v ){
    for( int i = 0, j = $v.length ; i < j ; i++ ) $v[i] = $v[i].trim();
    return $v;
}
static public LinkedList<String> trim( LinkedList<String> $v ){
    for( int i = 0, j = $v.size() ; i < j ; i++ ) $v.set( i, $v.get(i).trim() );
    return $v;
}
static public String replace( final String $v, final String $from, final String $to ){
    int i = $v.lastIndexOf($from);
    if( i < 0 ) return $v;
    {
        StringBuilder sb = new StringBuilder( $v.length() + ( $to.length() - $from.length() ) * 10 ).append($v);
        int j = $from.length();
        while( i > -1 ){
            sb.replace( i, ( i + j ), $to );
            i = $v.lastIndexOf( $from, i - 1 );
        }
        return sb.toString();
    }
}
@SuppressWarnings( "rawtypes" )
static public String join( final LinkedList $v ){return join( $v, "," );}
@SuppressWarnings( "rawtypes" )
static public String join( final LinkedList $v, final String $sep ){
    StringBuilder sb = new StringBuilder( $v.get(0).toString() );
    for( int i = 1, j = $v.size() ; i < j ; i++ ) sb.append($sep).append($v.get(i).toString());
    return sb.toString();
}
static public String tmpl( final String $v, String[] $data ){
    StringBuilder sb = new StringBuilder( $v.length() + 100 ).append($v);
    for( int i = 0, j = $data.length ; i < j ; i += 2 ){
        String k = "@" + $data[i] + "@";
        do{
            int l = sb.indexOf(k);
            if( l == -1 ) break;
            sb.replace( l, l + k.length(), $data[i + 1] );
        }while(true);
    }
    return sb.toString();
}
static public String tmpl( final String $v, final HashMap<String,String> $data ){
    StringBuilder sb = new StringBuilder( $v.length() + 100 ).append($v);
    for( Map.Entry<String, String> map : $data.entrySet() ){
        String k = "@" + map.getKey() + "@";
        do{
            int l = sb.indexOf(k);
            if( l == -1 ) break;
            sb.replace( l, l + k.length(), map.getValue() );
        }while(true);
    }
    return sb.toString();
}
static private LinkedList<String> split( String $v, String $sep, boolean $isTrim ){
    int i = 0, j = $v.indexOf( $sep );
    if( j > -1 ){
        LinkedList<String> result = new LinkedList<String>();
        for( ; j > -1 ; j = $v.indexOf( $sep, i ) ) result.add( $isTrim ? $v.substring( i++, j ).trim() : $v.substring( i++, j ) );
        if( i < $v.length() - 1 ) result.add( $isTrim ? $v.substring( i ).trim() : $v.substring( i ) );
        return result;
    }
    return null;
}
static public String strIsNum( final String $str ){
    int k = 0;
    for( int i = 0, j = $str.length() ; i < j ; i++ ){
        switch( $str.charAt(i) ){
            case'-': if( i > 0 ) return "string"; break;
            case'.': if( k > 0 ) return "string"; else k++; break;
            case'0':case'1':case'2':case'3':case'4':case'5':case'6':case'7':case'8':case'9': break;
            default: return "string";
        }
    }
    return k == 0 ? "int" : "float";
}
static public boolean strIsUrl( final String $v ){return $v.substring( 0, 4 ).equals("http");}

//endregion

//region SQLite

static private HashMap<String, bsSqlite> sqlitesST = new HashMap<String, bsSqlite>();
static public bsSqlite Sqlite( Context $context, String $db ){
	if( !sqlitesST.containsKey($db) ) sqlitesST.put( $db, new bsSqlite( $context, $db, 1 ) );
	return sqlitesST.get($db);
}
private HashMap<String, bsSqlite> sqlites = new HashMap<String, bsSqlite>();
public bsSqlite Sqlite( String $db ){
    if( !sqlites.containsKey($db) ) sqlites.put( $db, new bsSqlite( _act, $db, 1 ) );
    return sqlites.get($db);
}
static public class bsSqlite extends SQLiteOpenHelper{
	private String d;
	private SQLiteDatabase w;
	private SQLiteDatabase r;
	public bsSqlite( Context $context, String $database, int $ver ){
		super( $context, $database, null, $ver );
		d = $database;
		w = this.getWritableDatabase();
		r = this.getReadableDatabase();
	}
	@Override
	public void onCreate( SQLiteDatabase db ){}
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ){}
	public bsSqlite table( String $name, String...arg ){
		Cursor c = w.rawQuery( "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + $name + "'", null );
		if( c != null && c.getCount() > 0){
			c.close();
			return this;
		}
		c.close();
		StringBuilder sb = new StringBuilder(150).append("CREATE TABLE ").append($name).append(" (");
		int i = 0, j = arg.length;
		while( i < j ){
			sb.append(arg[i++]).append(" ").append(arg[i++]);
			if( i != j ) sb.append(",");
		}
		sb.append(")");
		exec(sb.toString());
		return this;
	}
	public int exec( String $query, String...arg ){
		w.execSQL( arg.length == 0 ? $query : BS.log(tmpl( $query, arg )) );
		Cursor c = r.rawQuery( "SELECT changes()", null );
		return c != null && c.getCount() > 0 && c.moveToFirst() ? c.getInt(0) : 0;
	}
	public int exec( String $query, HashMap<String, String> arg ){
		w.execSQL( arg == null ? $query : tmpl( $query, arg ) );
		Cursor c = r.rawQuery( "SELECT changes()", null );
		return c != null && c.getCount() > 0 && c.moveToFirst() ? c.getInt(0) : 0;
	}
	public Cursor select( String $query, String...arg ){
		Cursor c = r.rawQuery( arg.length == 0 ? $query : tmpl( $query, arg ), null );
		return c != null && c.getCount() > 0 && c.moveToFirst() ? c : null;
	}
	public Cursor select( String $query, HashMap<String, String> arg ){
		Cursor c = r.rawQuery( arg == null ? $query : tmpl( $query, arg ), null );
		return c != null && c.getCount() > 0 && c.moveToFirst() ? c : null;
	}
	public int lastId(){
		Cursor c = r.rawQuery( "select last_insert_rowid()", null );
		return c != null && c.getCount() > 0 && c.moveToFirst() ? c.getInt(0) : -1;
	}
}
static public class bsCursor implements Cursor{

	private ArrayList<ArrayList<Object>> _rs = new ArrayList<ArrayList<Object>>();
	private ArrayList<Object> _row;
	private ArrayList<String> _fields = new ArrayList<String>();
	private String[] _fieldsArray;
	private ArrayList<Integer> _fieldTypes = new ArrayList<Integer>();
	private int _length, _columeCount, _cursor;

	private bsCursor( Cursor c ){
		if( c != null ){
			_length = c.getCount();
			if( _length > 0 && c.moveToFirst() ){
				int i, j = _columeCount = c.getColumnCount();
				for( i = 0; i < j ; i++ ){
					_fields.add( c.getColumnName( i ) );
					_fieldTypes.add( c.getType( i ) );
				}
				_fieldsArray = (String[])_fields.toArray(new String[_fields.size()]);
				do{
					ArrayList<Object> _row = new ArrayList<Object>();
					for( i = 0; i < j ; i++ ){
						switch(_fieldTypes.get(i)){
						case Cursor.FIELD_TYPE_NULL:_row.add(null); break;
						case Cursor.FIELD_TYPE_INTEGER:_row.add(c.getInt(i)); break;
						case Cursor.FIELD_TYPE_FLOAT:_row.add(c.getFloat( i )); break;
						case Cursor.FIELD_TYPE_STRING:_row.add(c.getString( i )); break;
						case Cursor.FIELD_TYPE_BLOB:_row.add(c.getBlob( i )); break;
						}
					}
					_rs.add( _row );
				}while( c.moveToNext() );
				_cursor = 0;
				_row = _rs.get( 0 );
			}
		}
	}
	public byte[] getBlob(int columnIndex){return (byte[])_row.get(columnIndex);}
	public double getDouble(int columnIndex){return (Double)_row.get(columnIndex);}
	public float getFloat(int columnIndex){return (Float)_row.get(columnIndex);}
	public int getInt(int columnIndex){return (Integer)_row.get(columnIndex);}
	public long getLong(int columnIndex){return (Long)_row.get(columnIndex);}
	public short getShort(int columnIndex){return (Short)_row.get(columnIndex);}
	public String getString(int columnIndex){return (String)_row.get(columnIndex);}

	public int getColumnCount(){return _columeCount;}
	public int getColumnIndex(String columnName){return _fields.indexOf(columnName);}
	public String getColumnName(int columnIndex){return _fieldsArray[columnIndex];}
	public String[] getColumnNames(){return _fieldsArray;}
	public int getCount(){return _length;}
	public int getPosition(){return _cursor;}
	public int getType(int columnIndex){return _fieldTypes.get( columnIndex );}

	public boolean isAfterLast(){return _cursor == _length - 2;}
	public boolean isBeforeFirst(){return _cursor == 1;}

	public boolean isFirst(){return _cursor == 0;}
	public boolean isLast(){return _cursor == _length - 1;}
	public boolean isNull(int columnIndex){return _row.get( columnIndex ) == null;}
	public boolean move(int offset){
		int i = _cursor + offset;
		if( i < _length ){
			_cursor = i;
			_row = _rs.get(_cursor);
			return true;
		}else return false;
	}
	public boolean moveToFirst(){
		_cursor = 0;
		_row = _rs.get(_cursor);
		return true;
	}
	public boolean moveToLast(){
		_cursor = _length - 1;
		_row = _rs.get(_cursor);
		return true;
	}
	public boolean moveToNext(){
		if( _cursor < _length - 1 ){
			_cursor++;
			_row = _rs.get(_cursor);
			return true;
		}else return false;
	}
	public boolean moveToPosition(int position){
		if( position > -1 && position < _length ){
			_cursor = position;
			_row = _rs.get(_cursor);
			return true;
		}else return false;
	}
	public boolean moveToPrevious(){
		if( _cursor > 0 ){
			_cursor--;
			_row = _rs.get(_cursor);
			return true;
		}else return false;
	}

	public boolean isClosed(){return true;}
	public int getColumnIndexOrThrow( String s ) throws IllegalArgumentException{return 0;}
	public boolean getWantsAllOnMoveCalls(){return false;}
	public void close(){}
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer){}
	public void deactivate(){}
	public Uri getNotificationUri(){return null;}
	public Bundle getExtras(){return null;}
	public void registerContentObserver(ContentObserver observer){}
	public void registerDataSetObserver(DataSetObserver observer){}
	public boolean requery(){return false;}
	public Bundle respond(Bundle extras){return null;}
	public void setNotificationUri(ContentResolver cr, Uri uri){}
	public void unregisterContentObserver(ContentObserver observer){}
	public void unregisterDataSetObserver(DataSetObserver observer){}
}

//endregion

//region class

private interface SG{
	Object g( Object $k );
	Object s( Object $k, Object $v );
	Object S( Object...arg );
}
static private P _init( Object[] arg ){
	P p = hash();
	int i = 0, j = arg.length;
	while( i < j ) p.put( arg[i++], arg[i++] );
	return p;
}
static private Object _s( SG $self, P $p, HashMap $fn, Object[] $arg ){
	Object result = null, k, v;
	int j = $arg.length;
	if( j == 1 && $arg[0] instanceof Object[] ){
		$arg = (Object[])$arg[0];
		j = $arg.length;
	}
	if( j == 1 ){
		k = $arg[0];
		if( $p.containsKey(k) ) result = $p.get(k);
		else if( $fn.containsKey(k) ) result = $self.g(k);
	}else{
		int i = 0;
		while( i < j ){
			k = $arg[i++];
			v = $arg[i++];
			if( $fn.containsKey(k) ) result = $self.s( k, v );
			else{
				$p.put( k, v );
				result = v;
			}
		}
	}
	return result;
}
static private int asyncArgsID = 0;
static private HashMap<Integer, Object[]> asyncArgs = new HashMap<Integer, Object[]>();
static private Object _a( Handler $h, SG $self, P $p, HashMap $fn, Object[] $arg  ){
	Object result = null, k, v;
	int j = $arg.length;
	if( j == 1 && $arg[0] instanceof Object[] ){
		$arg = (Object[])$arg[0];
		j = $arg.length;
	}
	if( j == 1 ){
		k = $arg[0];
		if( $p.containsKey(k) ) result = $p.get(k);
		else if( $fn.containsKey(k) ) result = $self.g(k);
	}else{
		asyncArgs.put( asyncArgsID, $arg );
		Message r = Message.obtain($h);
		r.arg1 = asyncArgsID++;
		r.obj = $self;
		$h.sendMessage(r);
	}
	return result;
}

//endregion

//region mediaplayer

{
	_player.put( E_end, new _Player(){
		public Object g( final bsPlayer $m ){
			return $m.p.get( E_end );
		}

		public Object s( final bsPlayer $m, Object $v ){
			if( !$m.p.containsKey( COMPLETE ) )
				$m.p.put( COMPLETE, new MediaPlayer.OnCompletionListener(){
					public void onCompletion( MediaPlayer mediaPlayer ){
						$m.p.getRunnable( E_end ).run();
						log( "mplayerEnded" );
					}
				} );
			$m.p.put( E_end, $v );
			$m.setOnCompletionListener( (MediaPlayer.OnCompletionListener) $m.p.get( COMPLETE ) );
			return null;
		}
	} );
	_player.put( START, new _Player(){
		public Object g( bsPlayer $m ){
			return s( $m, null );
		}
		public Object s( bsPlayer $m, Object $v ){
			try{
				$m.prepare();
			}catch( Exception e ){
				log( "Mplayer.START:" + e.toString() );
			}
			if( $v != null ) $m.seekTo( (Integer) $v );
			$m.start();
			return null;
		}
	} );
	_player.put( SRC, new _Player(){
		public Object g( bsPlayer $m ){
			return $m.p.get( SRC );
		}

		public Object s( bsPlayer $m, Object $v ){
			$m.p.put( SRC, $v );
			try{
				$m.setDataSource( (String) $v );
			}catch( Exception e ){
				log( "Mplayer.SRC:" + e.toString() );
			}
			return null;
		}
	} );
	_player.put( CURRENT, new _Player(){
		public Object g( bsPlayer $m ){
			return $m.getCurrentPosition();
		}
		public Object s( bsPlayer $m, Object $v ){
			$m.seekTo((Integer)$v);
			return $v;
		}
	} );
	_player.put( DURATION, new _Player(){
		public Object g( bsPlayer $m ){
			return $m.getDuration();
		}
		public Object s( bsPlayer $m, Object $v ){return $m.getDuration();}
	} );
}
private abstract class _Player{
	public Object g( final bsPlayer $m ){return null;}
	public Object s( final bsPlayer $m, Object $v ){return $v;}
}
static private HashMap<Object, _Player> _player = new HashMap<Object, _Player>();

static public bsPlayer Player( String $key, Object...arg ){
	if( !player.containsKey($key) ) player.put( $key, new bsPlayer(arg) );
	return player.get($key);
}
static private HashMap<String, bsPlayer> player = new HashMap<String, bsPlayer>();
static public class bsPlayer extends MediaPlayer implements SG{
	public P p;
	private bsPlayer( Object... arg ){
		super();
		p = _init(arg);
	}
	public Object g( Object $k ){return _player.get($k).g(this);}
	public Object s( Object $k, Object $v ){return _player.get( $k ).s( this, $v );}
	public Object S( Object...arg ){return _s( this, p, _player, arg );}
}

//endregion

//region Event

//bs event
static private final int E_bsTouch = 2000;
static public final int E_down = 2001;
static public final int E_up = 2002;
static public final int E_move = 2003;
static public final int E_end = 2004;
{
	_view.put( E_down, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_down );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.touch( E_down, $b.runnable( $v ) );
			return $v;
		}
	} );
	_view.put( E_up, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_up );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.touch( E_up, $b.runnable( $v ) );
			return $v;
		}
	} );
	_view.put( E_move, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_move );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.touch( E_move, $b.runnable( $v ) );
			return $v;
		}
	} );
}
//view event
static public final int E_timeChange = 3001;
static public final int E_itemClick = 3002;
static public final int E_itemLongClick = 3003;
static public final int E_itemSelected = 3004;
static public final int E_checked = 3005;
static public final int E_click = 3006;
static public final int E_drag = 3007;
static public final int E_focusChange = 3008;
static public final int E_genericMotion = 3009;
static public final int E_hover = 3010;
static public final int E_key = 3011;
static public final int E_longClick = 3012;
static public final int E_touch = 3013;
{
	_view.put( E_checked, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_checked );
		}

		public Object s( final bsView $b, final Object $v ){
			CompoundButton.OnCheckedChangeListener v = $v == null ? null : (CompoundButton.OnCheckedChangeListener) listenerGet( $v, EcheckedNone );
			( (CompoundButton) $b.v ).setOnCheckedChangeListener( v );
			$b.p.put( E_checked, v );
			return v;
		}
	} );
	_view.put( E_itemClick, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_itemClick );
		}

		public Object s( final bsView $b, final Object $v ){
			AdapterView.OnItemClickListener v = $v == null ? null : (AdapterView.OnItemClickListener) listenerGet( $v, EitemClickNone );
			( (AdapterView) $b.v ).setOnItemClickListener( v );
			$b.p.put( E_itemClick, v );
			return $v;
		}
	} );
	_view.put( E_timeChange, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_timeChange );
		}

		public Object s( final bsView $b, final Object $v ){
			( (TimePicker) $b.v ).setOnTimeChangedListener( (TimePicker.OnTimeChangedListener) $v );
			$b.p.put( E_timeChange, $v );
			return $v;
		}
	} );

	_view.put( E_itemLongClick, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_itemLongClick );
		}

		public Object s( final bsView $b, final Object $v ){
			( (AdapterView) $b.v ).setOnItemLongClickListener( (AdapterView.OnItemLongClickListener) $v );
			$b.p.put( E_itemLongClick, $v );
			return $v;
		}
	} );
	_view.put( E_itemSelected, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_itemSelected );
		}

		public Object s( final bsView $b, final Object $v ){
			( (AdapterView) $b.v ).setOnItemSelectedListener( (AdapterView.OnItemSelectedListener) $v );
			$b.p.put( E_itemSelected, $v );
			return $v;
		}
	} );


	_view.put( E_click, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_click );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnClickListener( (View.OnClickListener) $v );
			$b.p.put( E_click, $v );
			return $v;
		}
	} );
	_view.put( E_drag, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_drag );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnDragListener( (View.OnDragListener) $v );
			$b.p.put( E_drag, $v );
			return $v;
		}
	} );
	_view.put( E_focusChange, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_focusChange );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnFocusChangeListener( (View.OnFocusChangeListener) $v );
			$b.p.put( E_focusChange, $v );
			return $v;
		}
	} );
	_view.put( E_genericMotion, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_genericMotion );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnGenericMotionListener( (View.OnGenericMotionListener) $v );
			$b.p.put( E_genericMotion, $v );
			return $v;
		}
	} );
	_view.put( E_hover, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_hover );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnHoverListener( (View.OnHoverListener) $v );
			$b.p.put( E_hover, $v );
			return $v;
		}
	} );
	_view.put( E_key, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_key );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnKeyListener( (View.OnKeyListener) $v );
			$b.p.put( E_key, $v );
			return $v;
		}
	} );
	_view.put( E_longClick, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_longClick );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnLongClickListener( (View.OnLongClickListener) $v );
			$b.p.put( E_longClick, $v );
			return $v;
		}
	} );
	_view.put( E_touch, new _View(){
		public Object g( final bsView $b ){
			return $b.p.get( E_touch );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnTouchListener( (View.OnTouchListener) $v );
			$b.p.put( E_touch, $v );
			return $v;
		}
	} );
}
static public Dclick DclickNone = new Dclick(){public void run( DialogInterface $v, P $p ){}};
static public abstract class Dclick extends D implements DialogInterface.OnClickListener{
	public Dclick(Object...arg){super(arg);}
	public void onClick( DialogInterface d, int i ){
		p.put( D_index, i );
		run( d, p );
	}
}
static private abstract class D{
	public void run( DialogInterface $v, P $p ){}
	protected P p;
	protected D( Object[] arg ){
		p = hash();
		int j = arg.length;
		if( j > 0 ){
			int i = 0;
			while( i < j ) p.put( arg[i++], arg[i++] );
		}
	}
}

static public Echecked EcheckedNone = new Echecked(){};
static public abstract class Echecked extends E implements CompoundButton.OnCheckedChangeListener{
	public Echecked(Object...arg){super( arg );}
	public void onCheckedChanged( CompoundButton $v, boolean $b ){
		p.put( V_checked, $b );
		run( $v, p );
	}
}

static public EitemClick EitemClickNone = new EitemClick(){};
static public abstract class EitemClick extends E implements AdapterView.OnItemClickListener{
	public EitemClick(Object...arg){super( arg );}
	public void onItemClick( AdapterView $adapterView, View $v, int $idx, long l ){
		if( $adapterView.getAdapter() instanceof AdapterCursor ){
			p.put( V_rowid, ((AdapterCursor)$adapterView.getAdapter()).rowid($idx) );
		}
		p.put( V_parent, $adapterView );
		p.put( V_index, $idx );
		run( $v, p );
	}
}
static private abstract class E{
	public void run( View $v, P $p ){}
	protected P p;
	protected E( Object[] arg ){
		p = hash();
		int j = arg.length;
		if( j > 0 ){
			int i = 0;
			while( i < j ) p.put( (String)arg[i++], arg[i++] );
		}
	}
}
static private Object listenerGet( Object $v, Object $none ){
	if( $v instanceof Integer ){
		switch((Integer)$v){
		case NONE:return $none;
		}
	}
	return $v;
}

//endregion

//region Dialog

static public final int D_show = 1000;
static public final int D_title = 1001;
static public final int D_adapter = 1002;
static public final int D_message = 1003;
static public final int D_cursor = 1004;
static public final int D_cursorColume = 1005;
static public final int D_yes = 1006;
static public final int D_no = 1007;
static public final int D_ok = 1008;
static public final int D_yesLable = 1009;
static public final int D_noLable = 1010;
static public final int D_okLable = 1011;
static public final int D_view = 1012;
static public final int D_array = 1013;
static public final int D_index = 1014;
{
	_dialog.put( D_title, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			if( $v instanceof String ) $d.builder.setTitle( (String) $v );
			else if( $v instanceof Integer ) $d.builder.setTitle( (Integer) $v );
			else if( $v instanceof View ) $d.builder.setCustomTitle( (View) $v );
			$d.p.put( D_title, $v );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_title );
		}
	} );
	_dialog.put( D_adapter, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( D_adapter, $v );
			if( $d.p.containsKey( E_click ) )
				$d.builder.setAdapter( (ListAdapter) $v, (DialogInterface.OnClickListener) $d.p.get( E_click ) );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_adapter );
		}
	} );
	_dialog.put( D_message, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( D_message, $v );
			$d.builder.setMessage( (String) $v );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_message );
		}
	} );
	_dialog.put( D_cursor, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			Cursor c = (Cursor) $v;
			$d.p.put( D_cursor, c );
			if( $d.p.containsKey( D_cursorColume ) && $d.p.containsKey( E_click ) )
				$d.builder.setCursor( c, (DialogInterface.OnClickListener) $d.p.get( E_click ), (String) $d.p.get( D_cursorColume ) );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_cursor );
		}
	} );
	_dialog.put( D_cursorColume, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			String v = null;
			if( $v instanceof String ) v = (String) $v;
			else if( $v instanceof Integer && $d.p.containsKey( D_cursor ) )
				v = ( (Cursor) $d.p.get( D_cursor ) ).getColumnNames()[(Integer) $v];
			$d.p.put( D_cursorColume, v );
			if( $d.p.containsKey( D_cursor ) && $d.p.containsKey( E_click ) )
				$d.builder.setCursor( (Cursor) $d.p.get( D_cursor ), (DialogInterface.OnClickListener) $d.p.get( E_click ), v );
			return v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_cursorColume );
		}
	} );
	_dialog.put( D_yes, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) listenerGet( $v, DclickNone );
			$d.builder.setPositiveButton( $d.p.containsKey(D_yesLable) ? (String) $d.p.get(D_yesLable) : "confirm", v );
			$d.p.put( D_yes, v );
			return v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_yes );
		}
	} );
	_dialog.put( D_no, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) listenerGet( $v, DclickNone );
			$d.builder.setNegativeButton( $d.p.containsKey(D_noLable) ? (String)$d.p.get( D_noLable ) : "cancel", v );
			$d.p.put( D_no, v );
			return v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_no );
		}
	} );
	_dialog.put( D_ok, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) listenerGet( $v, DclickNone );
			$d.builder.setNeutralButton( $d.p.containsKey(D_okLable) ? (String)$d.p.get( D_okLable ) : "ok", v );
			$d.p.put( D_ok, v );
			return v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_ok );
		}
	} );
	_dialog.put( D_yesLable, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( D_yesLable, $v );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_yesLable );
		}
	} );
	_dialog.put( D_noLable, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( D_noLable, $v );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_noLable );
		}
	} );
	_dialog.put( D_okLable, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( D_okLable, $v );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_okLable );
		}
	} );
	_dialog.put( D_view, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.builder.setView( (View) $v );
			$d.p.put( D_view, $v );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_view );
		}
	} );
	_dialog.put( D_array, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( D_array, $v );
			if( $d.p.containsKey( E_click ) )
				$d.builder.setItems( (String[]) $v, (DialogInterface.OnClickListener) $d.p.get( E_click ) );
			return $v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( D_array );
		}
	} );
	_dialog.put( D_show, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.show();
			return null;
		}
		public Object g( final bsDialog $d ){
			$d.show();
			return null;
		}
	} );
	_dialog.put( E_click, new _Dialog(){
		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) listenerGet( $v, DclickNone );
			if( v != null ){
				$d.p.put( E_click, v );
				if( $d.p.containsKey( D_adapter ) )
					$d.builder.setAdapter( (ListAdapter) $d.p.get( D_adapter ), v );
				else if( $d.p.containsKey( D_array ) )
					$d.builder.setItems( (String[]) $d.p.get( D_array ), v );
				else if( $d.p.containsKey( D_cursor ) && $d.p.containsKey( D_cursorColume ) )
					$d.builder.setCursor( (Cursor) $d.p.get( D_cursor ), v, (String) $d.p.get( D_cursorColume ) );

			}
			return v;
		}
		public Object g( final bsDialog $d ){
			return $d.p.get( E_itemClick );
		}
	} );
}
private interface  _Dialog{
	Object s( final bsDialog $t, final Object $v );
	Object g( final bsDialog $t );
}
static private HashMap<Object, _Dialog> _dialog = new HashMap<Object, _Dialog>();
private HashMap<String, bsDialog> dialog = new HashMap<String, bsDialog>();
public bsDialog Dialog( String $key, Object...arg ){
	if( _fm == null ) _fm = _act.getFragmentManager();
	if( !dialog.containsKey($key) ) dialog.put( $key, new bsDialog( this, $key, new AlertDialog.Builder(_act), _fm, arg ){} );
	return dialog.get($key);
}
static public abstract class bsDialog extends DialogFragment implements Runnable, SG{

	private BS bs;
	private FragmentManager fm;
	private AlertDialog.Builder builder;
	private String _key;

	private P p;

	private bsDialog( BS $bs, String $key, AlertDialog.Builder $b, FragmentManager $f, Object[] arg ){
		bs = $bs;
		_key = $key;
		builder = $b;
		fm = $f;
		p = _init(arg);
	}
	public Dialog onCreateDialog( Bundle $saved ){return builder.create();}
	private void show(){show( fm, _key );}
	public void run(){show();}

	public Object g( Object $k ){return _dialog.get($k).g(this);}
	public Object s( Object $k, Object $v ){return _dialog.get($k).s( this, $v );}
	public Object S(Object...arg){return _s( this, p, _dialog, arg );}
	public Object A( Object... arg ){return _a( bs.handlerDialog, this, p, _dialog, arg );}
}
private Handler handlerDialog = new Handler(){
	@Override
	public void handleMessage( Message $msg ){
		((SG)$msg.obj).S(asyncArgs.get($msg.arg1));
		asyncArgs.remove( $msg.arg1 );
	}
};

//endregion

//region View

//region Const View

static public final String V_ROOT = "V_ROOT";
static public final String V_span = "V_span";
static public final String V_scaleX = "V_scaleX";
static public final String V_scaleY = "V_scaleY";
static public final String V_x = "V_x";
static public final String V_y = "V_y";
static public final String V_background = "V_background";
static public final String V_margin = "V_margin";
static public final String V_alpha = "V_alpha";
static public final String V_layout = "V_layout";
static public final String V_visible = "V_visible";
static public final String V_id = "V_id";
static public final String V_tag = "V_tag";
static public final String V_param = "V_param";
static public final String V_parent = "V_<";
static public final String V_focus = "V_focus";
static public final String V_next = "V_next";
static public final String V_adapter = "V_adapter";
static public final String V_view = "V_view";
static public final String V_children = "V_children";
static public final String V_child = "V_child";
static public final String V_this = "V_this";
static public final String V_textColor = "V_textColor";
static public final String V_checked = "V_checked";
static public final String V_image = "V_image";
static public final String V_text = "V_text";
static public final String V_textScaleX = "V_textScaleX";
static public final String V_lineSpacing = "V_lineSpacing";
static public final String V_index = "V_index";
static public final String V_rowid = "V_rowid";

//endregion

//region Const Widgets

static public final String WV_url = "WV_url";
static public final String WV_js = "WV_js";
static public final String WV_apiKey = "WV_apiKey";
static public final String WV_api = "WV_api";
static public final String WV_isJS = "WV_isJS";
static public final String WV_file = "WV_file";
static public final String WV_zoom = "WV_zoom";
static public final String WV_client = "WV_client";
static public final String WV_chrome = "WV_chrome";
static public final String WV_layer = "WV_layer";
static public final String WV_priority = "WV_priority";
static public final String WV_cache = "WV_cache";
static public final String WV_cachePath = "WV_cachePath";
static public final String WV_cacheSize = "WV_cacheSize";
static public final String WV_agent = "WV_agent";
static public final String WV_encoding = "WV_encoding";
static public final String WV_header = "WV_header";
static public final String WV_canGoBack = "WV_canGoBack";
static public final String WV_back = "WV_back";
static public final String WV_multipleWindow = "WV_multipleWindow";

static public final String TP_24 = "TP_24";
static public final String TP_hour = "TP_hour";
static public final String TP_minute = "TP_minute";
static public final String PB_max = "PB_max";
static public final String PB_progress = "PB_progress";

//endregion

//region Const Animation

static private final String A_animation = "A_animation";
static private final String A_cancelListener = "A_cancelListener";
static private final String A_endListener = "A_endListener";
static private final String A_repeatListener = "A_repeatListener";
static private final String A_startListener = "A_startListener";
static private final String A_listener = "A_listener";
static public final String A_alpha = "A_alpha";
static public final String A_rotation = "A_rotation";
static public final String A_rotationX = "A_rotationX";
static public final String A_rotationY = "A_rotationY";
static public final String A_scaleX = "A_scaleX";
static public final String A_scaleY = "A_scaleY";
static public final String A_translationX = "A_translationX";
static public final String A_translationY = "A_translationY";
static public final String A_x = "A_x";
static public final String A_y = "A_y";
static public final String A_duration = "A_duration";
static public final String A_delay = "A_delay";
static public final String A_ease = "A_ease";
//event
static public final String A_update = "A_update";
static public final String A_ended = "A_ended";
static public final String A_started = "A_started";
static public final String A_canceled = "A_canceled";
static public final String A_repeated = "A_repeated";
//action
static public final String A_cancel = "A_cancel";
static public final String A_start = "A_start";

//endregion

//region Const New

static public final String NEW_WebView = "A_alpha";

//endregion

{
	_viewNew.put( NEW_WebView, new N(){public View r(){return new WebView( BS.this._act );}});


	//region Attribute

	_view.put( V_textColor, new _View(){
		public Object g( final bsView $b ){return $b.p.get(V_textColor);}
		public Object s( final bsView $b, final Object $v ){
			int c = $v instanceof Integer ? (Integer)$v : str2color((String)$v);
			textColor( $b.v, c );
			$b.p.put( V_textColor, c );
			return c;
		}
	});
	_view.put( V_view, new _View(){
		public Object g( final bsView $b ){return $b.v;}
		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof String ){
				String v = (String)$v;
				$b.v = _viewNew.containsKey(v) ? _viewNew.get(v).r() : _viewIDs.containsKey(v) ? _act.findViewById(_viewIDs.get(v)) : null;
			}else if( $v instanceof Integer ){
				int v = (Integer)$v;
				$b.v = Rtype(v) == _RtypeLayout ? Rlayout(v) : _act.findViewById(v);
			}else if( $v instanceof View ) $b.v = (View)$v;
			return $v;
		}
	});
	_view.put( V_this, new _View(){public Object g( final bsView $b ){return $b;}});
	_view.put( V_span, new _View(){
		public Object s( final bsView $b, final Object $v ){
			TableRow.LayoutParams params = (TableRow.LayoutParams) $b.v.getLayoutParams();
			params.span = (Integer)$v;
			return $v;
		}
	});
	_view.put( V_x, new _View(){
		public Object g( final bsView $b ){return $b.v.getX();}
		public Object s( final bsView $b, final Object $v ){$b.v.setX((Float)$v);return $v;}
	});
	_view.put( V_y, new _View(){
		public Object g( final bsView $b ){return $b.v.getY();}
		public Object s( final bsView $b, final Object $v ){$b.v.setY((Float)$v);return $v;}
	});
	_view.put( V_scaleX, new _View(){
		public Object g( final bsView $b ){return $b.v.getScaleX();}
		public Object s( final bsView $b, final Object $v ){$b.v.setScaleX((Float)$v);return $v;}
	});
	_view.put( V_scaleY, new _View(){
		public Object g( final bsView $b ){return $b.v.getScaleY();}
		public Object s( final bsView $b, final Object $v ){$b.v.setScaleY((Float)$v);return $v;}
	});
	_view.put( V_background, new _View(){
		public Object g( final bsView $b ){return $b.p.get("background");}
		@SuppressLint("NewApi")
		public Object s( final bsView $b, final Object $v ){
			$b.p.put("background",$v);
			if( $v instanceof String ) $b.v.setBackgroundColor(BS.str2color((String) $v));
			else if( $v instanceof Drawable ) $b.v.setBackground((Drawable)$v);
			return $v;
		}
	});
	_view.put( V_margin, new _View(){
		public Object g( final bsView $b ){return $b.p.containsKey("margin") ? $b.p.get("margin") : _viewMargin;}
		public Object s( final bsView $b, final Object $v ){
			int[] v = (int[])$v;
			ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) $b.v.getLayoutParams();
			param.setMargins( v[0], v[1], v[2], v[3] );
			$b.p.put( "margin", v );
			$b.v.setLayoutParams(param);
			return $v;
		}
	});
	_view.put( V_alpha, new _View(){
		public Object g( final bsView $b ){return $b.v.getAlpha();}
		public Object s( final bsView $b, final Object $v ){$b.v.setAlpha((Float)$v);return $v;}
	} );
	_view.put( V_layout, new _View(){
		public Object g( final bsView $b ){return $b.p.containsKey("layout") ? $b.p.get("layout") : _viewLayout;}
		public Object s( final bsView $b, final Object $v ){
			ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) $b.v.getLayoutParams();
			int[] v = (int[])$v;
			$b.p.put( "layout", v );
			param.width = v[0];
			param.height = v[1];
			if( $b.p.containsKey("margin") ) {
				v = (int[]) $b.p.get("margin");
				param.setMargins( v[0], v[1], v[2], v[3] );
			}
			$b.v.setLayoutParams(param);
			return $v;
		}
	} );
	_view.put( V_visible, new _View(){
		public Object g( final bsView $b ){return $b.v.getVisibility();}
		public Object s( final bsView $b, final Object $v ){$b.v.setVisibility( (Boolean)$v ? View.VISIBLE : View.INVISIBLE );return $v;}
	});
	_view.put( V_id, new _View(){
		public Object g( final bsView $b ){return $b.v.getId();}
		public Object s( final bsView $b, final Object $v ){
			String v = (String)$v;
			if( _viewIDs.containsKey(v) ){
				log( "Existed ID ::" + v );
				return $v;
			}
			int id = _viewID;
			_viewIDs.put( v, id );
			$b.v.setId(id);
			_viewID++;
			return $v;
		}
	});
	_view.put( V_tag, new _View(){
		public Object g( final bsView $b ){return $b.v.getTag();}
		public Object s( final bsView $b, final Object $v ){$b.v.setTag($v);return $v;}
	});
	_view.put( V_param, new _View(){
		public Object g( final bsView $b ){return $b.p.clone();}
		public Object s( final bsView $b, final Object $v ){$b.p.putAll((HashMap<String, Object>)$v);return $v;}
	});
	_view.put( V_focus, new _View(){
		public Object s( final bsView $b, final Object $v ){
			Boolean v = (Boolean)$v;
			$b.v.setFocusable(v);
			if( v ) $b.v.requestFocus();
			$b.v.setFocusableInTouchMode(v);
			return $v;
		}
	} );
	_view.put( V_parent, new _View(){
		public Object g( final bsView $b ){return $b.v.getParent();}
		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof String ){
				String v = (String)$v;
				if( v.equals(V_ROOT) ) BS.this.contents($b.v);
				else ((ViewGroup)BS.this.View((String)$v).v).addView($b.v);
			}else if( $v instanceof Integer ) ((ViewGroup)BS.this.View((Integer)$v).v).addView($b.v);
			else if( $v instanceof View ) ((ViewGroup)BS.this.View((View)$v).v).addView($b.v);
			else if( $v == null ){
				ViewGroup p = (ViewGroup)$b.v.getParent();
				if( p != null ) p.removeView($b.v);
			}
			return $v;
		}
	} );
	_view.put( V_children, new _View(){
		public Object g( final bsView $b ){
			ViewGroup v = (ViewGroup)$b.v;
			int j = v.getChildCount();
			View[] t0 = new View[j];
			for( int i = 0 ; i < j ; i++ ) t0[i] = v.getChildAt(i);
			return t0;
		}
	} );
	_view.put( V_child, new _View(){
		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Integer ) return ((ViewGroup)$b.v).getChildAt((Integer)$v);
			else if( $v instanceof View ) ((ViewGroup)$b.v).addView((View)$v);
			return $v;
		}
	} );
	_view.put( V_next, new _View(){public Object s( final bsView $b, final Object $v ){((Runnable)$v).run();return $v;}});
	_view.put( V_adapter, new _View(){
		public Object g( final bsView $b ){return ((AdapterView)$b.v).getAdapter();}
		public Object s( final bsView $b, final Object $v ){((AdapterView)$b.v).setAdapter((Adapter)$v);return $v;}
	} );
	_view.put( V_text, new _View(){
		public Object g( final bsView $b ){return ((TextView)$b.v).getText();}
		public Object s( final bsView $b, final Object $v ){((TextView)$b.v).setText( $v instanceof String ? (String)$v : BS.this.Rstring((Integer)$v) );return $v;}
	});
	_view.put( V_textScaleX, new _View(){public Object s( final bsView $b, final Object $v ){((TextView)$b.v).setTextScaleX((Float)$v);return $v;}});
	_view.put( V_lineSpacing, new _View(){public Object s( final bsView $b, final Object $v ){((TextView)$b.v).setLineSpacing( (Float)$v, 1 );return $v;}});
	_view.put( V_image, new _View(){
		public Object g( final bsView $b ){return ((ImageView)$b.v).getDrawable();}
		public Object s( final bsView $b, final Object $v ){
			ImageView v = (ImageView)$b.v;
			if( $v instanceof Integer ) v.setImageResource((Integer)$v);
			else if( $v instanceof String ) v.setImageDrawable(Rdrawable((String)$v));
			else if( $v instanceof Drawable ) v.setImageDrawable((Drawable)$v);
			return $v;
		}
	});
	_view.put( V_checked, new _View(){
		public Object g( final bsView $b ){return ( ( CompoundButton ) $b.v ).isChecked();}
		public Object s( final bsView $b, final Object $v ){( ( CompoundButton ) $b.v ).setChecked((Boolean)$v);return $v;}
	});

	//endregion

	//region Animation

	_view.put( A_alpha, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().alpha((Float) $v);
		else{
			float[] v = (float[])$v;
			$b.v.setAlpha(v[0]);
			$b.ani().alpha(v[1]);
		}
		return $v;
	}});
	_view.put( A_rotation, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().rotation((Float) $v);
		else{
			float[] v = (float[])$v;
			$b.v.setRotation(v[0]);
			$b.ani().rotation(v[1]);
		}
		return $v;
	}});
	_view.put( A_rotationX, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().rotationX((Float) $v);
		else{
			float[] v = (float[])$v;
			$b.v.setRotationX(v[0]);
			$b.ani().rotationX(v[1]);
		}
		return $v;
	}});
	_view.put( A_rotationY, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().rotationY((Float) $v);
		else{
			float[] v = (float[])$v;
			$b.v.setRotationY(v[0]);
			$b.ani().rotationY(v[1]);
		}
		return $v;
	}});
	_view.put( A_scaleX, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().scaleX((Float) $v);
		else{
			float[] v = (float[])$v;
			$b.v.setScaleX(v[0]);
			$b.ani().scaleX(v[1]);
		}
		return $v;
	}});
	_view.put( A_scaleY, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().scaleY((Float)$v);
		else{
			float[] v = (float[])$v;
			$b.v.setScaleY(v[0]);
			$b.ani().scaleY(v[1]);
		}
		return $v;
	}});
	_view.put( A_translationX, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().translationX((Float)$v);
		else{
			float[] v = (float[])$v;
			$b.v.setTranslationX(v[0]);
			$b.ani().translationX(v[1]);
		}
		return $v;
	}});
	_view.put( A_translationY, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().translationY((Float)$v);
		else{
			float[] v = (float[])$v;
			$b.v.setTranslationY(v[0]);
			$b.ani().translationY(v[1]);
		}
		return $v;
	}});
	_view.put( A_x, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().x((Float)$v);
		else{
			float[] v = (float[])$v;
			$b.v.setX(v[0]);
			$b.ani().x(v[1]);
		}
		return $v;
	}});
	_view.put( A_y, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Float ) $b.ani().y((Float)$v);
		else{
			float[] v = (float[])$v;
			$b.v.setY(v[0]);
			$b.ani().y(v[1]);
		}
		return $v;
	}});
	_view.put( A_duration, new _View(){
		public Object g( final bsView $b ){return $b.ani().getDuration();}
		public Object s( final bsView $b, final Object $v ){$b.ani().setDuration((Long)$v);return $v;}
	});

	_view.put( A_delay, new _View(){
		public Object g( final bsView $b ){return $b.ani().getStartDelay();}
		public Object s( final bsView $b, final Object $v ){$b.ani().setStartDelay((Long)$v);return $v;}
	});
	_view.put( A_ease, new _View(){
		@SuppressLint("NewApi")
		public Object g( final bsView $b ){return $b.ani().getInterpolator();}
		public Object s( final bsView $b, final Object $v ){$b.ani().setInterpolator((TimeInterpolator) $v);return $v;}
	});

	_view.put( A_canceled, new _View(){public Object s( final bsView $b, final Object $v ){$b.aniListener( A_cancelListener, $b.runnable($v) );return $v;}});
	_view.put( A_repeated, new _View(){public Object s( final bsView $b, final Object $v ){$b.aniListener(A_repeatListener, $b.runnable($v) );return $v;}});
	if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ){
		_view.put( A_ended, new _View(){public Object s( final bsView $b, final Object $v ){$b.aniListener( A_endListener, $b.runnable($v) );return $v;}});
		_view.put( A_started, new _View(){public Object s( final bsView $b, final Object $v ){$b.aniListener( A_startListener, $b.runnable($v) );return $v;}});
	}else{
		_view.put(A_update, new _View() {
			@SuppressLint("NewApi")
			public Object s( final bsView $b, final Object $v ){$b.ani().setUpdateListener((ValueAnimator.AnimatorUpdateListener) $v);return $v;}
		});
		_view.put(A_ended, new _View() {
			@SuppressLint("NewApi")
			public Object s( final bsView $b, final Object $v ){$b.ani().withEndAction($b.runnable($v));return $v;}
		});
		_view.put(A_started, new _View() {
			@SuppressLint("NewApi")
			public Object s( final bsView $b, final Object $v ){$b.ani().withStartAction($b.runnable($v));return $v;}
		});
	}

	_view.put( A_cancel, new _View(){public Object s( final bsView $b, final Object $v ){$b.ani().cancel();return $v;}});
	_view.put( A_start, new _View(){public Object s( final bsView $b, final Object $v ){
		if( $v instanceof Long[] ){
			Long[] v = (Long[])$v;
			$b.ani().setDuration(v[0]);
			$b.ani(true).setStartDelay(v[1]);
		}else if( $v instanceof Long ) $b.ani().setDuration((Long)$v);
		$b.ani(true).start();
		return $v;
	}});

	//endregion

	//region Widgets

	_viewWebViewClient = new WebViewClient(){
		public void onPageStarted( WebView $v, String $url, Bitmap $favicon ){log( "WebviewPageStarted:" + $url );}
		public void onLoadResource( WebView $v, String $url ){super.onLoadResource( $v, $url );}
		public boolean shouldOverrideUrlLoading( WebView $v, String $url ){$v.loadUrl($url);return true;}
	};
	_viewWebChromeClient = new WebChromeClient(){
		public boolean onJsAlert( WebView $v, String $url, String $msg, final android.webkit.JsResult $result ){
			new AlertDialog.Builder($v.getContext()).setMessage($msg).setNeutralButton( "확인", DclickNone ).show();
			return true;
		}
	};
	_view.put( WV_client, new _View(){public Object s( final bsView $b, final Object $v ){
		WebViewClient v = null;
		if( $v instanceof Integer ){
			if( (Integer)$v == NONE ) v = _viewWebViewClient;
		}else v = (WebViewClient)$v;
		((WebView)$b.v).setWebViewClient(v);
		return $v;
	}});
	_view.put( WV_chrome, new _View(){public Object s( final bsView $b, final Object $v ){
		WebChromeClient v = null;
		if( $v instanceof Integer ){
			if( (Integer)$v == NONE ) v = _viewWebChromeClient;
		}else v = (WebChromeClient)$v;
		((WebView)$b.v).setWebChromeClient(v);
		return $v;
	}});

	_view.put( WV_url, new _View(){public Object s( final bsView $b, final Object $v ){
		WebView v = (WebView)$b.v;
		if( $b.p.containsKey(WV_header) ) v.loadUrl( (String)$v, (Map<String, String>)$b.p.get(WV_header) );
		else v.loadUrl((String)$v);
		return $v;
	}});
	_view.put( WV_js, new _View(){public Object s( final bsView $b, final Object $v ){
		WebView v = (WebView)$b.v;
		if( $b.p.containsKey(WV_header) ) v.loadUrl( "javascript:" + (String)$v, (Map<String, String>)$b.p.get(WV_header) );
		else v.loadUrl("javascript:" + (String)$v);
		return $v;
	}});
	_view.put( WV_apiKey, new _View(){public Object s( final bsView $b, final Object $v ){$b.p.put("apiKey", $v);return $v;}});
	_view.put( WV_api, new _View(){public Object s( final bsView $b, final Object $v ){((WebView)$b.v).addJavascriptInterface($v, $b.p.containsKey("apiKey") ? $b.p.getString( "apiKey" ) : "Bs");return $v;}});
	_view.put( WV_isJS, new _View(){public Object s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setJavaScriptEnabled((Boolean) $v);return $v;}});
	_view.put( WV_file, new _View() {
		public Object s( final bsView $b, final Object $v ) {
			WebSettings s = ((WebView) $b.v).getSettings();
			Boolean v = (Boolean)$v;
			s.setAllowFileAccess(v);
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) s.setAllowUniversalAccessFromFileURLs(v);
			return $v;
		}
	});
	_view.put( WV_zoom, new _View(){public Object s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setSupportZoom((Boolean) $v);return $v;}});
	_view.put( WV_layer, new _View(){public Object s( final bsView $b, final Object $v ){((WebView)$b.v).setLayerType( (Integer)$v, null );return $v;}});
	if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ){
		_view.put( WV_priority, new _View(){public Object s( final bsView $b, final Object $v ){((WebView) $b.v).getSettings().setRenderPriority((WebSettings.RenderPriority) $v);return $v;}});
		_view.put( WV_cacheSize, new _View(){public Object s( final bsView $b, final Object $v ){((WebView) $b.v).getSettings().setAppCacheMaxSize((Long) $v);return $v;}});
	}
	_view.put( WV_cache, new _View(){public Object s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setAppCacheEnabled((Boolean) $v);return $v;}});
	_view.put( WV_cachePath, new _View(){public Object s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setAppCachePath((String)$v);return $v;}});
	_view.put(WV_agent, new _View() {
		public Object g( final bsView $b ){return System.getProperty("http.agent");}
		public Object s( final bsView $b, final Object $v ){((WebView) $b.v).getSettings().setUserAgentString((String) $v);return $v;}
	});
	_view.put( WV_encoding, new _View(){public Object s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setDefaultTextEncodingName((String) $v);return $v;}});
	_view.put( WV_header, new _View(){
		public Object g( final bsView $b ){return $b.p.get(WV_header);}
		public Object s( final bsView $b, final Object $v ){
			if( !$b.p.containsKey(WV_header) ) $b.p.put( WV_header, new HashMap<String, String>() );
			((HashMap<String, String>)$b.p.get(WV_header)).putAll((HashMap<String, String>)$v);
			return $v;
		}
	});
	_view.put( WV_canGoBack, new _View(){
		public Object g( final bsView $b ){return ((WebView)$b.v).canGoBack();}
		public Object s( final bsView $b, final Object $v ){return ((WebView)$b.v).canGoBack();}
	});
	_view.put( WV_back, new _View(){
		public Object g( final bsView $b ){((WebView)$b.v).goBack();return null;}
		public Object s( final bsView $b, final Object $v ){((WebView)$b.v).goBack();return $v;}
	});
	_view.put( WV_multipleWindow, new _View(){
		public Object g( final bsView $b ){return $b.p.get(WV_multipleWindow);}
		public Object s( final bsView $b, final Object $v ){
			$b.p.put( WV_multipleWindow, $v );
			((WebView)$b.v).getSettings().setSupportMultipleWindows((Boolean)$v);
			return $v;
		}
	});

	_view.put( TP_24, new _View(){
		public Object s( final bsView $b, final Object $v ){((TimePicker)$b.v).setIs24HourView((Boolean)$v);return $v;}
	});
	_view.put( TP_hour, new _View(){
		public Object g( final bsView $b ){return ((TimePicker)$b.v).getCurrentHour();}
		public Object s( final bsView $b, final Object $v ){((TimePicker)$b.v).setCurrentHour((Integer)$v);return $v;}
	});
	_view.put( TP_minute, new _View(){
		public Object g( final bsView $b ){return ((TimePicker)$b.v).getCurrentMinute();}
		public Object s( final bsView $b, final Object $v ){((TimePicker)$b.v).setCurrentMinute((Integer)$v);return $v;}
	});
	_view.put( PB_max, new _View(){
		public Object g( final bsView $b ){return ((ProgressBar)$b.v).getMax();}
		public Object s( final bsView $b, final Object $v ){((ProgressBar)$b.v).setMax((Integer)$v);return $v;}
	});
	_view.put( PB_progress, new _View(){
		public Object g( final bsView $b ){return ((ProgressBar)$b.v).getProgress();}
		public Object s( final bsView $b, final Object $v ){((ProgressBar)$b.v).setProgress((Integer)$v);return $v;}
	});

	//endregion
}

//region view def

private abstract class _View{
	public Object s( final bsView $v0, final Object $v1 ){return $v1;}
	public Object g( final bsView $v0 ){return null;}
}
static private HashMap<Object, _View> _view = new HashMap<Object, _View>();
private HashMap<View, bsView> view = new HashMap<View, bsView>();
private bsView viewSet( View $v, boolean $isCache ){
	if( $isCache ){
		if( view.containsKey( $v ) ) return view.get( $v );
		else{
			bsView t0;
			int i = _viewPool.size();
			if( i > 0 ){
				t0 = _viewPool.get( i - 1 );
				_viewPool.remove( i - 1 );
			}else t0 = new bsView();
			view.put( $v, t0.init($v) );
			return t0;
		}
	}else return new bsView().init($v);
}
public class bsView implements SG{

	public float[][] motionPoint = null;
	public MotionEvent motionEvent = null;
	public int motionType;
	private View v;

	private P p;

	private bsView init( View $v ){
		if( $v == null ){
			view.remove( v );
			hashPool(p);
			p = null;
		}else{
			Object tag = $v.getTag();
			if( tag == _viewParam ){
				p = (P)tag;
				$v.setTag(null);
			}else p = hash();
		}
		v = $v;
		return this;
	}
	private void touch( int $k, Runnable $v ){
		if( !p.containsKey(E_bsTouch) ){
			motionPoint = new float[5][2];
			View.OnTouchListener t0 = new View.OnTouchListener(){
				public boolean onTouch( View $v, MotionEvent $e ){
					motionEvent = $e;
					int t0 = motionType = _viewTouchAction[$e.getAction()];
					if( p.containsKey(t0) ){
						int j = $e.getPointerCount();
						if( 0 < j && j < 5 ){
							float w = 1, h = 1;
							BS bs = BS.this;
							if( bs._isVirtual ){
								w = bs.virtual_widthRate;
								h = bs.virtual_heightRate;
							}
							for( int i = 0 ; i < j ; i++ ){
								int id = $e.getPointerId( i );
								motionPoint[id][0] = $e.getX( i ) * w;
								motionPoint[id][1] = $e.getY( i ) * h;
							}
							p.getRunnable(t0).run();
							return true;
						}
					}
					try{
						Thread.sleep( 15 );
					}catch( Exception e ){
						BS.log( e.toString() );
					}
					return true;
				}
			};
			p.put( E_bsTouch, t0 );
			v.setOnTouchListener( t0 );
		}
		p.put( $k, $v );
	}
	private ViewPropertyAnimator ani( Boolean $isStart ){
		ViewPropertyAnimator t0 = ani();
		p.remove( A_animation );
		p.remove( A_delay );
		return t0;
	}
	private ViewPropertyAnimator ani(){
		if( !p.containsKey( A_animation ) ) p.put( A_animation, v.animate() );
		return ( ViewPropertyAnimator ) p.get( A_animation );
	}
	private void aniListener( String $k, Runnable $v ){
		p.put( $k, $v );
		if( !p.containsKey( A_listener ) ){
			p.put( A_listener, new Animator.AnimatorListener(){
				public void onAnimationCancel( Animator animation ){
					if( p.containsKey( A_cancelListener ) ) p.getRunnable( A_cancelListener ).run();
				}
				public void onAnimationEnd( Animator animation ){
					if( p.containsKey( A_endListener ) ) p.getRunnable( A_endListener ).run();
				}

				public void onAnimationRepeat( Animator animation ){
					if( p.containsKey( A_repeatListener ) ) p.getRunnable( A_repeatListener ).run();
				}

				public void onAnimationStart( Animator animation ){
					if( p.containsKey( A_startListener ) ) p.getRunnable( A_startListener ).run();
				}
			} );
			ani().setListener( ( Animator.AnimatorListener ) p.get( A_listener ) );
		}
	}
	public Runnable runnable( Object $v ){
		if( $v instanceof Integer ){
			switch((Integer)$v){
			case RUN_invisible:
				if( !p.containsKey(RUN_invisible) ) p.put( RUN_invisible, new Runnable(){public void run(){v.setVisibility(View.INVISIBLE);}} );
				return p.getRunnable(RUN_invisible);
			case RUN_visible:
				if( !p.containsKey(RUN_visible) ) p.put( RUN_invisible, new Runnable(){public void run(){v.setVisibility(View.VISIBLE);}} );
				return p.getRunnable(RUN_visible);
			case RUN_visibleToggle:
				if( !p.containsKey(RUN_visibleToggle) ) p.put( RUN_visibleToggle, new Runnable(){public void run(){v.setVisibility( v.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE );}} );
				return p.getRunnable(RUN_visibleToggle);
			}
			return null;
		}
		return (Runnable)$v;
	}

	public Object g( Object $k ){return _view.get($k).g(this);}
	public Object s( Object $k, Object $v ){return _view.get($k).s( this, $v );}
	public Object S(Object...arg){return _s( this, p, _view, arg );}
	public Object A( Object... arg ){return _a( BS.this.handlerView, this, p, _dialog, arg );}
}
private Handler handlerView = new Handler(){
	@Override
	public void handleMessage( Message $msg ){
		((SG)$msg.obj).S(asyncArgs.get($msg.arg1));
		asyncArgs.remove($msg.arg1);
	}
};

static public abstract class AdapterCursor extends BaseAdapter{

	private bsCursor _c;
	private P _data = new P();
	private int[] _rowid;

	public AdapterCursor(){}
	public AdapterCursor( Cursor $c, int $rowid, Object...arg ){
		init( $c, $rowid );
		int i = 0, j = arg.length;
		while( i < j ) _data.put( (String)arg[i++], arg[i++] );
	}
	public AdapterCursor( Cursor $c, int $rowid, P arg ){
		init( $c, $rowid );
		_data.putAll(arg);
	}
	private void init( Cursor $c, int $rowid ){
		if( $c != null ){
			_c = new bsCursor($c);
			if( $rowid > -1 ){
				int i = 0, j = _c.getCount();
				_rowid = new int[j];
				while( i < j ){
					_c.moveToPosition( i );
					_rowid[i++] = _c.getInt( $rowid );
				}
			}
		}else _c = null;
	}

	public int getCount(){return _c == null ? 0 : _c.getCount();}
	public Object getItem( int $i ){return null;}
	public long getItemId( int $i ){return $i;}
	public View getView( int $idx, View $v, ViewGroup $g ){
		_c.moveToPosition( $idx );
		if( $v == null ) $v = view( _c, $idx, _rowid[$idx], _data );
		data( _c, $idx, $v, $g, _rowid[$idx], _data );
		return $v;
	}
	public boolean isEnabled( int $idx ){return true;}
	public void update( Cursor $c, int $rowid ){
		init( $c, $rowid );
		notifyDataSetChanged();
	}
	public int rowid( int $idx ){return _rowid[$idx];}
	public Object data( String $key ){return _data.get($key);}
	public void data( String $key, Object $val ){_data.put( $key, $val );}
	public abstract View view( bsCursor $c, int $idx, int $rowid, P $data );
	public abstract void data( bsCursor $c, int $idx, View $v, ViewGroup $g, int $rowid, P $data );
}

private interface N{View r();}
static private HashMap<Object, N> _viewNew = new HashMap<Object, N>();

static private int _viewID = 0;
static private HashMap<String, Integer> _viewIDs = new HashMap<String, Integer>();
static private P _viewParam = new P();
static private int[] _viewMargin = new int[]{0, 0, 0, 0};
static private int[] _viewLayout = new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
static private int[] _viewTouchAction = new int[]{E_down, E_up, E_move};
static private WebViewClient _viewWebViewClient;
static private WebChromeClient _viewWebChromeClient;
static private void textColor( View $v, int $c ){
	if( $v instanceof NumberPicker ) numberPickerTextColor( (NumberPicker)$v, $c );
	else if( $v instanceof TextView ) ((TextView)$v).setTextColor($c);
	else if( $v instanceof ViewGroup ){
		ViewGroup t0 = (ViewGroup)$v;
		for( int i = 0, j = t0.getChildCount() ; i < j ; i++ ) textColor( t0.getChildAt(i), $c );
	}
}
static private void numberPickerTextColor( NumberPicker $v, int $c ){
	for(int i = 0, j = $v.getChildCount() ; i < j; i++){
		View t0 = $v.getChildAt(i);
		if( t0 instanceof EditText ){
			try{
				Field t1 = $v.getClass().getDeclaredField("mSelectorWheelPaint");
				t1.setAccessible(true);
				((Paint)t1.get($v)).setColor($c);
				((EditText)t0).setTextColor($c);
				$v.invalidate();
			}catch(Exception e){}
		}
	}
}
private ArrayList<bsView> _viewPool = new ArrayList<bsView>();
private void viewPool( bsView $view ){_viewPool.add($view);}
private void contents( View $view ){_act.setContentView($view);}

public bsView View( View $view ){return View( $view, true );}
public bsView View( View $view, boolean $isCache ){return viewSet( $view, $isCache );}
public bsView View( int $id ){return View( $id, true );}
public bsView View( int $id, boolean $isCache ){
	return viewSet( Rtype( $id ) == _RtypeLayout ? Rlayout( $id ) : _act.findViewById( $id ), $isCache );
}
public bsView View( String $id ){return View( $id, true );}
public bsView View( String $id, boolean $isCache ){
	return _viewNew.containsKey($id) ? viewSet( _viewNew.get( $id ).r(), $isCache ) : _viewIDs.containsKey($id) ? viewSet( _act.findViewById( _viewIDs.get( $id ) ), $isCache ) : null;
}
public bsView View( String $id, View $finder ){return View( $id, $finder, true );}
public bsView View( String $id, View $finder, boolean $isCache ){
	return _viewNew.containsKey($id) ? viewSet( _viewNew.get( $id ).r(), $isCache ) : _viewIDs.containsKey($id) ? viewSet( $finder.findViewById( _viewIDs.get( $id ) ), $isCache ) : null;
}
public bsView View( int $id, View $finder ){return View( $id, $finder, true );}
public bsView View( int $id, View $finder, boolean $isCache ){
	return viewSet( Rtype( $id ) == _RtypeLayout ? Rlayout( $id ) : $finder.findViewById( $id ), $isCache );
}
public bsView View( String $id, Object $finder ){return View( $id, $finder, true );}
public bsView View( String $id, Object $finder, boolean $isCache ){
	if( $finder instanceof bsView ) return View( $id, ((bsView)$finder).v, $isCache );
	else if( $finder instanceof View ) return View( $id, (View)$finder, $isCache );
	else if( $finder instanceof Integer ) return View( $id, View((Integer)$finder), $isCache );
	else if( $finder instanceof String ) return View( $id, View((String)$finder), $isCache );
	return View($id);
}
public bsView View( int $id, Object $finder ){return View( $id, $finder, true );}
public bsView View( int $id, Object $finder, boolean $isCache ){
	if( $finder instanceof bsView ) return View( $id, ((bsView)$finder).v, $isCache );
	else if( $finder instanceof View ) return View( $id, (View)$finder, $isCache );
	else if( $finder instanceof Integer ) return View( $id, View((Integer)$finder), $isCache );
	else if( $finder instanceof String ) return View( $id, View((String)$finder), $isCache );
	return View( $id, $isCache );
}
//endregion

//endregion

//region Instance

//region R
static private final int _RtypeLayout = 1;
static private final int _RtypeString = 2;
static private HashMap<String, Integer> _Rtype;
{
	_Rtype = new HashMap<String, Integer>();
	_Rtype.put( "layout", _RtypeLayout);
	_Rtype.put( "string", _RtypeString);
}
private int Rtype( int $id ){
	String t0 = _res.getResourceTypeName($id);
	return _Rtype.containsKey(t0) ? _Rtype.get(t0) : 0;
}
public View Rlayout( String $id ){return Rlayout(_res.getIdentifier( $id, "layout", _packageName));}
public View Rlayout( int $id ){return _inflater.inflate( $id, null);}
public int Rid( String $id ){return _res.getIdentifier( $id, "id", _packageName);}
public Drawable Rdrawable( String $id ){return Rdrawable(_res.getIdentifier( $id, "drawable", _packageName ));}
public Drawable Rdrawable( int $id ){return _res.getDrawable($id);}
public String Rstring( String $id ){
	int i = _res.getIdentifier( $id, "string", _packageName);
	return i == 0 ? "" : Rstring(i);
}
public String Rstring( int $id ){return _res.getString($id);}
//endregion

//region instanceService
public void virtual( final float $width, final float $height ){
	_isVirtual = true;
	virtual_width = $width;
	virtual_height = $height;
	size();
}
private void size(){
	_display.getMetrics(_dm);
	width = _dm.widthPixels;
	height = _dm.heightPixels;
	virtual_widthRate = virtual_width / ((float) width);
	virtual_heightRate = virtual_height / ((float) height);
}

static public class bsIntent extends Intent{
	public P p = hash();
	public bsIntent(Context packageContext, Class<?> cls,Object...arg){
		super( packageContext, cls );
		int i = 0, j = arg.length;
		while( i < j ) p.put( arg[i++], arg[i++] );
	}
}

public void alarm( Class $class, int $id, int $hour, int $minute, String $repeat, String[] $data ){
	log( "alam add:" + $id + ":" + str( $hour ) + ":" + str( $minute ) + ":" + str( $data ) + ":" + $repeat );
	if( _am == null ) _am = (AlarmManager)_act.getSystemService(Context.ALARM_SERVICE);
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(System.currentTimeMillis());
	int day = c.get(Calendar.DAY_OF_MONTH);
	if( c.get( Calendar.HOUR_OF_DAY ) > $hour || ( c.get( Calendar.HOUR_OF_DAY ) == $hour && c.get( Calendar.MINUTE ) >= $minute ) ) day++;
	//noinspection ResourceType
	c.set( c.get(Calendar.YEAR), c.get( Calendar.MONTH ), day, $hour, $minute, 0 );
	long time = c.getTimeInMillis();
	Intent t0 = new Intent( _act, $class );
	t0.putExtra( "data", $data );
	PendingIntent pi = PendingIntent.getBroadcast( _act, $id, t0, 0 );
	if( $repeat.equals("0000000") ) _am.set( AlarmManager.RTC_WAKEUP, time, pi );
	else _am.setInexactRepeating( AlarmManager.RTC_WAKEUP, time, 24 * 60 * 60 * 1000, pi );
}
public void alarm( Class $class, int $id ){
	log( "alarm cancel:"+str($id));
	if( _am == null ) _am = (AlarmManager)_act.getSystemService(Context.ALARM_SERVICE);
	_am.cancel(PendingIntent.getBroadcast( _act, $id, new Intent( _act, $class ), 0 ));
}
public void toast( String $msg, int $length ){
	Toast.makeText( _act, $msg, $length ).show();
}
static public void toast( Context $context, String $msg, int $length ){
	Toast.makeText( $context, $msg, $length ).show();
}
public HashMap<String, Uri> alarmRingtonHash(){
	HashMap<String, Uri> r = new HashMap<String, Uri>();
	RingtoneManager ringtoneMgr = new RingtoneManager( _act );
	ringtoneMgr.setType( RingtoneManager.TYPE_ALARM );
	Cursor c = ringtoneMgr.getCursor();
	if( c != null && c.getCount() > 0 && c.moveToFirst() ){
		do{
			Uri uri = ringtoneMgr.getRingtoneUri( c.getPosition() );
			Ringtone ringtone = RingtoneManager.getRingtone( _act, uri );
			r.put( ringtone.getTitle( _act ), uri );
		}while( c.moveToNext() );
	}
	return r;
}
public ArrayList<String> alarmRingtonList(){
	ArrayList<String> r = new ArrayList<String>();
	RingtoneManager ringtoneMgr = new RingtoneManager( _act );
	ringtoneMgr.setType( RingtoneManager.TYPE_ALARM );
	Cursor c = ringtoneMgr.getCursor();
	if( c != null && c.getCount() > 0 && c.moveToFirst() ){
		do{
			Uri uri = ringtoneMgr.getRingtoneUri( c.getPosition() );
			Ringtone ringtone = RingtoneManager.getRingtone( _act, uri );
			r.add(ringtone.getTitle(_act));
			r.add(uri.toString());
		}while( c.moveToNext() );
	}
	return r;
}
public HashMap<String, String> musicHash(){
	HashMap<String, String> r = new HashMap<String, String>();
	Cursor c = _act.getContentResolver().query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			new String[]{
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.SIZE
			}, null, null, null );
	if( c != null && c.getCount() > 0 && c.moveToFirst() ){
		int t0 = c.getColumnIndex( MediaStore.Audio.Media.TITLE ), t1 = c.getColumnIndex( MediaStore.Audio.Media.DATA );
		do{
			r.put( c.getString(t0), c.getString(t1) );
		}while( c.moveToNext() );
	}
	if( c != null ) c.close();
	return r;
}
public ArrayList<String> musicList(){
	ArrayList<String> r = new ArrayList<String>();
	Cursor c = _act.getContentResolver().query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			new String[]{
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.SIZE
			}, null, null, null );
	if( c != null && c.getCount() > 0 && c.moveToFirst() ){
		int t0 = c.getColumnIndex( MediaStore.Audio.Media.TITLE ), t1 = c.getColumnIndex( MediaStore.Audio.Media.DATA );
		do{
			r.add(c.getString(t0));
			r.add(c.getString(t1));
		}while( c.moveToNext() );
	}
	if( c != null ) c.close();
	return r;
}

public boolean wifiActive(){
	if( _cm == null ) _cm = (ConnectivityManager)_act.getSystemService(Context.CONNECTIVITY_SERVICE);
	if( _wifi == null ) _wifi = _cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	return _wifi.isConnected();
}

//endregion

//region fields
public Boolean isAlive;
public int width, height = 0;
public float virtual_width, virtual_height, virtual_widthRate, virtual_heightRate = 0;

private Activity _act;
private Window _win;
private Display _display;
private String _packageName;
private Resources _res;
private AlarmManager _am;
private ConnectivityManager _cm;
private NetworkInfo _wifi;
private FragmentManager _fm;
private LayoutInflater _inflater;
private Boolean _screenOn;
private DisplayMetrics _dm = new DisplayMetrics();
private Boolean _isVirtual = false;
//endregion

//region liftCycle
public void onResume(){
	log( "bs.onResume" );
	if( _screenOn ) _win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
}
public void onPause(){
	log( "bs.onPause" );
	if( _screenOn ) _win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
}
public void onDestroy(){
	log( "bs.onDestroy" );
	_act.finish();
	isAlive = Boolean.FALSE;
}
public void onStop(){
	log( "bs.onStop" );
}
public void onActivityResult( int $request, int $result, Intent $data ){
	log( "bs.onActivityResult" );
    /*
    bsIntent.onActivityResult( $request, $result, $data );
    */
}
public void onPostCreate( Bundle $savedInstanceState ){
	log( "bs.onPostCreate" );
}
//endregion

public BS(Activity $activity, Bundle $savedInstanceState){
	log( "bs.onCreate" );
	_act = $activity;
	_win = _act.getWindow();
	_display = _act.getWindowManager().getDefaultDisplay();
	_packageName = _act.getPackageName();
	_res = _act.getResources();
	_inflater = _act.getLayoutInflater();
	isAlive = Boolean.TRUE;
	_screenOn = Rstring("bs_screenOn").equals("true") ? Boolean.TRUE : Boolean.FALSE;
	if( Rstring("bs_fullScreen").equals("true") ) _win.setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
	if( Rstring("bs_titleBar").equals("false") ) _act.requestWindowFeature(Window.FEATURE_NO_TITLE);
	String t0 = Rstring("bs_orientation");
	if( !t0.equals("") && !t0.equals("false") ){
		int i = -1;
		if( t0.equals("landscape") ) i = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		else if( t0.equals("landscape_sensor") ) i = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
		else if( t0.equals("landscape_reverse") ) i = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
		else if( t0.equals("portrait") ) i = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		else if( t0.equals("portrait_sensor") ) i = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
		else if( t0.equals("portrait_reverse") ) i = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
		if( i > -1 ) _act.setRequestedOrientation(i);
	}
	t0 = Rstring("bs_webviewControl");
	if( !t0.equals("") && !t0.equals("false") ) {
		//임의의 웹뷰를 만들고
		//컨텐츠에는 추가하지 않으며
		//url에 해당되는 자바스크립트를 로딩한다
	}
	size();
}

//endregion

}