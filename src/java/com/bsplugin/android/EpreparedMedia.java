package com.bsplugin.android;

import android.media.MediaPlayer;

abstract public class EpreparedMedia extends E<MediaPlayer> implements MediaPlayer.OnPreparedListener{
static private boolean none;

public EpreparedMedia( Object... arg ){
	super( arg );
	if( !none ){
		none = true;
		E.none( BS.EVENT.preparedMedia, new EpreparedMedia(){

			@Override
			public void run( MediaPlayer $v, P $p ){

			}

		} );
	}
}

public void onPrepared( MediaPlayer $m ){
	run( $m, p );
}
}