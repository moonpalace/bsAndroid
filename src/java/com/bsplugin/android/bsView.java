package com.bsplugin.android;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class bsView extends S<bsView> implements View.OnTouchListener{

static private enum C{touch, ani, anilistener}

private interface N{
	View run( Activity $act );
}

static private HashMap<View, bsView> _pool = new HashMap<View, bsView>();

static bsView pool( Activity $act, View $v, boolean $isCache ){
	if( $isCache ){
		if( !_pool.containsKey( $v ) ) _pool.put( $v, new bsView( $act, $v ) );
		return _pool.get( $v );
	}else return new bsView( $act, $v );
}

static bsView pool( Activity $act, String $v, boolean $isCache ){
	return _viewIDs.containsKey( $v ) ? bsView.pool( $act, $act.findViewById( _viewIDs.get( $v ) ), $isCache ) : null;
}

static bsView pool( Activity $act, View $finder, String $v, boolean $isCache ){
	return _viewIDs.containsKey( $v ) ? bsView.pool( $act, $finder.findViewById( _viewIDs.get( $v ) ), $isCache ) : null;
}

static bsView pool( Activity $act, BS.VIEW $v, boolean $isCache ){
	if( _news.containsKey( $v ) ) return pool( $act, _news.get( $v ).run( $act ), $isCache );
	else return null;
}

static void pool( bsView $v ){
	_pool.remove( $v.v );
	P.pool( $v.p );
}

public float[][] motionPoint = null;
public MotionEvent motionEvent = null;
public BS.EVENT motionType;

View v;
Activity act;

private bsView( Activity $act, View $v ){
	super( null, attr, null );
	Object tag = $v.getTag();
	if( tag instanceof P ){
		p = (P) tag;
		$v.setTag( null );
	}
	v = $v;
	act = $act;
}

public void d(){
	ViewGroup t0 = (ViewGroup) v.getParent();
	if( t0 != null ) t0.removeView( v );
	pool( this );
}

private void touch( BS.EVENT $k, Runnable $v ){
	if( motionPoint == null ){
		motionPoint = new float[5][2];
		v.setOnTouchListener( this );
	}
	p.put( $k, $v );
}

public boolean onTouch( View $v, MotionEvent $e ){
	motionEvent = $e;
	BS.EVENT t0 = motionType = _touchAction[$e.getAction()];
	if( p.containsKey( t0 ) ){
		int j = $e.getPointerCount();
		if( 0 < j && j < 5 ){
			for( int i = 0 ; i < j ; i++ ){
				int id = $e.getPointerId( i );
				motionPoint[id][0] = $e.getX( i );
				motionPoint[id][1] = $e.getY( i );
			}
			p.RUNNABLE( t0 ).run();
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

private ViewPropertyAnimator ani( Boolean $isStart ){
	ViewPropertyAnimator t0 = ani();
	p.remove( C.ani );
	p.remove( BS.ANI.delay );
	return t0;
}

private ViewPropertyAnimator ani(){
	if( !p.containsKey( C.ani ) ) p.put( C.ani, v.animate() );
	return (ViewPropertyAnimator) p.get( C.ani );
}

private void aniListener( Object $k, Runnable $v ){
	p.put( $k, $v );
	if( !p.containsKey( C.anilistener ) ){
		p.put( C.anilistener, new Animator.AnimatorListener(){
			public void onAnimationCancel( Animator animation ){
				if( p.containsKey( BS.EVENT.canceledAni ) )
					p.RUNNABLE( BS.EVENT.canceledAni ).run();
			}

			public void onAnimationEnd( Animator animation ){
				if( p.containsKey( BS.EVENT.endedAni ) ) p.RUNNABLE( BS.EVENT.endedAni ).run();
			}

			public void onAnimationRepeat( Animator animation ){
				if( p.containsKey( BS.EVENT.repeatedAni ) )
					p.RUNNABLE( BS.EVENT.repeatedAni ).run();
			}

			public void onAnimationStart( Animator animation ){
				if( p.containsKey( BS.EVENT.startedAni ) ) p.RUNNABLE( BS.EVENT.startedAni ).run();
			}
		} );
		ani().setListener( (Animator.AnimatorListener) p.get( C.anilistener ) );
	}
}

public Runnable runnable( Object $v ){
	if( $v instanceof Runnable ) return (Runnable) $v;
	if( $v == BS.RUN.invisible ){
		if( !p.containsKey( BS.RUN.invisible ) ) p.put( BS.RUN.invisible, new Runnable(){
			public void run(){
				v.setVisibility( View.INVISIBLE );
			}
		} );
	}else if( $v == BS.RUN.visible ){
		if( !p.containsKey( BS.RUN.visible ) ) p.put( BS.RUN.invisible, new Runnable(){
			public void run(){
				v.setVisibility( View.VISIBLE );
			}
		} );
	}else if( $v == BS.RUN.visibleToggle ){
		if( !p.containsKey( BS.RUN.visibleToggle ) ) p.put( BS.RUN.visibleToggle, new Runnable(){
			public void run(){
				v.setVisibility( v.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE );
			}
		} );
	}
	return p.RUNNABLE( $v );
}

static private int _viewID = 0;
static private HashMap<String, Integer> _viewIDs = new HashMap<String, Integer>();
static private WebViewClient webViewClient = new WebViewClient(){
	public void onPageStarted( WebView $v, String $url, Bitmap $favicon ){
		BS.log( "WebviewPageStarted:" + $url );
	}

	public void onLoadResource( WebView $v, String $url ){
		super.onLoadResource( $v, $url );
	}

	public boolean shouldOverrideUrlLoading( WebView $v, String $url ){
		$v.loadUrl( $url );
		return true;
	}
};

static public WebChromeClient webChromeClient( final BS $bs, Object... $arg ){
	return new bsWebChromeClient( $bs, $arg );
}

static private void textColor( View $v, int $c ){
	if( $v instanceof NumberPicker ) numberPickerTextColor( (NumberPicker) $v, $c );
	else if( $v instanceof TextView ) ( (TextView) $v ).setTextColor( $c );
	else if( $v instanceof ViewGroup ){
		ViewGroup t0 = (ViewGroup) $v;
		for( int i = 0, j = t0.getChildCount() ; i < j ; i++ ) textColor( t0.getChildAt( i ), $c );
	}
}

static private void numberPickerTextColor( NumberPicker $v, int $c ){
	for( int i = 0, j = $v.getChildCount() ; i < j ; i++ ){
		View t0 = $v.getChildAt( i );
		if( t0 instanceof EditText ){
			try{
				Field t1 = $v.getClass().getDeclaredField( "mSelectorWheelPaint" );
				t1.setAccessible( true );
				( (Paint) t1.get( $v ) ).setColor( $c );
				( (EditText) t0 ).setTextColor( $c );
				$v.invalidate();
			}catch( Exception e ){
			}
		}
	}
}

static private abstract class AT implements Iattr<bsView>{
	Object key;

	private AT( Object $key ){
		key = $key;
	}

	public Object g( final bsView $b ){
		return $b.p.containsKey( key ) ? $b.p.get( key ) : false;
	}

	public Object s( final bsView $b, final Object $v ){
		Object t0 = _s( $b, $v );
		$b.p.put( key, t0 );
		return t0;
	}

	protected Object _s( final bsView $b, final Object $v ){
		return $v;
	}
}

static private int[] _margin = new int[]{0, 0, 0, 0};
static int[] layout = new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
static private BS.EVENT[] _touchAction = new BS.EVENT[]{BS.EVENT.down, BS.EVENT.up, BS.EVENT.move};
static private HashMap<Object, N> _news = new HashMap<Object, N>();
static private HashMap<Object, Iattr<bsView>> attr = new HashMap<Object, Iattr<bsView>>();

{
	_news.put( BS.VIEW.webView, new N(){
		public View run( Activity $act ){
			return new WebView( $act );
		}
	} );
	_news.put( BS.VIEW.frameLayout, new N(){
		public View run( Activity $act ){
			return new FrameLayout( $act );
		}
	} );
	//region Attribute

	attr.put( BS.VIEW.clickable, new AT( BS.VIEW.clickable ){
		public Object _s( final bsView $b, final Object $v ){
			$b.v.setClickable( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.focusable, new AT( BS.VIEW.focusable ){
		public Object s( final bsView $b, final Object $v ){
			$b.v.setFocusable( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.longClickable, new AT( BS.VIEW.longClickable ){
		public Object _s( final bsView $b, final Object $v ){
			$b.v.setLongClickable( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.focusableInTouchMode, new AT( BS.VIEW.focusableInTouchMode ){
		public Object _s( final bsView $b, final Object $v ){
			$b.v.setFocusableInTouchMode( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.enabled, new AT( BS.VIEW.enabled ){
		public Object _s( final bsView $b, final Object $v ){
			$b.v.setEnabled( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.pressed, new AT( BS.VIEW.pressed ){
		public Object _s( final bsView $b, final Object $v ){
			$b.v.setPressed( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.textColor, new AT( BS.VIEW.pressed ){
		public Object _s( final bsView $b, final Object $v ){
			int c = $v instanceof Integer ? (Integer) $v : BS.str2color( (String) $v );
			textColor( $b.v, c );
			return c;
		}
	} );
	attr.put( BS.VIEW.view, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v;
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof String ){
				String v = (String) $v;
				$b.v = _news.containsKey( v ) ? _news.get( v ).run( $b.act ) : _viewIDs.containsKey( v ) ? $b.act.findViewById( _viewIDs.get( v ) ) : null;
			}else if( $v instanceof Integer ){
				int v = (Integer) $v;
				$b.v = BS.Rtype( $b.act, v ) == BS.R.layout ? BS.Rlayout( $b.act, v ) : $b.act.findViewById( v );
			}else if( $v instanceof View ) $b.v = (View) $v;
			return $v;
		}
	} );
	attr.put( BS.VIEW.self, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b;
		}

		public Object s( final bsView $b, final Object $v ){
			return $b;
		}
	} );
	attr.put( BS.VIEW.span, new AT( BS.VIEW.span ){
		public Object _s( final bsView $b, final Object $v ){
			TableRow.LayoutParams params = (TableRow.LayoutParams) $b.v.getLayoutParams();
			params.span = (Integer) $v;
			$b.v.setLayoutParams( params );
			return $v;
		}
	} );
	attr.put( BS.VIEW.x, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getX();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setX( (Float) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.y, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getY();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setY( (Float) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.scaleX, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getScaleX();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setScaleX( (Float) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.scaleY, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getScaleY();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setScaleY( (Float) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.background, new AT( BS.VIEW.background ){
		@SuppressLint( "NewApi" )
		public Object _s( final bsView $b, final Object $v ){
			if( $v instanceof String ) $b.v.setBackgroundColor( BS.str2color( (String) $v ) );
			else if( $v instanceof Drawable ) $b.v.setBackground( (Drawable) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.margin, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.containsKey( BS.VIEW.margin ) ? $b.p.get( BS.VIEW.margin ) : _margin;
		}

		public Object s( final bsView $b, final Object $v ){
			int[] v = (int[]) $v;
			ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) $b.v.getLayoutParams();
			if( param == null )
				param = new ViewGroup.MarginLayoutParams( ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT );
			param.setMargins( v[0], v[1], v[2], v[3] );
			$b.p.put( BS.VIEW.margin, v );
			$b.v.setLayoutParams( param );
			return $v;
		}
	} );
	attr.put( BS.VIEW.alpha, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getAlpha();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setAlpha( (Float) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.layout, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.containsKey( BS.VIEW.layout ) ? $b.p.get( BS.VIEW.layout ) : layout;
		}

		public Object s( final bsView $b, final Object $v ){
			int[] v = (int[]) $v;
			$b.p.put( BS.VIEW.layout, v );
			ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) $b.v.getLayoutParams();
			if( param == null ) param = new ViewGroup.MarginLayoutParams( v[0], v[1] );
			else{
				param.width = v[0];
				param.height = v[1];
			}
			if( $b.p.containsKey( BS.VIEW.margin ) ){
				v = (int[]) $b.p.get( BS.VIEW.margin );
				param.setMargins( v[0], v[1], v[2], v[3] );
			}
			$b.v.setLayoutParams( param );
			return $v;
		}
	} );
	attr.put( BS.VIEW.visible, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getVisibility() == View.VISIBLE;
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setVisibility( (Boolean) $v ? View.VISIBLE : View.INVISIBLE );
			return $v;
		}
	} );
	attr.put( BS.VIEW.id, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getId();
		}

		public Object s( final bsView $b, final Object $v ){
			String v = (String) $v;
			if( _viewIDs.containsKey( v ) ) return $v;
			int id = _viewID++;
			_viewIDs.put( v, id );
			$b.v.setId( id );
			return $v;
		}
	} );
	attr.put( BS.VIEW.tag, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getTag();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setTag( $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.param, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.clone();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.p.putAll( (HashMap) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.focus, new AT( BS.VIEW.focus ){
		public Object _s( final bsView $b, final Object $v ){
			Boolean v = (Boolean) $v;
			$b.v.setFocusable( v );
			if( v ) $b.v.requestFocus();
			$b.v.setFocusableInTouchMode( v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.parent, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getParent();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v == BS.VIEW.root ) $b.act.setContentView( $b.v );
			else if( $v instanceof String ){
				String v = (String) $v;
				( (ViewGroup) bsView.pool( $b.act, v, true ).v ).addView( $b.v );
			}else if( $v instanceof Integer ){
				int v = (Integer) $v;
				( (ViewGroup) ( BS.Rtype( $b.act, v ) == BS.R.layout ? BS.Rlayout( $b.act, v ) : $b.act.findViewById( v ) ) ).addView( $b.v );
			}else if( $v instanceof View ) ( (ViewGroup) $v ).addView( $b.v );
			else if( $v == null ){
				ViewGroup p = (ViewGroup) $b.v.getParent();
				if( p != null ) p.removeView( $b.v );
			}
			return $v;
		}
	} );
	attr.put( BS.VIEW.children, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			ViewGroup v = (ViewGroup) $b.v;
			int j = v.getChildCount();
			View[] t0 = new View[j];
			for( int i = 0 ; i < j ; i++ ) t0[i] = v.getChildAt( i );
			return t0;
		}

		public Object s( final bsView $b, final Object $v ){
			return g( $b );
		}
	} );
	attr.put( BS.VIEW.child, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return null;
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Integer ) return ( (ViewGroup) $b.v ).getChildAt( (Integer) $v );
			else if( $v instanceof View ) ( (ViewGroup) $b.v ).addView( (View) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.next, new Iattr<bsView>(){
				public Object g( final bsView $b ){
					return null;
				}

				public Object s( final bsView $b, final Object $v ){
					( (Runnable) $v ).run();
					return $v;
				}
			}
	);
	attr.put( BS.VIEW.adapter, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (AdapterView) $b.v ).getAdapter();
		}

		public Object s( final bsView $b, final Object $v ){
			( (AdapterView) $b.v ).setAdapter( (Adapter) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.text, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (TextView) $b.v ).getText();
		}

		public Object s( final bsView $b, final Object $v ){
			( (TextView) $b.v ).setText( $v instanceof String ? (String) $v : BS.Rstring( $b.act, (Integer) $v ) );
			return $v;
		}
	} );
	attr.put( BS.VIEW.textScaleX, new Iattr<bsView>(){
				public Object g( final bsView $b ){
					return ( (TextView) $b.v ).getTextScaleX();
				}

				public Object s( final bsView $b, final Object $v ){
					( (TextView) $b.v ).setTextScaleX( (Float) $v );
					return $v;
				}
			}
	);
	attr.put( BS.VIEW.lineSpacing, new AT( BS.VIEW.lineSpacing ){
		public Object _s( final bsView $b, final Object $v ){
			( (TextView) $b.v ).setLineSpacing( (Float) $v, 1 );
			return $v;
		}
	} );
	attr.put( BS.VIEW.image, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (ImageView) $b.v ).getDrawable();
		}

		public Object s( final bsView $b, final Object $v ){
			ImageView v = (ImageView) $b.v;
			if( $v instanceof Integer ) v.setImageResource( (Integer) $v );
			else if( $v instanceof String )
				v.setImageDrawable( BS.Rdrawable( $b.act, (String) $v ) );
			else if( $v instanceof Drawable ) v.setImageDrawable( (Drawable) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.checked, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (CompoundButton) $b.v ).isChecked();
		}

		public Object s( final bsView $b, final Object $v ){
			( (CompoundButton) $b.v ).setChecked( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.layer, new AT( BS.VIEW.layer ){
		public Object _s( final bsView $b, final Object $v ){
			$b.v.setLayerType( (Integer) $v, null );
			return $v;
		}
	} );

	//endregion
	//region Animation

	attr.put( BS.ANI.alpha, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getAlpha();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().alpha( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setAlpha( v[0] );
				$b.ani().alpha( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.rotation, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getRotation();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().rotation( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setRotation( v[0] );
				$b.ani().rotation( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.rotationX, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getRotationX();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().rotationX( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setRotationX( v[0] );
				$b.ani().rotationX( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.rotationY, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getRotationY();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().rotationY( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setRotationY( v[0] );
				$b.ani().rotationY( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.scaleX, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getScaleX();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().scaleX( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setScaleX( v[0] );
				$b.ani().scaleX( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.scaleY, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getScaleY();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().scaleY( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setScaleY( v[0] );
				$b.ani().scaleY( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.translationX, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getTranslationX();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().translationX( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setTranslationX( v[0] );
				$b.ani().translationX( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.translationY, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getTranslationY();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().translationY( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setTranslationY( v[0] );
				$b.ani().translationY( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.x, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getX();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().x( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setX( v[0] );
				$b.ani().x( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.y, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.v.getY();
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Float ) $b.ani().y( (Float) $v );
			else{
				float[] v = (float[]) $v;
				$b.v.setY( v[0] );
				$b.ani().y( v[1] );
			}
			return $v;
		}
	} );
	attr.put( BS.ANI.duration, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.ani().getDuration();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.ani().setDuration( (Long) $v );
			return $v;
		}
	} );
	attr.put( BS.ANI.delay, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.ani().getStartDelay();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.ani().setStartDelay( (Long) $v );
			return $v;
		}
	} );
	attr.put( BS.ANI.ease, new Iattr<bsView>(){
		@SuppressLint( "NewApi" )
		public Object g( final bsView $b ){
			return $b.ani().getInterpolator();
		}

		public Object s( final bsView $b, final Object $v ){
			$b.ani().setInterpolator( (TimeInterpolator) $v );
			return $v;
		}
	} );
	attr.put( BS.ANI.cancel, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			$b.ani().cancel();
			return null;
		}

		public Object s( final bsView $b, final Object $v ){
			$b.ani().cancel();
			return $v;
		}
	} );
	attr.put( BS.ANI.start, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return s( $b, 1000L );
		}

		public Object s( final bsView $b, final Object $v ){
			if( $v instanceof Long[] ){
				Long[] v = (Long[]) $v;
				$b.ani().setDuration( v[0] );
				$b.ani( true ).setStartDelay( v[1] );
			}else if( $v instanceof Long ) $b.ani().setDuration( (Long) $v );
			$b.ani( true ).start();
			return $v;
		}
	} );

	//endregion
	//region Widgets

	attr.put( BS.VIEW.webviewClient, new AT( BS.VIEW.webviewClient ){
		public Object _s( final bsView $b, final Object $v ){
			WebViewClient v = null;
			if( $v instanceof Integer ){
				if( (Integer) $v == BS.NONE ) v = webViewClient;
			}else v = (WebViewClient) $v;
			( (WebView) $b.v ).setWebViewClient( v );
			return v;
		}
	} );
	attr.put( BS.VIEW.webviewChrome, new AT( BS.VIEW.webviewChrome ){
		public Object _s( final bsView $b, final Object $v ){
			WebChromeClient v = null;
			if( $v instanceof Integer ){
				if( (Integer) $v == BS.NONE ) v = webChromeClient( BS.pool( $b.act, null ),
						BS.WEBCHROME.alert, true, BS.WEBCHROME.confirm, true, BS.WEBCHROME.console, true,
						BS.WEBCHROME.fullScreen, true, BS.WEBCHROME.popup, true
				);
			}else if( $v instanceof WebChromeClient ) v = (WebChromeClient) $v;
			if( v != null ) ( (WebView) $b.v ).setWebChromeClient( v );
			return v;
		}
	} );
	attr.put( BS.VIEW.webviewLocalstorage, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (WebView) $b.v ).getSettings().getDomStorageEnabled();
		}

		public Object s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).getSettings().setDomStorageEnabled( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewUrl, new AT( BS.VIEW.webviewUrl ){
		public Object _s( final bsView $b, final Object $v ){
			WebView v = (WebView) $b.v;
			if( $b.p.containsKey( BS.VIEW.webviewHeader ) )
				v.loadUrl( (String) $v, (Map<String, String>) $b.p.get( BS.VIEW.webviewHeader ) );
			else v.loadUrl( (String) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewJs, new AT( BS.VIEW.webviewJs ){
		public Object _s( final bsView $b, final Object $v ){
			WebView v = (WebView) $b.v;
			v.loadUrl( "javascript:" + $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewApiKey, new AT( BS.VIEW.webviewApiKey ){
		public Object _s( final bsView $b, final Object $v ){
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewApi, new AT( BS.VIEW.webviewApi ){
		public Object _s( final bsView $b, final Object $v ){
			if( $v == null ){
				if( $b.p.containsKey( BS.VIEW.webviewApi ) )
					( (WebView) $b.v ).removeJavascriptInterface( $b.p.STRING( BS.VIEW.webviewApi ) );
			}else{
				if( !$b.p.containsKey( BS.VIEW.webviewApiKey ) )
					$b.p.put( BS.VIEW.webviewApiKey, "BS" );
				( (WebView) $b.v ).addJavascriptInterface( $v, $b.p.STRING( BS.VIEW.webviewApiKey ) );
			}
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewIsJS, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (WebView) $b.v ).getSettings().getJavaScriptEnabled();
		}

		public Object s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).getSettings().setJavaScriptEnabled( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewFile, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (WebView) $b.v ).getSettings().getAllowFileAccess();
		}

		public Object s( final bsView $b, final Object $v ){
			WebSettings s = ( (WebView) $b.v ).getSettings();
			Boolean v = (Boolean) $v;
			s.setAllowFileAccess( v );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
				s.setAllowUniversalAccessFromFileURLs( v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewZoom, new AT( BS.VIEW.webviewZoom ){
		public Object _s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).getSettings().setSupportZoom( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewCache, new AT( BS.VIEW.webviewCache ){
		public Object _s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).getSettings().setAppCacheEnabled( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewCachePath, new AT( BS.VIEW.webviewCachePath ){
		public Object _s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).getSettings().setAppCachePath( (String) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewAgent, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return System.getProperty( "http.agent" );
		}

		public Object s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).getSettings().setUserAgentString( (String) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewEncoding, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (WebView) $b.v ).getSettings().getDefaultTextEncodingName();
		}

		public Object s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).getSettings().setDefaultTextEncodingName( (String) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewHeader, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.VIEW.webviewHeader );
		}

		public Object s( final bsView $b, final Object $v ){
			if( !$b.p.containsKey( BS.VIEW.webviewHeader ) )
				$b.p.put( BS.VIEW.webviewHeader, new HashMap<String, String>() );
			( (HashMap<String, String>) $b.p.get( BS.VIEW.webviewHeader ) ).putAll( (HashMap<String, String>) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewCanGoBack, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (WebView) $b.v ).canGoBack();
		}

		public Object s( final bsView $b, final Object $v ){
			return ( (WebView) $b.v ).canGoBack();
		}
	} );
	attr.put( BS.VIEW.webviewBack, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return this.s( $b, null );
		}

		public Object s( final bsView $b, final Object $v ){
			WebView v = (WebView) $b.v;
			if( $b.p.containsKey( BS.VIEW.webviewChrome ) ){
				Object t0 = $b.p.get( BS.VIEW.webviewChrome );
				if( t0 instanceof bsWebChromeClient ){
					bsWebChromeClient wv = (bsWebChromeClient) t0;
					if( wv.isFullScreen ){
						wv.onHideCustomView();
						return true;
					}else{
						if( wv.popup != null && wv.popup.size() > 0 ){
							int i = wv.popup.size();
							WebView t1 = wv.popup.get( i - 1 );
							if( t1.canGoBack() ) t1.goBack();
							else wv.onCloseWindow( t1 );
							return true;
						}
					}
				}
			}
			if( v.canGoBack() ){
				v.goBack();
				return true;
			}else return false;
		}
	} );
	attr.put( BS.VIEW.webviewMultipleWindow, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.VIEW.webviewMultipleWindow );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.p.put( BS.VIEW.webviewMultipleWindow, $v );
			( (WebView) $b.v ).getSettings().setSupportMultipleWindows( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.webviewReload, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			( (WebView) $b.v ).reload();
			return null;
		}

		public Object s( final bsView $b, final Object $v ){
			( (WebView) $b.v ).reload();
			return $v;
		}
	} );
	attr.put( BS.VIEW.timePicker24, new AT( BS.VIEW.timePicker24 ){
		public Object _s( final bsView $b, final Object $v ){
			( (TimePicker) $b.v ).setIs24HourView( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.timePickerHour, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (TimePicker) $b.v ).getCurrentHour();
		}

		public Object s( final bsView $b, final Object $v ){
			( (TimePicker) $b.v ).setCurrentHour( (Integer) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.timePickerMinute, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (TimePicker) $b.v ).getCurrentMinute();
		}

		public Object s( final bsView $b, final Object $v ){
			( (TimePicker) $b.v ).setCurrentMinute( (Integer) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.progressBarMax, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (ProgressBar) $b.v ).getMax();
		}

		public Object s( final bsView $b, final Object $v ){
			( (ProgressBar) $b.v ).setMax( (Integer) $v );
			return $v;
		}
	} );
	attr.put( BS.VIEW.progressBarProgress, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return ( (ProgressBar) $b.v ).getProgress();
		}

		public Object s( final bsView $b, final Object $v ){
			( (ProgressBar) $b.v ).setProgress( (Integer) $v );
			return $v;
		}
	} );

	//endregion
	//region event

	attr.put( BS.EVENT.down, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.down );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.touch( BS.EVENT.down, $b.runnable( $v ) );
			return $v;
		}
	} );
	attr.put( BS.EVENT.up, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.up );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.touch( BS.EVENT.up, $b.runnable( $v ) );
			return $v;
		}
	} );
	attr.put( BS.EVENT.move, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.move );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.touch( BS.EVENT.move, $b.runnable( $v ) );
			return $v;
		}
	} );
	attr.put( BS.EVENT.checked, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.checked );
		}

		public Object s( final bsView $b, final Object $v ){
			CompoundButton.OnCheckedChangeListener v = $v == null ? null : (CompoundButton.OnCheckedChangeListener) E.listener( $v, BS.EVENT.checked );
			( (CompoundButton) $b.v ).setOnCheckedChangeListener( v );
			$b.p.put( BS.EVENT.checked, v );
			return v;
		}
	} );
	attr.put( BS.EVENT.itemClick, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.itemClick );
		}

		public Object s( final bsView $b, final Object $v ){
			AdapterView.OnItemClickListener v = $v == null ? null : (AdapterView.OnItemClickListener) E.listener( $v, BS.EVENT.itemClick );
			( (AdapterView) $b.v ).setOnItemClickListener( v );
			$b.p.put( BS.EVENT.itemClick, v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.timeChange, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.timeChange );
		}

		public Object s( final bsView $b, final Object $v ){
			( (TimePicker) $b.v ).setOnTimeChangedListener( (TimePicker.OnTimeChangedListener) $v );
			$b.p.put( BS.EVENT.timeChange, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.itemLongClick, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.itemLongClick );
		}

		public Object s( final bsView $b, final Object $v ){
			( (AdapterView) $b.v ).setOnItemLongClickListener( (AdapterView.OnItemLongClickListener) $v );
			$b.p.put( BS.EVENT.itemLongClick, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.itemSelected, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.itemSelected );
		}

		public Object s( final bsView $b, final Object $v ){
			( (AdapterView) $b.v ).setOnItemSelectedListener( (AdapterView.OnItemSelectedListener) $v );
			$b.p.put( BS.EVENT.itemSelected, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.click, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.click );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnClickListener( (View.OnClickListener) $v );
			$b.p.put( BS.EVENT.click, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.drag, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.drag );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnDragListener( (View.OnDragListener) $v );
			$b.p.put( BS.EVENT.drag, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.focus, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.focus );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnFocusChangeListener( (View.OnFocusChangeListener) $v );
			$b.p.put( BS.EVENT.focus, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.genericMotion, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.genericMotion );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnGenericMotionListener( (View.OnGenericMotionListener) $v );
			$b.p.put( BS.EVENT.genericMotion, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.hover, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.hover );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnHoverListener( (View.OnHoverListener) $v );
			$b.p.put( BS.EVENT.hover, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.key, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.key );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnKeyListener( (View.OnKeyListener) $v );
			$b.p.put( BS.EVENT.key, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.longClick, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.longClick );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnLongClickListener( (View.OnLongClickListener) $v );
			$b.p.put( BS.EVENT.longClick, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.touch, new Iattr<bsView>(){
		public Object g( final bsView $b ){
			return $b.p.get( BS.EVENT.touch );
		}

		public Object s( final bsView $b, final Object $v ){
			$b.v.setOnTouchListener( (View.OnTouchListener) $v );
			$b.p.put( BS.EVENT.touch, $v );
			return $v;
		}
	} );
	attr.put( BS.EVENT.canceledAni, new AT( BS.EVENT.canceledAni ){
		public Object _s( final bsView $b, final Object $v ){
			Runnable v = $b.runnable( $v );
			$b.aniListener( BS.EVENT.canceledAni, v );
			return v;
		}
	} );
	attr.put( BS.EVENT.repeatedAni, new AT( BS.EVENT.repeatedAni ){
		public Object s( final bsView $b, final Object $v ){
			Runnable v = $b.runnable( $v );
			$b.aniListener( BS.EVENT.repeatedAni, v );
			return v;
		}
	} );
	attr.put( BS.EVENT.endedAni, new AT( BS.EVENT.endedAni ){
		public Object _s( final bsView $b, final Object $v ){
			Runnable v = $b.runnable( $v );
			$b.aniListener( BS.EVENT.endedAni, v );
			return v;
		}
	} );
	attr.put( BS.EVENT.startedAni, new AT( BS.EVENT.startedAni ){
		public Object s( final bsView $b, final Object $v ){
			Runnable v = $b.runnable( $v );
			$b.aniListener( BS.EVENT.startedAni, v );
			return v;
		}
	} );
	if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ){
		attr.put( BS.EVENT.updatedAni, new AT( BS.EVENT.updatedAni ){
			@SuppressLint( "NewApi" )
			public Object _s( final bsView $b, final Object $v ){
				$b.ani().setUpdateListener( (ValueAnimator.AnimatorUpdateListener) $v );
				return $v;
			}
		} );
	}

	//endregion
}

}