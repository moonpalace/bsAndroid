package com.bsplugin.android;

import android.media.MediaPlayer;

abstract public class EendedMedia extends E<MediaPlayer> implements MediaPlayer.OnCompletionListener{
static private boolean none;

public EendedMedia( Object... arg ){
	super( arg );
	if( !none ){
		none = true;
		E.none( BS.EVENT.endedMedia, new EendedMedia(){
			public void run( MediaPlayer $m, P $p ){
			}
		} );
	}
}

public void onCompletion( MediaPlayer $m ){
	run( $m, p );
}

}
