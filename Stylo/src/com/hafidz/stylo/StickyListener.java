package com.hafidz.stylo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hafidz.stylo.model.TaskManager;

public class StickyListener implements OnDragListener, OnLongClickListener,
		OnClickListener {

	private Context context;

	public StickyListener(Context context) {
		this.context = context;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if (event.getLocalState() instanceof TextView) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				RelativeLayout task = (RelativeLayout) v;
				TextView memberText = (TextView) event.getLocalState();
				String newOwner = memberText.getText().toString();

				// update task manager
				TaskManager.assignOwner(task.getId(), newOwner);

				System.out.println("* * * * * member " + newOwner
						+ " dropped to task");

				// sticky note
				TextView ownerText = (TextView) task.getChildAt(2);
				String oriOwner = ownerText.getText().toString();
				ownerText.setText(newOwner);

				// hide member
				memberText.setVisibility(View.GONE);

				// if replace existing owner, show replaced owner
				if (oriOwner != null && !oriOwner.trim().isEmpty()
						&& !oriOwner.equals(newOwner)) {
					// TODO
				}

			}

			return true;
		}

		// non member
		return false;
	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	//
	// case MotionEvent.ACTION_DOWN:
	//
	// System.out.println("* * * * * start touch");
	//
	// // shadow
	// View.DragShadowBuilder shadow = new DragShadowBuilder(v);
	//
	// // hide ori post it
	// v.setVisibility(View.INVISIBLE);
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
		System.out.println("* * * * * start on long click");

		// shadow
		View.DragShadowBuilder shadow = new DragShadowBuilder(v);

		// hide ori post it
		v.setVisibility(View.INVISIBLE);

		// start drag
		v.startDrag(null, shadow, v, 0);

		return true;
	}

	@Override
	public void onClick(View v) {
		System.out.println("* * * * * * * * * * on click sticky");

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		builder.setView(inflater.inflate(R.layout.sticky_layout, null));

		AlertDialog dialog = builder.create();

		dialog.show();
	}

}