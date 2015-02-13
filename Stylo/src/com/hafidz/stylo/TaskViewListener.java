/**
 * 
 */
package com.hafidz.stylo;

import android.content.Context;
import android.os.AsyncTask;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridLayout;
import android.widget.TextView;

import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

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
			}
			// confirm remove owner because outside
			else {

				// ////////////////////
				AsyncTask<Object, Void, ParseException> bgTask = new AsyncTask<Object, Void, ParseException>() {
					@Override
					protected void onPreExecute() {
						Util.startLoading();
					}

					@Override
					protected ParseException doInBackground(Object... args) {
						try {
							TaskManager.freeOwner(context,
									TaskManager.load(context, taskId));
						} catch (ParseException e) {
							return e;
						}

						return null;
					}

					@Override
					protected void onPostExecute(ParseException exception) {
						Util.stopLoading();
						// no error
						if (exception == null) {
							Util.showSuccess(context,
									"Task updated to the server.");

							// put back owner to member pool
							GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
									.findViewWithTag(owner);
							memberSticker.setVisibility(View.VISIBLE);
							memberSticker.findViewById(R.id.memberName)
									.setVisibility(View.VISIBLE);

							// update task sticker
							View taskSticker = Util.whiteboardLayout
									.findViewWithTag(taskId);
							((TextView) taskSticker
									.findViewById(R.id.taskDetailOwner))
									.setText(null);
						}
						// got error
						else {
							Util.showError(context,
									"Problem updating task to the server.");
							Util.reloadStickers();
						}
					}
				};
				bgTask.execute();

				// update UI
				TextView memberName = (TextView) v
						.findViewById(R.id.taskDetailOwner);

				memberName.setText(null);

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
