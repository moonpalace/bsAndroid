package com.bsplugin.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;

import java.util.HashMap;

@SuppressLint("ValidFragment")
public class bsDialog extends DialogFragment implements Runnable, IS{

static private HashMap<Object, Iattr<bsDialog>> attr = new HashMap<Object, Iattr<bsDialog>>();
static private HashMap<String, bsDialog> _pool = new HashMap<String, bsDialog>();

static public bsDialog pool( Activity $act, String $key, Object... $arg ){
	if( !_pool.containsKey( $key ) )
		_pool.put( $key, new bsDialog( $key, new AlertDialog.Builder( $act ), $act.getFragmentManager(), $arg.length == 1 && $arg[0] instanceof Object[] ? (Object[]) $arg[0] : $arg ){
		} );
	return _pool.get( $key );
}

static public bsDialog pool( BS $bs, String $key, Object... $arg ){
	Activity act = $bs.activity();
	if( !_pool.containsKey( $key ) )
		_pool.put( $key, new bsDialog( $key, new AlertDialog.Builder( act ), act.getFragmentManager(), $arg.length == 1 && $arg[0] instanceof Object[] ? (Object[]) $arg[0] : $arg ){
		} );
	return _pool.get( $key );
}

private String _key;
private AlertDialog.Builder builder;
private FragmentManager fm;
private S<bsDialog> s;
public P p;

private bsDialog( String $key, AlertDialog.Builder $b, FragmentManager $f, Object[] arg ){
	_key = $key;
	builder = $b;
	fm = $f;
	s = new S<bsDialog>( this, attr, arg );
	p = s.p;
}

public Dialog onCreateDialog( Bundle $saved ){
	return builder.create();
}

public void show(){
	show( fm, _key );
}

public void run(){
	show( fm, _key );
}

public void d(){
	P.pool( p );
	_pool.remove( _key );
}

public Object s( Object... arg ){
	return s.s( arg );
}

public Object a( BS $bs, Object... arg ){
	return s.a( $bs, arg );
}

static{
	attr.put( BS.DIALOG.title, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			if( $v instanceof String ) $d.builder.setTitle( (String) $v );
			else if( $v instanceof Integer ) $d.builder.setTitle( (Integer) $v );
			else if( $v instanceof View ) $d.builder.setCustomTitle( (View) $v );
			$d.p.put( BS.DIALOG.title, $v );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.title );
		}
	} );
	attr.put( BS.DIALOG.adapter, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( BS.DIALOG.adapter, $v );
			if( $d.p.containsKey( BS.EVENT.click ) )
				$d.builder.setAdapter( (ListAdapter) $v, (DialogInterface.OnClickListener) $d.p.get( BS.EVENT.click ) );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.adapter );
		}
	} );
	attr.put( BS.DIALOG.message, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( BS.DIALOG.message, $v );
			$d.builder.setMessage( (String) $v );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.message );
		}
	} );
	attr.put( BS.DIALOG.cursor, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			Cursor c = (Cursor) $v;
			$d.p.put( BS.DIALOG.cursor, c );
			if( $d.p.containsKey( BS.DIALOG.cursorColume ) && $d.p.containsKey( BS.EVENT.click ) )
				$d.builder.setCursor( c, (DialogInterface.OnClickListener) $d.p.get( BS.EVENT.click ), (String) $d.p.get( BS.DIALOG.cursorColume ) );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.cursor );
		}
	} );
	attr.put( BS.DIALOG.cursorColume, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			String v = null;
			if( $v instanceof String ) v = (String) $v;
			else if( $v instanceof Integer && $d.p.containsKey( BS.DIALOG.cursor ) )
				v = ( (Cursor) $d.p.get( BS.DIALOG.cursor ) ).getColumnNames()[(Integer) $v];
			$d.p.put( BS.DIALOG.cursorColume, v );
			if( $d.p.containsKey( BS.DIALOG.cursor ) && $d.p.containsKey( BS.EVENT.click ) )
				$d.builder.setCursor( (Cursor) $d.p.get( BS.DIALOG.cursor ), (DialogInterface.OnClickListener) $d.p.get( BS.EVENT.click ), v );
			return v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.cursorColume );
		}
	} );
	attr.put( BS.EVENT.yesDialog, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) E.listener( $v, BS.EVENT.clickDialog );
			$d.builder.setPositiveButton( $d.p.containsKey( BS.DIALOG.yes ) ? (String) $d.p.get( BS.DIALOG.yes ) : "confirm", v );
			$d.p.put( BS.EVENT.yesDialog, v );
			return v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.EVENT.yesDialog );
		}
	} );
	attr.put( BS.EVENT.noDialog, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) E.listener( $v, BS.EVENT.clickDialog );
			$d.builder.setNegativeButton( $d.p.containsKey( BS.DIALOG.no ) ? (String) $d.p.get( BS.DIALOG.no ) : "cancel", v );
			$d.p.put( BS.EVENT.noDialog, v );
			return v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.EVENT.noDialog );
		}
	} );
	attr.put( BS.EVENT.okDialog, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) E.listener( $v, BS.EVENT.clickDialog );
			$d.builder.setNeutralButton( $d.p.containsKey( BS.DIALOG.ok ) ? (String) $d.p.get( BS.DIALOG.ok ) : "ok", v );
			$d.p.put( BS.EVENT.okDialog, v );
			return v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.EVENT.okDialog );
		}
	} );
	attr.put( BS.DIALOG.yes, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( BS.DIALOG.yes, $v );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.yes );
		}
	} );
	attr.put( BS.DIALOG.no, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( BS.DIALOG.no, $v );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.no );
		}
	} );
	attr.put( BS.DIALOG.ok, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( BS.DIALOG.ok, $v );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.ok );
		}
	} );
	attr.put( BS.DIALOG.view, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.builder.setView( (View) $v );
			$d.p.put( BS.DIALOG.view, $v );
			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.view );
		}
	} );
	attr.put( BS.DIALOG.array, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.p.put( BS.DIALOG.array, $v );
			if( $d.p.containsKey( BS.EVENT.clickDialog ) ){
				BS.log( "들어왔나" );
				BS.log( ( (String[]) $v ).toString() );
				$d.builder.setItems( (String[]) $v, (DialogInterface.OnClickListener) $d.p.get( BS.EVENT.clickDialog ) );
			}

			return $v;
		}

		public Object g( final bsDialog $d ){
			return $d.p.get( BS.DIALOG.array );
		}
	} );
	attr.put( BS.DIALOG.show, new Iattr<bsDialog>(){
		public Object s( final bsDialog $d, final Object $v ){
			$d.show();
			return null;
		}

		public Object g( final bsDialog $d ){
			$d.show();
			return null;
		}
	} );
	attr.put( BS.EVENT.clickDialog, new Iattr<bsDialog>(){
		public Object g( final bsDialog $d ){
			return $d.p.get( BS.EVENT.clickDialog );
		}

		public Object s( final bsDialog $d, final Object $v ){
			DialogInterface.OnClickListener v = $v == null ? null : (DialogInterface.OnClickListener) E.listener( $v, BS.EVENT.clickDialog );
			if( v != null ){
				$d.p.put( BS.EVENT.clickDialog, v );
				if( $d.p.containsKey( BS.DIALOG.adapter ) )
					$d.builder.setAdapter( (ListAdapter) $d.p.get( BS.DIALOG.adapter ), v );
				else if( $d.p.containsKey( BS.DIALOG.array ) )
					$d.builder.setItems( (String[]) $d.p.get( BS.DIALOG.array ), v );
				else if( $d.p.containsKey( BS.DIALOG.cursor ) && $d.p.containsKey( BS.DIALOG.cursorColume ) )
					$d.builder.setCursor( (Cursor) $d.p.get( BS.DIALOG.cursor ), v, (String) $d.p.get( BS.DIALOG.cursorColume ) );

			}
			return v;
		}
	} );
}

}