package com.bsplugin.android;

import android.content.DialogInterface;

abstract public class EclickDialog extends E<DialogInterface> implements DialogInterface.OnClickListener{
static private boolean none;

public EclickDialog( Object... arg ){
	super( arg );
	if( !none ){
		none = true;
		E.none( BS.EVENT.clickDialog, new EclickDialog(){
			public void run( DialogInterface $v, P $p ){
			}
		} );
	}
}

public void onClick( DialogInterface d, int i ){
	p.put( BS.DIALOG.index, i );
	run( d, p );
}


}
