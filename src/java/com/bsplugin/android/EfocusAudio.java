package com.bsplugin.android;

import android.media.AudioManager;

abstract public class EfocusAudio extends E implements AudioManager.OnAudioFocusChangeListener{
static private boolean none;

public EfocusAudio( Object... arg ){
	super( arg );
	if( !none ){
		none = true;
		E.none( BS.EVENT.focusAudio, new EfocusAudio(){
			public void run( Object $v, P $p ){

			}
		} );
	}
}

@Override
public void onAudioFocusChange( int $focusChange ){
	p.put( BS.AUDIO.focusAudio, $focusChange );
	run( null, p );
}

}
