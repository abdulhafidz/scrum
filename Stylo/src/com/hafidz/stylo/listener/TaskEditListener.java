/**
 * 
 */
package com.hafidz.stylo.listener;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.R.id;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

/**
 * @author hafidz
 * 
 */
public class TaskEditListener implements OnClickListener {

	// private ScrollView taskViewLayout;
	private String taskId;
	private Dialog editDialog;
	private Context context;

	public TaskEditListener(Context context, String taskId, Dialog editDialog) {
		// this.taskViewLayout = taskViewLayout;
		this.taskId = taskId;
		this.editDialog = editDialog;
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		// Dialog editDialog = (Dialog) dialog;
		EditText editTitle = (EditText) editDialog
				.findViewById(R.id.taskEditTitle);
		EditText editDesc = (EditText) editDialog
				.findViewById(R.id.taskEditDesc);

		// validation
		if (editTitle.getText() == null
				|| editTitle.getText().toString().trim().isEmpty()) {
			editTitle.setError("Please insert a title.");
			return;
		}
		if (editDesc.getText() == null
				|| editDesc.getText().toString().trim().isEmpty()) {
			editDesc.setError("Please insert a description.");
			return;
		}

		// update server
		Util.startLoading();
		Thread updateThread = new Thread(new UpdateTaskThread(editTitle
				.getText().toString(), editDesc.getText().toString()));
		updateThread.start();

		// update UI
		TaskManager.updateSticker(
				(RelativeLayout) Util.whiteboardLayout.findViewWithTag(taskId),
				editTitle.getText().toString(), editDesc.getText().toString());

		editDialog.dismiss();

	}

	private class UpdateTaskThread implements Runnable {

		private String editTitle;
		private String editDesc;

		public UpdateTaskThread(String editTitle, String editDesc) {
			super();
			this.editTitle = editTitle;
			this.editDesc = editDesc;
		}

		@Override
		public void run() {
			try {
				// update manager
				TaskManager.obtainLock(taskId);
				TaskManager.updateTask(context, taskId, editTitle.toString(),
						editDesc.toString());
				TaskManager.releaseLock(taskId);

				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();
						Util.showSuccess(context, "Task updated to server.");
					}
				});
			} catch (ParseException e) {
				e.printStackTrace();

				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();
						Util.showError(context,
								"Problem saving task to server.");
						Util.reloadStickers();

					}
				});
			}

		}

	}
}
