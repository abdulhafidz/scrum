package com.hafidz.stylo;

import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

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
			GridLayout memberSticker = (GridLayout) event.getLocalState();

			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();
				garbage.setColorFilter(null);

				try {
					TextView tv = (TextView) memberSticker
							.findViewById(R.id.memberName);
					String name = tv.getText().toString();

					MemberManager.obtainLock(name);
					MemberManager.remove(context, name);
					MemberManager.releaseLock(name);
				} catch (ParseException e) {
					Util.showError(context, "Problem updating server.");
				}

				break;

			case DragEvent.ACTION_DRAG_ENTERED:

				garbage.setColorFilter(Color.parseColor("#F44336"));

				memberSticker = (GridLayout) event.getLocalState();

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
				garbage.setColorFilter(null);

				RelativeLayout task = (RelativeLayout) event.getLocalState();

				String id = (String) task.getTag();

				try {
					TaskManager.obtainLock(id);
					TaskManager.remove(context, id,
							(RelativeLayout) Util.whiteboardLayout
									.findViewWithTag(id));
					TaskManager.releaseLock(id);
				} catch (ParseException e) {
					Util.showError(context, "Problem updating server.");
				}

				break;

			case DragEvent.ACTION_DRAG_ENTERED:

				garbage.setColorFilter(Color.parseColor("#F44336"));
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
