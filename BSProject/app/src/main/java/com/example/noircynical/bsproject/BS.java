package com.example.noircynical.bsproject;

import android.animation.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class BS{
    static private ExecutorService _workers = Executors.newFixedThreadPool(8);
    {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    }

    //region Util
        //region CoreUtil
    static private LinkedList<HashMap<String, Object>> _hashPool = new LinkedList<HashMap<String, Object>>();
    static private HashMap<String, Object> hash(){return _hashPool.size() > 0 ? _hashPool.pop() : new HashMap<String, Object>();}
    static private void hashPool( HashMap<String, Object> $v ){$v.clear();_hashPool.add($v);}
    static public String log( String $msg ){Log.i( "BS", $msg );return $msg;}
        //endregion
        //region String casting
    final static private String _strSharp = "#";
    final static private String _strComma = ",";

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
    //endregion

    //region SQLite

    public class Sqlite extends SQLiteOpenHelper{
        private String d;
        private SQLiteDatabase w;
        private SQLiteDatabase r;
        public Sqlite( Context $context, String $database, int $ver ){
            super( $context, $database, null, $ver );
            d = $database;
            w = this.getWritableDatabase();
            r = this.getReadableDatabase();
        }
        @Override
        public void onCreate( SQLiteDatabase db ){}
        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ){}
        public Sqlite table( String $name, String...arg ){
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
            w.execSQL( arg.length == 0 ? $query : tmpl( $query, arg ) );
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
    }
        //region Sqlite
    private HashMap<String, Sqlite> _sqlites = new HashMap<String, Sqlite>();
    public Sqlite Sqlite( String $db ){
        if( !_sqlites.containsKey($db) ) _sqlites.put( $db, new Sqlite( _act, $db, 1 ) );
        return _sqlites.get($db);
    }
        //endregion

    //endregion

    //region View
    static public abstract class AdapterCursor extends BaseAdapter{
        private Cursor _c;
        public AdapterCursor( Cursor $c ){_c = $c;}
        public int getCount(){return _c == null ? 0 : _c.getCount();}
        public Object getItem( int $i ){return null;}
        public long getItemId( int $i ){return $i;}
        public View getView( int $i, View $v, ViewGroup $g ){
            if( $v == null ) $v = view();
            _c.moveToPosition($i);
            data( _c, $i, $v, $g );
            return $v;
        }

        public void update( Cursor $c ){
            _c = $c;
            notifyDataSetChanged();
        }
        public abstract View view();
        public abstract void data( Cursor $c, int $i, View $v, ViewGroup $g );
    }

    private abstract class S{
        public void s( final bsView $v0, final Object $v1 ){}
        public Object g( final bsView $v0 ){return null;}
    }
    private interface N{View r();}
    public class bsView{
        public float[][] motionPoint = null;
        public MotionEvent motionEvent = null;
        public String motionType;
        private View v;
        private HashMap<String, Object> p;

        private bsView init( View $v ){
            if( $v == null ){
                _viewCache.remove(v);
                hashPool(p);
                p = null;
            }else{
                Object tag = $v.getTag();
                if( tag == _viewParam ){
                    p = (HashMap<String, Object>)tag;
                    $v.setTag(null);
                }else p = hash();
            }
            v = $v;
            return this;
        }
        private void touch( String $k, Runnable $v ){
            if( !p.containsKey(E_bsTouch) ){
                motionPoint = new float[5][2];
                View.OnTouchListener t0 = new View.OnTouchListener(){
                    public boolean onTouch( View $v, MotionEvent $e ){
                        motionEvent = $e;
                        String t0 = motionType = _viewTouchAction[$e.getAction()];
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
                                    int id = $e.getPointerId(i);
                                    motionPoint[id][0] = $e.getX(i) * w;
                                    motionPoint[id][1] = $e.getY(i) * h;
                                }
                                ((Runnable)p.get(t0)).run();
                                return true;
                            }
                        }
                        try {
                            Thread.sleep(15);
                        }catch(Exception e){
                            BS.log(e.toString());
                        }
                        return true;
                    }
                };
                p.put( E_bsTouch, t0 );
                v.setOnTouchListener(t0);
            }
            p.put( $k, $v );
        }
        private ViewPropertyAnimator ani( Boolean $isStart ){
            ViewPropertyAnimator t0 = ani();
            p.remove(A_animation);
            p.remove(A_delay);
            return t0;
        }
        private ViewPropertyAnimator ani(){
            if (!p.containsKey(A_animation)) p.put(A_animation, v.animate());
            return (ViewPropertyAnimator) p.get(A_animation);
        }
        private void aniListener( String $k, Runnable $v ){
            p.put( $k, $v );
            if( !p.containsKey(A_listener) ){
                p.put( A_listener,  new Animator.AnimatorListener(){
                    public void	onAnimationCancel(Animator animation){if( p.containsKey(A_cancelListener) ) ((Runnable)p.get(A_cancelListener)).run();}
                    public void onAnimationEnd(Animator animation){if( p.containsKey(A_endListener) ) ((Runnable)p.get(A_endListener)).run();}
                    public void onAnimationRepeat(Animator animation){if( p.containsKey(A_repeatListener) ) ((Runnable)p.get(A_repeatListener)).run();}
                    public void onAnimationStart(Animator animation){if( p.containsKey(A_startListener) ) ((Runnable)p.get(A_startListener)).run();}
                } );
                ani().setListener((Animator.AnimatorListener)p.get(A_listener));
            }
        }
        public Object A( Object... arg ){
            if( arg.length == 1 && _viewS.containsKey(arg[0]) ) return _viewS.get(arg[0]).g(this);
            {
                _viewArgs.put(_viewArgID, arg);
                BS bs = BS.this;
                Message r = Message.obtain(bs.handler);
                r.arg1 = _viewArgID++;
                r.obj = this;
                bs.handler.sendMessage(r);
            }
            return null;
        }
        public Object S( Object... arg ){
            if( arg.length == 1 && _viewS.containsKey(arg[0]) ) return _viewS.get(arg[0]).g(this);
            {
                if( arg[0] == null ){
                    ( (ViewGroup) v.getParent() ).removeView(v);
                    init(null);
                    viewPool(this);
                }else{
                    int i = 0, j = arg.length;
                    while( i < j ){
                        String k = (String)arg[i++];
                        if( _viewS.containsKey(k) ) _viewS.get(k).s( this, arg[i++] );
                        else i++;
                    }
                }
            }
            return null;
        }
    }
        //region View
    private Handler handler = new Handler(){
        @Override
        public void handleMessage( Message $msg ){
            bsView v = (bsView)$msg.obj;
            Object[] arg = _viewArgs.get($msg.arg1);
            _viewArgs.remove($msg.arg1);
            if( arg[0] == null ){
                ( (ViewGroup) v.v.getParent() ).removeView(v.v);
                v.init(null);
                viewPool(v);
            }else{
                int i = 0, j = arg.length;
                while( i < j ){
                    String k = (String)arg[i++];
                    if( _viewS.containsKey(k) ) _viewS.get(k).s(v, arg[i++]);
                    else i++;
                }
            }
        }
    };
    private HashMap<View, bsView> _viewCache = new HashMap<View, bsView>();
    private LinkedList<bsView> _viewPool = new LinkedList<bsView>();
    private void viewPool( bsView $view ){_viewPool.push($view);}
    private bsView view( View $v ){
        if( _viewCache.containsKey($v) ) return _viewCache.get($v);
        else{
            bsView t0 = _viewPool.size() > 0 ? _viewPool.pop() : new bsView();
            _viewCache.put( $v, t0.init($v) );
            return t0;
        }
    }
    public bsView View( int $id ){return view( Rtype($id) == _RtypeLayout ? Rlayout($id) : _act.findViewById($id) );}
    public bsView View( View $view ){return view($view);}
    public bsView View( String $id ) {
        if( _viewNew.containsKey($id) ) return view(_viewNew.get($id).r());
        return _viewIDs.containsKey($id) ? view(_act.findViewById(_viewIDs.get($id))) : null;
    }
    private void contents( View $view ){_act.setContentView($view);}
        //endregion

        //region Const Event
    static private final String E_bsTouch = "E_bsTouch";
    static private final String E_bsKey = "E_bsKey";
    static public final String E_click = "E_click";
    static public final String E_drag = "E_drag";
    static public final String E_focusChange = "E_focusChange";
    static public final String E_genericMotion = "E_genericMotion";
    static public final String E_hover = "E_hover";
    static public final String E_key = "E_key";
    static public final String E_longClick = "E_longClick";
    static public final String E_touch = "E_touch";
    static public final String E_down = "E_down";
    static public final String E_up = "E_up";
    static public final String E_move = "E_move";
        //endregion
        //region Const View
    static public final String V_ROOT = "V_ROOT";
    static public final String V_span = "V_span";
    static public final String V_scaleX = "V_scaleX";
    static public final String V_scaleY = "V_scaleY";
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
    static public final String V_adapterView = "V_adapterView";

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
    static public final String A_end = "A_end";
    static public final String A_started = "A_started";
    static public final String A_canceled = "A_canceled";
    static public final String A_repeated = "A_repeated";
    //action
    static public final String A_cancel = "A_cancel";
    static public final String A_start = "A_start";
        //endregion
        //region Const WebView
    static public final String WV_url = "WV_url";
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
        //endregion
        //region Const TextView
    static public final String TV_textScaleX = "TV_textScaleX";
    static public final String TV_lineSpacing = "TV_lineSpacing";
    static public final String TV_setText = "TV_setText";
    static public final String TV_setTextColor = "TV_setTextColor";
        //endregion
        //region _view Fields
        static private int _viewID = 0, _viewArgID = 0;
    static private HashMap<String, Integer> _viewIDs = new HashMap<String, Integer>();
    static private HashMap<Integer, Object[]> _viewArgs = new HashMap<Integer, Object[]>();
    static private HashMap<String, Object> _viewParam = new HashMap<String, Object>();
    static private int[] _viewMargin = new int[]{0, 0, 0, 0};
    static private int[] _viewLayout = new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
    static private String[] _viewTouchAction = new String[]{E_down, E_up, E_move};
    static private HashMap<String, S> _viewS;
    static private HashMap<String, N> _viewNew;
    static private WebViewClient _viewWebViewClient = new WebViewClient(){
        public void onLoadResource( WebView $v, String $url ){super.onLoadResource( $v, $url );}
        public boolean shouldOverrideUrlLoading( WebView $v, String $url ){
            $v.loadUrl($url);
            return true;
        }
        public void onPageStarted( WebView $v, String $url, Bitmap $favicon ){log( "WebviewPageStarted:" + $url );}
    };
    static private WebChromeClient _viewWebChromeClient = new WebChromeClient(){
        public boolean onJsAlert( WebView $v, String $url, String $msg, final android.webkit.JsResult $result ){
            new AlertDialog.Builder($v.getContext()).setTitle("AlertDialog").setMessage($msg)
                    .setPositiveButton( android.R.string.ok,new AlertDialog.OnClickListener(){public void onClick(DialogInterface dialog, int which){$result.confirm();}})
                    .setCancelable(false).create().show();
            return true;
        };
    };
    //endregion
        //region _view Init
    {
        _viewNew = new HashMap<String, N>();
        _viewS = new HashMap<String, S>();

            //region CreateView
        _viewNew.put( "Webview", new N(){public View r(){return new WebView( BS.this._act );}});
            //endregion

            //region Event
        _viewS.put( E_click, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_click);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnClickListener((View.OnClickListener)$v);$b.p.put( E_click, $v );}
        });
        _viewS.put( E_drag, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_drag);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnDragListener((View.OnDragListener)$v);$b.p.put( E_drag, $v );}
        });
        _viewS.put( E_focusChange, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_focusChange);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnFocusChangeListener((View.OnFocusChangeListener)$v);$b.p.put( E_focusChange, $v );}
        });
        _viewS.put( E_genericMotion, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_genericMotion);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnGenericMotionListener((View.OnGenericMotionListener)$v);$b.p.put( E_genericMotion, $v );}
        });
        _viewS.put( E_hover, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_hover);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnHoverListener((View.OnHoverListener)$v);$b.p.put( E_hover, $v );}
        });
        _viewS.put( E_key, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_key);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnKeyListener((View.OnKeyListener)$v);$b.p.put( E_key, $v );}
        });
        _viewS.put( E_longClick, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_longClick);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnLongClickListener((View.OnLongClickListener)$v);$b.p.put( E_longClick, $v );}
        });
        _viewS.put( E_touch, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_touch);}
            public void s( final bsView $b, final Object $v ){$b.v.setOnTouchListener((View.OnTouchListener)$v);$b.p.put( E_touch, $v );}
        });
        _viewS.put( E_down, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_down);}
            public void s( final bsView $b, final Object $v ){$b.touch( E_down, (Runnable)$v );}
        });
        _viewS.put( E_up, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_up);}
            public void s( final bsView $b, final Object $v ){$b.touch( E_up, (Runnable)$v );}
        });
        _viewS.put( E_move, new S(){
            public Object g( final bsView $b ){return $b.p.get(E_move);}
            public void s( final bsView $b, final Object $v ){$b.touch( E_move, (Runnable)$v );}
        });
            //endregion
            //region Attribute
        _viewS.put( V_span, new S(){
            public void s( final bsView $b, final Object $v ){
                TableRow.LayoutParams params = (TableRow.LayoutParams) $b.v.getLayoutParams();
                params.span = (Integer)$v;
            }
        });
        _viewS.put( V_scaleX, new S(){
            public Object g( final bsView $b ){return $b.v.getScaleX();}
            public void s( final bsView $b, final Object $v ){$b.v.setScaleX((Float)$v);}
        });
        _viewS.put( V_scaleY, new S(){
            public Object g( final bsView $b ){return $b.v.getScaleY();}
            public void s( final bsView $b, final Object $v ){$b.v.setScaleY((Float)$v);}
        });
        _viewS.put( V_background, new S(){
            public Object g( final bsView $b ){return $b.p.get("background");}
            @SuppressLint("NewApi")
            public void s( final bsView $b, final Object $v ){
                $b.p.put("background",$v);
                if( $v instanceof String ) $b.v.setBackgroundColor(BS.str2color((String) $v));
                else if( $v instanceof Drawable ) $b.v.setBackground((Drawable)$v);
            }
        });
        _viewS.put( V_margin, new S(){
            public Object g( final bsView $b ){return $b.p.containsKey("margin") ? $b.p.get("margin") : _viewMargin;}
            public void s( final bsView $b, final Object $v ){
                int[] v = (int[])$v;
                ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) $b.v.getLayoutParams();
                param.setMargins( v[0], v[1], v[2], v[3] );
                $b.p.put( "margin", v );
                $b.v.setLayoutParams(param);
            }
        });
        _viewS.put( V_alpha, new S(){
            public Object g( final bsView $b ){return $b.v.getAlpha();}
            public void s( final bsView $b, final Object $v ){$b.v.setAlpha((Float)$v);}
        } );
        _viewS.put( V_layout, new S(){
            public Object g( final bsView $b ){return $b.p.containsKey("layout") ? $b.p.get("layout") : _viewLayout;}
            public void s( final bsView $b, final Object $v ){
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
            }
        } );
        _viewS.put( V_visible, new S(){
            public Object g( final bsView $b ){return $b.v.getVisibility();}
            public void s( final bsView $b, final Object $v ){$b.v.setVisibility( (Boolean)$v ? View.VISIBLE : View.INVISIBLE );}
        });
        _viewS.put( V_id, new S(){
            public Object g( final bsView $b ){return $b.v.getId();}
            public void s( final bsView $b, final Object $v ){
                String v = (String)$v;
                if( _viewIDs.containsKey(v) ){
                    log( "Existed ID ::" + v );
                    return;
                }
                int id = _viewID;
                _viewIDs.put( v, id );
                $b.v.setId(id);
                _viewID++;
            }
        });
        _viewS.put( V_tag, new S(){
            public Object g( final bsView $b ){return $b.v.getTag();}
            public void s( final bsView $b, final Object $v ){$b.v.setTag($v);}
        });
        _viewS.put( V_param, new S(){
            public Object g( final bsView $b ){return $b.p.clone();}
            public void s( final bsView $b, final Object $v ){$b.p.putAll((HashMap<String, Object>)$v);}
        });
            //endregion
            //region Action
        _viewS.put( V_focus, new S(){
            public void s( final bsView $b, final Object $v ){
                $b.v.setFocusable(true);
                $b.v.requestFocus();
                $b.v.setFocusableInTouchMode(true);
            }
        } );
        _viewS.put( V_parent, new S(){
            public Object g( final bsView $b ){return $b.v.getParent();}
            public void s( final bsView $b, final Object $v ){
                if( $v instanceof String ){
                    String v = (String)$v;
                    if( v.equals(V_ROOT) ) BS.this.contents($b.v);
                    else ((ViewGroup)BS.this.View((String)$v).v).addView($b.v);
                }else if( $v instanceof Integer ) ((ViewGroup)BS.this.View((Integer)$v).v).addView($b.v);
                else if( $v instanceof View ) ((ViewGroup)BS.this.View((View)$v).v).addView($b.v);
            }
        } );
        _viewS.put( V_next, new S(){public void s( final bsView $b, final Object $v ){((Runnable)$v).run();}});
        _viewS.put( V_adapterView, new S() {
            public void s( final bsView $b, final Object $v ){
                $b.p.put( V_adapterView, $v );
            }
        });
        _viewS.put( V_adapter, new S(){
            public Object g( final bsView $b ){return ((AdapterView)$b.v).getAdapter();}
            public void s( final bsView $b, final Object $v ){
                AdapterView v = (AdapterView)$b.v;
                if( $v instanceof Cursor ){
                    Cursor c = (Cursor)$v;
                    String[] t0 = c.getColumnNames();
                    int[] t1 = new int[t0.length];
                    for( int i = 0, j = t1.length ; i < j ; i++ ) t1[i] = Rid(t0[i]);
                    v.setAdapter( new SimpleCursorAdapter( _act, (Integer)$b.p.get(V_adapterView), c, t0, t1, 1 ) );
                }else v.setAdapter((Adapter)$v);
            }
        } );
            //endregion
            //region Animation
            //region attribute
        _viewS.put( A_alpha, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().alpha((Float) $v);
            else{
                float[] v = (float[])$v;
                $b.ani().alphaBy(v[0]).alpha(v[1]);
            }
        }});
        _viewS.put( A_rotation, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().rotation((Float) $v);
            else{
                float[] v = (float[])$v;
                $b.ani().rotationBy(v[0]).rotation(v[1]);
            }
        }});
        _viewS.put( A_rotationX, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().rotationX((Float) $v);
            else{
                float[] v = (float[])$v;
                $b.ani().rotationXBy(v[0]).rotationX(v[1]);
            }
        }});
        _viewS.put( A_rotationY, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().rotationY((Float) $v);
            else{
                float[] v = (float[])$v;
                $b.ani().rotationYBy(v[0]).rotationY(v[1]);
            }
        }});
        _viewS.put( A_scaleX, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().scaleX((Float) $v);
            else{
                float[] v = (float[])$v;
                $b.ani().scaleXBy(v[0]).scaleX(v[1]);
            }
        }});
        _viewS.put( A_scaleY, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().scaleY((Float)$v);
            else{
                float[] v = (float[])$v;
                $b.ani().scaleYBy(v[0]).scaleY(v[1]);
            }
        }});
        _viewS.put( A_translationX, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().translationX((Float)$v);
            else{
                float[] v = (float[])$v;
                $b.ani().translationXBy(v[0]).translationX(v[1]);
            }
        }});
        _viewS.put( A_translationY, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().translationY((Float)$v);
            else{
                float[] v = (float[])$v;
                $b.ani().translationYBy(v[0]).translationY(v[1]);
            }
        }});
        _viewS.put( A_x, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float ) $b.ani().x((Float)$v);
            else{
                float[] v = (float[])$v;
                $b.ani().xBy(v[0]).x(v[1]);
            }
        }});
        _viewS.put( A_y, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Float[] ){
                float[] v = (float[])$v;
                $b.ani().yBy(v[0]).y(v[1]);
            }else $b.ani().y((Float)$v);
        }});
        _viewS.put( A_duration, new S(){
            public Object g( final bsView $b ){return $b.ani().getDuration();}
            public void s( final bsView $b, final Object $v ){$b.ani().setDuration((Long)$v);}
        });

        _viewS.put( A_delay, new S(){
            public Object g( final bsView $b ){return $b.ani().getStartDelay();}
            public void s( final bsView $b, final Object $v ){$b.ani().setStartDelay((Long)$v);}
        });
        _viewS.put( A_ease, new S(){
            @SuppressLint("NewApi")
            public Object g( final bsView $b ){return $b.ani().getInterpolator();}
            public void s( final bsView $b, final Object $v ){$b.ani().setInterpolator((TimeInterpolator) $v);}
        });
            //endregion
            //region event
        _viewS.put( A_canceled, new S(){public void s( final bsView $b, final Object $v ){$b.aniListener( A_cancelListener, (Runnable)$v );}});
        _viewS.put( A_repeated, new S(){public void s( final bsView $b, final Object $v ){$b.aniListener(A_repeatListener, (Runnable) $v);}});
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ){
            _viewS.put( A_end, new S(){public void s( final bsView $b, final Object $v ){$b.aniListener( A_endListener, (Runnable)$v );}});
            _viewS.put( A_started, new S(){public void s( final bsView $b, final Object $v ){$b.aniListener( A_startListener, (Runnable)$v );}});
        }else{
            _viewS.put(A_update, new S() {
                @SuppressLint("NewApi")
                public void s( final bsView $b, final Object $v ){$b.ani().setUpdateListener((ValueAnimator.AnimatorUpdateListener) $v);}
            });
            _viewS.put(A_end, new S() {
                @SuppressLint("NewApi")
                public void s( final bsView $b, final Object $v ){$b.ani().withEndAction((Runnable) $v);}
            });
            _viewS.put(A_started, new S() {
                @SuppressLint("NewApi")
                public void s( final bsView $b, final Object $v ){$b.ani().withStartAction((Runnable) $v);}
            });
        }
            //endregion
            //region action
        _viewS.put( A_cancel, new S(){public void s( final bsView $b, final Object $v ){$b.ani().cancel();}});
        _viewS.put( A_start, new S(){public void s( final bsView $b, final Object $v ){
            if( $v instanceof Long[] ){
                Long[] v = (Long[])$v;
                $b.ani().setDuration(v[0]);
                $b.ani(true).setStartDelay(v[1]);
            }else if( $v instanceof Long ) $b.ani().setDuration((Long)$v);
            $b.ani(true).start();
        }});
            //endregion
            //endregion
            //region Webview
        _viewS.put( WV_url, new S(){public void s( final bsView $b, final Object $v ){
            WebView v = (WebView)$b.v;
            if( $b.p.containsKey(WV_header) ) v.loadUrl( (String)$v, (Map<String, String>)$b.p.get(WV_header) );
            else v.loadUrl((String)$v);
        }});
        _viewS.put( WV_apiKey, new S(){public void s( final bsView $b, final Object $v ){$b.p.put("apiKey", $v);}});
        _viewS.put( WV_api, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).addJavascriptInterface($v, $b.p.containsKey("apiKey") ? (String) $b.p.get("apiKey") : "Bs");}});
        _viewS.put( WV_isJS, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setJavaScriptEnabled((Boolean) $v);}});
        _viewS.put( WV_file, new S() {
            public void s(final bsView $b, final Object $v) {
                WebSettings s = ((WebView) $b.v).getSettings();
                Boolean v = (Boolean)$v;
                s.setAllowFileAccess(v);
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) s.setAllowUniversalAccessFromFileURLs(v);
            }
        });
        _viewS.put( WV_zoom, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setSupportZoom((Boolean) $v);}});
        _viewS.put( WV_client, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).setWebViewClient($v == null ? _viewWebViewClient : (WebViewClient) $v);}});
        _viewS.put( WV_chrome, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).setWebChromeClient($v == null ? _viewWebChromeClient : (WebChromeClient) $v);}});
        _viewS.put( WV_layer, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).setLayerType( (Integer)$v, null );}});
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ){
            _viewS.put( WV_priority, new S(){public void s( final bsView $b, final Object $v ){((WebView) $b.v).getSettings().setRenderPriority((WebSettings.RenderPriority) $v);}});
            _viewS.put( WV_cacheSize, new S(){public void s( final bsView $b, final Object $v ){((WebView) $b.v).getSettings().setAppCacheMaxSize((Long) $v);}});
        }
        _viewS.put( WV_cache, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setAppCacheEnabled((Boolean) $v);}});
        _viewS.put( WV_cachePath, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setAppCachePath((String)$v);}});
        _viewS.put(WV_agent, new S() {
            public Object g( final bsView $b ){return System.getProperty("http.agent");}
            public void s( final bsView $b, final Object $v ){((WebView) $b.v).getSettings().setUserAgentString((String) $v);}
        });
        _viewS.put( WV_encoding, new S(){public void s( final bsView $b, final Object $v ){((WebView)$b.v).getSettings().setDefaultTextEncodingName((String) $v);}});
        _viewS.put( WV_header, new S(){
            public Object g( final bsView $b ){return $b.p.get(WV_header);}
            public void s( final bsView $b, final Object $v ){
                if( !$b.p.containsKey(WV_header) ) $b.p.put( WV_header, new HashMap<String, String>() );
                ((HashMap<String, String>)$b.p.get(WV_header)).putAll((HashMap<String, String>)$v);
            }
        });
            //endregion
            //region TextView
        _viewS.put( TV_textScaleX, new S(){public void s( final bsView $b, final Object $v ){((TextView)$b.v).setTextScaleX((Float)$v);}});
        _viewS.put( TV_lineSpacing, new S(){public void s( final bsView $b, final Object $v ){((TextView)$b.v).setLineSpacing( (Float)$v, 1 );}});
        _viewS.put( TV_setText, new S(){public void s( final bsView $b, final Object $v){ ((TextView)$b.v).setText((String)$v); }});
        _viewS.put( TV_setTextColor, new S(){public void s( final bsView $b, final Object $v){ ((TextView)$b.v).setTextColor(Integer.parseInt((String)$v)); }});
            //endregion
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
        /*
        res = exBitmap._resource = act.getResources();
        exCore.bundle( $savedInstanceState );
        bsIntent.onCreate();
        bsIO.onCreate();
        bsIni.onCreate();
        bsWidget.onCreate();
        bsParser.onCreate();

        start( $start );
        */
        size();
    }
    //endregion
}