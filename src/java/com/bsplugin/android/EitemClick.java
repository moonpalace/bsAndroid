package com.bsplugin.android;

import android.view.View;
import android.widget.AdapterView;

abstract public class EitemClick extends E<View> implements AdapterView.OnItemClickListener{
static private boolean none;

public EitemClick( Object... arg ){
	super( arg );
	if( !none ){
		none = true;
		E.none( BS.EVENT.itemClick, new EitemClick(){
			public void run( View $v, P $p ){
			}
		} );
	}
}

public void onItemClick( AdapterView $adapterView, View $v, int $idx, long l ){
	if( $adapterView.getAdapter() instanceof AdapterCursor )
		p.put( BS.VIEW.rowid, ( (AdapterCursor) $adapterView.getAdapter() ).rowid( $idx ) );
	p.put( BS.VIEW.parent, $adapterView );
	p.put( BS.VIEW.index, $idx );
	run( $v, p );
}
}