package com.bsplugin.android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.HashMap;

public class bsMedia extends MediaPlayer implements IS{

static private HashMap<Object, Iattr<bsMedia>> attr = new HashMap<Object, Iattr<bsMedia>>();
static private HashMap<String, bsMedia> _pool = new HashMap<String, bsMedia>();

static public bsMedia pool( String $key, Object... arg ){
	if( !_pool.containsKey( $key ) ) _pool.put( $key, new bsMedia( arg ) );
	return _pool.get( $key );
}

private S<bsMedia> s;
public P p;

private bsMedia( Object... arg ){
	super();
	s = new S<bsMedia>( this, attr, arg );
	p = s.p;
}

public void d(){
}

public Object s( Object... arg ){
	return s.s( arg );
}

public Object a( BS bs, Object... arg ){
	return s.a( bs, arg );
}

static{
	attr.put( BS.EVENT.endedMedia, new Iattr<bsMedia>(){
		public Object g( final bsMedia $m ){
			return $m.p.get( BS.EVENT.endedMedia );
		}

		public Object s( final bsMedia $m, Object $v ){
			if( !$m.p.containsKey( BS.EVENT.endedMedia ) ) $m.p.put( BS.EVENT.endedMedia, $v );
			$m.setOnCompletionListener( (MediaPlayer.OnCompletionListener) $m.p.get( BS.EVENT.endedMedia ) );
			return null;
		}
	} );
	attr.put( BS.EVENT.preparedMedia, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.setOnPreparedListener( (MediaPlayer.OnPreparedListener) $v );
			return $v;
		}
	} );

	attr.put( BS.MEDIA.start, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			if( $v != null ) $m.seekTo( (Integer) $v );
			$m.start();
			return $v;
		}
	} );
	attr.put( BS.MEDIA.prepare, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			try{
				$m.prepare();
			}catch( Exception e ){
				BS.log( "Mplayer.PREPARE:" + e.toString() );
			}
			return $v;
		}
	} );
	attr.put( BS.MEDIA.pause, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.pause();
			return $m;
		}
	} );
	attr.put( BS.MEDIA.src, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return $m.p.get( BS.MEDIA.src );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.p.put( BS.MEDIA.src, $v );
			try{
				$m.setDataSource( (String) $v );
			}catch( Exception e ){
				BS.log( "Mplayer.SRC:" + e.toString() );
			}
			return null;
		}
	} );
	attr.put( BS.MEDIA.uri, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return $m.p.get( BS.MEDIA.uri );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.p.put( BS.MEDIA.uri, $v );
			try{
				if( $m.p.containsKey( BS.ACTIVITY ) )
					$m.setDataSource( (Activity) $m.p.get( BS.ACTIVITY ), (Uri) $v );
				else BS.log( "Mplayer has no activity" );
			}catch( Exception e ){
				BS.log( "Mplayer.SRC:" + e.toString() );
			}
			return null;
		}
	} );
	attr.put( BS.MEDIA.current, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return $m.getCurrentPosition();
		}

		public Object s( bsMedia $m, Object $v ){
			$m.seekTo( (Integer) $v );
			return $v;
		}
	} );
	attr.put( BS.MEDIA.duration, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return $m.getDuration();
		}

		public Object s( bsMedia $m, Object $v ){
			return $m.getDuration();
		}
	} );
	attr.put( BS.MEDIA.wake, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return $m.getDuration();
		}

		public Object s( bsMedia $m, Object $v ){
			$m.setScreenOnWhilePlaying( (Boolean) $v );
			return $v;
		}
	} );
	attr.put( BS.MEDIA.streamType, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.setAudioStreamType( (Integer) $v );
			return $v;
		}
	} );
	attr.put( BS.MEDIA.prepareAsync, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.prepareAsync();
			return $v;
		}
	} );
	attr.put( BS.MEDIA.reset, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.reset();
			return $v;
		}
	} );
	attr.put( BS.MEDIA.stop, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.stop();
			return $v;
		}
	} );
	attr.put( BS.MEDIA.loop, new Iattr<bsMedia>(){
		public Object g( bsMedia $m ){
			return s( $m, null );
		}

		public Object s( bsMedia $m, Object $v ){
			$m.setLooping( (Boolean) $v );
			return $v;
		}
	} );
}
}
