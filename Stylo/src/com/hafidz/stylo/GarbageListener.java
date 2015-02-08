package com.hafidz.stylo;

import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.TaskManager;

import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnHoverListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GarbageListener implements OnDragListener {

	private Context context;

	public GarbageListener(Context context) {
		this.context = context;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		ImageView garbage = (ImageView) v;
		// member deleted
		if (event.getLocalState() instanceof GridLayout) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();

				GridLayout memberSticker = (GridLayout) event.getLocalState();
				TextView tv = (TextView) memberSticker
						.findViewById(R.id.memberName);
				String name = tv.getText().toString();

				MemberManager.obtainLock(name);
				MemberManager.remove(context, name);
				MemberManager.releaseLock(name);

				garbage.setColorFilter(null);

				break;

			case DragEvent.ACTION_DRAG_ENTERED:

				garbage.setColorFilter(Color.RED);
				System.out
						.println("entered = = = = = = = == = = = = = = = = = = == = = = = = == = = ==");
				break;

			case DragEvent.ACTION_DRAG_EXITED:

				garbage.setColorFilter(null);
				System.out
						.println("exited = = = = = = = == = = = = = = = = = = == = = = = = == = = ==");
				break;
			}

			return true;
		}

		// task deleted
		if (event.getLocalState() instanceof RelativeLayout) {

			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();

				RelativeLayout task = (RelativeLayout) event.getLocalState();

				String id = (String) task.getTag();
				TaskManager.obtainLock(id);
				TaskManager.remove(context, id,
						(RelativeLayout) Util.whiteboardLayout
								.findViewWithTag(id));
				TaskManager.releaseLock(id);

				garbage.setColorFilter(null);

				break;

			case DragEvent.ACTION_DRAG_ENTERED:

				garbage.setColorFilter(Color.RED);
				System.out
						.println("entered = = = = = = = == = = = = = = = = = = == = = = = = == = = ==");
				break;

			case DragEvent.ACTION_DRAG_EXITED:

				garbage.setColorFilter(null);
				System.out
						.println("exited = = = = = = = == = = = = = = = = = = == = = = = = == = = ==");
				break;
			}

			return true;
		}

		// non member
		return false;
	}
}
