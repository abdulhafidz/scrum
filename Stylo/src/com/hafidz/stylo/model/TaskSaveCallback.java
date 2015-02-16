/**
 * 
 */
package com.hafidz.stylo.model;

import android.app.Activity;
import android.content.Context;

import com.hafidz.stylo.MainActivity;
import com.hafidz.stylo.Util;
import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * @author hafidz
 * 
 */
public class TaskSaveCallback extends SaveCallback {

	// private Task task;
	private Context context;
	private String taskId;

	public TaskSaveCallback(Context context, String taskId) {
		super();
		// this.task = task;
		this.context = context;
		this.taskId = taskId;
	}

	@Override
	public void done(ParseException error) {
		if (error != null) {
			((Activity) context).runOnUiThread(new UIThreadError(error,
					(MainActivity) context));

		} else {

			TaskManager.push(taskId, TaskManager.PUSH_ACTION_CREATE,
					"New empty task created.");

			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Util.stopLoading();
					// Util.showSuccess(context, "Task (" + task.getTitle()
					// + ") saved to the server.");
					Util.showSuccess(context, "Task saved to the server.");

				}
			});

		}

	}

	private class UIThreadError implements Runnable {
		private ParseException error;
		private MainActivity mainActivity;

		public UIThreadError(ParseException error, MainActivity mainActivity) {
			this.error = error;
			this.mainActivity = mainActivity;

		}

		public void run() {
			Util.stopLoading();
			// Util.whiteboardLayout.removeView(Util.whiteboardLayout
			// .findViewWithTag(task.getId()));

			// just simply refresh all stickers
			mainActivity.reloadStickers();

			Util.showError(context,
					"Problem saving task to server. - " + error.getCode()
							+ ": " + error.getLocalizedMessage());
		}
	}

}
