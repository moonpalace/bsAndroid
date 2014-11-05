package com.bsplugin.android;

import java.util.HashMap;

abstract class E<T>{

static private HashMap<Object, E> _none = new HashMap<Object, E>();

static void none( Object $k, E $v ){
	_none.put( $k, $v );
}

static public Object listener( Object $v, Object $none ){
	if( $v instanceof Integer ){
		if( (Integer) $v == BS.NONE ) return _none.get( $none );
	}
	return $v;
}

abstract public void run( T $v, P $p );

protected P p;

protected E( Object[] arg ){
	p = P.pool();
	int i = 0, j = arg.length;
	if( j > 0 ) while( i < j ) p.put( arg[i++], arg[i++] );
}

}
