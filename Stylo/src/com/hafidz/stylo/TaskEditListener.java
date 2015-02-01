/**
 * 
 */
package com.hafidz.stylo;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hafidz.stylo.model.TaskManager;

/**
 * @author hafidz
 * 
 */
public class TaskEditListener implements OnClickListener {

	private ScrollView stickyLayout;
	private int taskId;
	private Dialog editDialog;

	public TaskEditListener(int taskId, ScrollView stickyLayout,
			Dialog editDialog) {
		this.stickyLayout = stickyLayout;
		this.taskId = taskId;
		this.editDialog = editDialog;
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

		// update manager
		TaskManager.obtainLock(taskId);
		TaskManager.updateTask(taskId, editTitle.getText().toString(), editDesc
				.getText().toString());
		TaskManager.releaseLock(taskId);

		editDialog.dismiss();

	}
}
