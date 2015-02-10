/**
 * 
 */
package com.hafidz.stylo;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

/**
 * @author hafidz
 * 
 */
public class TaskEditListener implements OnClickListener {

	private ScrollView stickyLayout;
	private String taskId;
	private Dialog editDialog;
	private Context context;

	public TaskEditListener(Context context, String taskId,
			ScrollView stickyLayout, Dialog editDialog) {
		this.stickyLayout = stickyLayout;
		this.taskId = taskId;
		this.editDialog = editDialog;
		this.context = context;
	}

	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	// switch (which) {
	// // save
	// case DialogInterface.BUTTON_POSITIVE:
	//
	// Dialog editDialog = (Dialog) dialog;
	// EditText editTitle = (EditText) editDialog
	// .findViewById(R.id.taskEditTitle);
	// EditText editDesc = (EditText) editDialog
	// .findViewById(R.id.taskEditDesc);
	//
	// // validation
	// if (editTitle.getText() == null
	// || editTitle.getText().toString().trim().isEmpty()) {
	// editTitle.setError("Please insert a title.");
	// return;
	// }
	// if (editDesc.getText() == null
	// || editDesc.getText().toString().trim().isEmpty()) {
	// editTitle.setError("Please insert a description.");
	// return;
	// }
	//
	// // TextView taskDetailTitle = (TextView) stickyLayout
	// // .findViewById(R.id.taskDetailTitle);
	// // taskDetailTitle.setText(editTitle.getText());
	//
	// TextView taskDetailDesc = (TextView) stickyLayout
	// .findViewById(R.id.taskDetailDesc);
	//
	// taskDetailDesc.setText(editDesc.getText());
	//
	// // update manager
	// TaskManager.obtainLock(taskId);
	// TaskManager.updateTask(taskId, editTitle.getText().toString(),
	// editDesc.getText().toString());
	// TaskManager.releaseLock(taskId);
	//
	// break;
	//
	// default:
	// break;
	// }
	//
	// }

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

		// TextView taskDetailTitle = (TextView) stickyLayout
		// .findViewById(R.id.taskDetailTitle);
		// taskDetailTitle.setText(editTitle.getText());

		TextView taskDetailDesc = (TextView) stickyLayout
				.findViewById(R.id.taskDetailDesc);

		taskDetailDesc.setText(editDesc.getText());

		try {
			// update manager
			TaskManager.obtainLock(taskId);
			TaskManager.updateTask(context, taskId, editTitle.getText().toString(),
					editDesc.getText().toString(),
					(RelativeLayout) Util.whiteboardLayout.findViewWithTag(taskId));
			TaskManager.releaseLock(taskId);

			editDialog.dismiss();
		} catch (ParseException e) {
			Util.showError(context, "Problem updating the server.");
		}

	}
}
