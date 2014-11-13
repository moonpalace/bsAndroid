package com.bsplugin.android;

import android.widget.CompoundButton;

abstract public class Echecked extends E<CompoundButton> implements CompoundButton.OnCheckedChangeListener{
static private boolean none;

public Echecked( Object... arg ){
	super( arg );
	if( !none ){
		none = true;
		E.none( BS.EVENT.checked, new Echecked(){
			public void run( CompoundButton $v, P $p ){
			}
		} );
	}
}

public void onCheckedChanged( CompoundButton $v, boolean $b ){
	p.put( BS.VIEW.checked, $b );
	run( $v, p );
}
}
