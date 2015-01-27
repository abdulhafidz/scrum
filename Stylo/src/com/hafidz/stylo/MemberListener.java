package com.hafidz.stylo;

import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public class MemberListener implements OnDragListener, OnLongClickListener {

	@Override
	public boolean onDrag(View v, DragEvent event) {
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_ENDED:
			v.setVisibility(View.VISIBLE);
			return true;

		case DragEvent.ACTION_DRAG_LOCATION:
			return true;
		}

		return false;
	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	//
	// case MotionEvent.ACTION_DOWN:
	//
	// System.out.println("* * * * * start touch member");
	//
	// // shadow
	// View.DragShadowBuilder shadow = new DragShadowBuilder(v);
	//
	// // hide ori post it
	// // v.setVisibility(View.INVISIBLE);
	//
	// // start drag
	// v.startDrag(null, shadow, v, 0);
	// break;
	//
	// }
	//
	// return true;
	// }

	@Override
	public boolean onLongClick(View v) {
		System.out.println("* * * * * start touch member");

		// shadow
		View.DragShadowBuilder shadow = new DragShadowBuilder(v);

		// hide ori post it
		v.setVisibility(View.INVISIBLE);

		// start drag
		v.startDrag(null, shadow, v, 0);

		return true;
	}

}