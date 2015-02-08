/**
 * 
 */
package com.hafidz.stylo;

import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;

/**
 * @author hafidz
 * 
 */
public class OwnerStickerListener implements OnLongClickListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {
		// shadow
		View.DragShadowBuilder shadow = new DragShadowBuilder(v);

		// hide ori post it
		v.setVisibility(View.INVISIBLE);

		// start drag
		v.startDrag(null, shadow, v, 0);

		return true;
	}

	// @Override
	// public boolean onDrag(View v, DragEvent event) {
	// System.out.println("xxx xxx xxx xxx owner sticker , event.getAction() = = = = = = = = = = = "
	// + event.getAction());
	// return true;
	// }

	// /*
	// * (non-Javadoc)
	// *
	// * @see android.view.View.OnDragListener#onDrag(android.view.View,
	// * android.view.DragEvent)
	// */
	// @Override
	// public boolean onDrag(View view, DragEvent event) {
	// switch (event.getAction()) {
	//
	// case DragEvent.ACTION_DROP:
	// view.setVisibility(View.VISIBLE);
	// break;
	//
	// default:
	// view.setVisibility(View.VISIBLE);
	// }
	//
	// return true;
	//
	// }

}
