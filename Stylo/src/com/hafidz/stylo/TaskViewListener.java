/**
 * 
 */
package com.hafidz.stylo;

import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.Toast;

/**
 * @author hafidz
 * 
 */
public class TaskViewListener implements OnDragListener {

	private Context context;
	private String taskId;
	private boolean in;
	private String owner;

	public TaskViewListener(Context context, String task, String owner) {
		this.context = context;
		this.taskId = task;
		this.owner = owner;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {

		switch (event.getAction()) {

		// cancel remove owner because dropped in the same layout
		case DragEvent.ACTION_DRAG_ENDED:
			if (in) {
				View owner = (View) event.getLocalState();
				owner.setVisibility(View.VISIBLE);
			} else {

				try {
					TaskManager.freeOwner(context,
							TaskManager.load(context, taskId));
				} catch (ParseException e) {
					View owner = (View) event.getLocalState();
					owner.setVisibility(View.VISIBLE);
					Util.showError(context, "Problem updating the server.");
				}

			}
			return true;
		case DragEvent.ACTION_DRAG_EXITED:
			in = false;
			return true;
		case DragEvent.ACTION_DRAG_ENTERED:
			in = true;
			return true;

		}

		return true;
	}

}
