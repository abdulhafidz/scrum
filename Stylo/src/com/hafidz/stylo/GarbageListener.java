package com.hafidz.stylo;

import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.TaskManager;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GarbageListener implements OnDragListener {

	private Context context;

	public GarbageListener(Context context) {
		this.context = context;
	}

	@Override
	public boolean onDrag(View arg0, DragEvent event) {

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

			}

			return true;
		}

		// task deleted
		if (event.getLocalState() instanceof RelativeLayout) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();

				RelativeLayout task = (RelativeLayout) event.getLocalState();

				TaskManager.obtainLock(task.getId());
				TaskManager.remove(context, task.getId());
				TaskManager.releaseLock(task.getId());

				System.out.println(task.getVisibility());
			}

			return true;
		}

		// non member
		return false;
	}

}
